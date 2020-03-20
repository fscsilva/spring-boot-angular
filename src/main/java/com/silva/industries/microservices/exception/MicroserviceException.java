package com.silva.industries.microservices.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MicroserviceException extends RuntimeException {

    private String errorCode;
    private Integer httpStatus;
    private String moreInfo;
    private String message;
    private String userMessage;

    public MicroserviceException(String errorMessage) {
        super(errorMessage);
    }

    public MicroserviceException(Throwable throwable) {
        super(throwable);
    }

    public MicroserviceException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    public MicroserviceException toError() {
        return MicroserviceException
                .builder()
                .errorCode(this.errorCode)
                .message(this.getMessage())
                .moreInfo(this.moreInfo)
                .userMessage(this.userMessage)
                .build();
    }
}
