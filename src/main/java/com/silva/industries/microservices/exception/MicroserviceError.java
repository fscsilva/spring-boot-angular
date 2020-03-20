package com.silva.industries.microservices.exception;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public final class MicroserviceError {
    private final String errorCode;
    private final UUID id = UUID.randomUUID();
    private final String message;
    private final String moreInfo;
    private final Long time = System.currentTimeMillis();
    private final String userMessage;
}
