package com.silva.industries.microservices.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static com.silva.industries.microservices.enumeration.ErrorType.CURRENT_DATA_NOT_FOUND;

@Getter
@NoArgsConstructor
public class CurrentDataNotFoundException extends MicroserviceException {

    private String errorCode = CURRENT_DATA_NOT_FOUND.getCode();
    private String userMessage = "We're unable to retrieve your information,"
            + "so please try again or see a staff member for assistance.";
    private Integer httpStatus = NOT_FOUND.value();
    private String message = "Information not found";

    public CurrentDataNotFoundException(String customErrorMessage) {
        this.message = customErrorMessage;
    }
}
