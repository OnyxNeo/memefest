package com.memefest.Jaxrs.Providers;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    public Response toResponse(JsonProcessingException exception){
        exception.printStackTrace();
        return Response.serverError().build();
    }
}
