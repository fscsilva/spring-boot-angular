package com.silva.industries.microservices.web.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.silva.industries.microservices.enumeration.ErrorType;
import com.silva.industries.microservices.exception.InternalServerErrorException;
import com.silva.industries.microservices.exception.MicroserviceError;
import com.silva.industries.microservices.exception.MicroserviceException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import javax.naming.ServiceUnavailableException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.silva.industries.microservices.web.ResponseBody;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MicroserviceException.class)
    protected ResponseEntity<ResponseBody> handleMicroserviceException(@NotNull MicroserviceException ex) {
        return buildErrorResponse(HttpStatus.valueOf(ex.getHttpStatus()), ex.toError());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    protected ResponseEntity<ResponseBody> handleServiceUnavailable(ServiceUnavailableException ex) {
        return buildErrorResponse(INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ResponseBody> handleNoFoundElement(NoSuchElementException ex) {
        return buildErrorResponse(NOT_FOUND, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ResponseBody> handleDateException(IllegalArgumentException ex) {
        return buildErrorResponse(BAD_REQUEST, ex);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<ResponseBody> handleHttpClientError(HttpClientErrorException ex) {
        return buildErrorResponse(BAD_REQUEST, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintException(ConstraintViolationException ex) {
        Map<Path, Set<String>> errorsMap = ex.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(ConstraintViolation::getPropertyPath,
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toSet())));
        return buildValidationErrorResponse(BAD_REQUEST, errorsMap.toString());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildValidationErrorResponse(status, getErrorsFromBindingResult(ex.getBindingResult()));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        return buildValidationErrorResponse(status, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {
        return buildValidationErrorResponse(status, getErrorsFromBindingResult(ex.getBindingResult()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseBody> handleAll(Exception e) {
        InternalServerErrorException internalServerErrorException = new InternalServerErrorException();
        MicroserviceError error = MicroserviceError.builder()
                .errorCode(internalServerErrorException.getErrorCode())
                .message(internalServerErrorException.getMessage())
                .userMessage(internalServerErrorException.getUserMessage())
                .build();
        log.error(e.getMessage(), e);
        return buildErrorResponse(INTERNAL_SERVER_ERROR, error);
    }

    @ExceptionHandler(CompletionException.class)
    protected ResponseEntity<ResponseBody> handleCompletionException(CompletionException ex) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if (rootCause instanceof NoSuchElementException) {
            return handleNoFoundElement((NoSuchElementException) rootCause);
        }

        MicroserviceError error = MicroserviceError.builder()
                .message(rootCause.getMessage())
                .userMessage(ex.getMessage())
                .build();

        return buildErrorResponse(INTERNAL_SERVER_ERROR, error);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException exception = castException(ex.getCause(), InvalidFormatException.class);
            Object value = exception.getValue();
            String objectName = exception.getTargetType().getSimpleName();
            String errorMsg = String.format("Value %s is not valid for %s", value.toString(), objectName);
            return buildValidationErrorResponse(BAD_REQUEST, errorMsg);
        } else if (ex.getCause() instanceof JsonMappingException) {
            JsonMappingException exception = castException(ex.getCause(), JsonMappingException.class);
            String path = exception.getPathReference();
            String message = "There is an error mapping fields [%s]";
            return buildValidationErrorResponse(BAD_REQUEST, String.format(message, path));
        } else {
            return buildValidationErrorResponse(BAD_REQUEST, ex.toString());
        }
    }

    private <T> T castException(Throwable t, Class<T> expectedType) {
        if (expectedType.isInstance(t)) {
            return (T) t;
        } else {
            throw new IllegalStateException(
                    "This exception was previously validated as InvalidFormatException (This should´t happen");
        }
    }

    private ResponseEntity<ResponseBody> buildErrorResponse(HttpStatus httpStatus, MicroserviceError errors) {
        ResponseBody body = ResponseBody.builder().error(errors).status(httpStatus.value()).build();
        return new ResponseEntity<>(body, httpStatus);
    }

    private ResponseEntity<ResponseBody> buildErrorResponse(HttpStatus status, Exception ex) {
        MicroserviceError error = buildMicroserviceError(status, ex);
        ResponseBody body = ResponseBody.builder().error(error).status(status.value()).build();
        return new ResponseEntity<>(body, status);
    }

    private ResponseEntity<Object> buildValidationErrorResponse(HttpStatus status, String devErrorMessage) {
        MicroserviceError error = MicroserviceError.builder()
                .errorCode(ErrorType.DEFAULT_VALIDATION.getCode())
                .message(devErrorMessage)
                .build();
        ResponseBody body = ResponseBody.builder().error(error).status(status.value()).build();
        return new ResponseEntity<>(body, status);
    }

    private MicroserviceError buildMicroserviceError(HttpStatus status, Exception ex) {
        return buildMicroserviceError(status, ex, false);
    }

    private MicroserviceError buildMicroserviceError(HttpStatus status, Exception ex, boolean hasDevErrorMessage) {
        String developerErrorMessage = hasDevErrorMessage ? ExceptionUtils.getRootCause(ex).getMessage() : "";
        return buildMicroserviceError(status, developerErrorMessage);
    }

    private MicroserviceError buildMicroserviceError(HttpStatus status, String developerErrorMessage) {
        return MicroserviceError.builder()
                .errorCode(String.valueOf(status.value()))
                .message(developerErrorMessage)
                .build();
    }

    private String getErrorsFromBindingResult(BindingResult bindingResult) {
        Map<String, Set<String>> errorsMap = bindingResult.getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())));
        errorsMap.putAll(bindingResult.getGlobalErrors().stream()
                .collect(Collectors.groupingBy(ObjectError::getObjectName,
                        Collectors.mapping(ObjectError::getDefaultMessage, Collectors.toSet()))));
        return errorsMap.toString();
    }
}
