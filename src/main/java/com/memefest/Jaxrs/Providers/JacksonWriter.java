package com.memefest.Jaxrs.Providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces("application/json")
public class JacksonWriter implements MessageBodyWriter<Object>{

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void writeTo(Object target, 
                                Class<?> type, Type genericType,
                                    Annotation[] annotations, 
                                        MediaType mediaType,
                                        MultivaluedMap<String, Object> httpHeaders,
                                        OutputStream outputStream) throws IOException{
        mapper.writeValue(outputStream,target);  
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                                Annotation[] annotations, MediaType mediaType){
        return true;
    }

    @Override
    public long getSize(Object target, 
                                Class<?> type, Type genericType,
                                    Annotation[] annotations, 
                                        MediaType mediaType){
        return -1;
    }

}
