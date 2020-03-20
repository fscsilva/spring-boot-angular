package com.silva.industries.microservices.web;


import com.silva.industries.microservices.exception.MicroserviceError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public final class ResponseBody<T> {

    private final MicroserviceError error;
    private final T payload;
    private final int status;
}
