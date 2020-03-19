package com.silva.industries.microservices.enumeration;

import lombok.Getter;

@Getter
public enum ErrorType {
    //TODO must change the WE prefix
    AUTHENTICATION_ERROR("WE-0001"),
    DATA_NOT_MATCH("WE-0002"),
    SERVER_ERROR("WE-0003"),
    DEFAULT_VALIDATION("WE-0004"),
    THIRDPARTY_SERVICE_UNAVAILABLE("WE-0005"),
    CURRENT_DATA_NOT_FOUND("WE-0006"),;
    
    private String code;
    
    ErrorType(String code) {
        this.code = code;
    }
}
