package com.silva.industries.microservices.exception;

import com.silva.industries.microservices.enumeration.ErrorType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@NoArgsConstructor
public class InternalServerErrorException extends MicroserviceException {
    private final String errorCode = ErrorType.SERVER_ERROR.getCode();
    private final Integer httpStatus = INTERNAL_SERVER_ERROR.value();
    private final String message = INTERNAL_SERVER_ERROR.getReasonPhrase();
    private String userMessage = "We're unable to retrieve your information, "
            + "so please try again or see a staff member for assistance.";

    public InternalServerErrorException(String userMessage, Throwable e) {
        super(e);
        this.userMessage = userMessage;
    }
}
