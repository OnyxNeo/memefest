package com.memefest.Jaxrs.Providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

@Provider
@Consumes("application/json")
public class JacksonReader implements MessageBodyReader<Object>{

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public  boolean isReadable(Class<?> object, Type genericType, Annotation[] annotations,
                        MediaType mediaType){        
        //TypeFactory.defaultInstance().constructType(object);
        //return mapper.canDeserialize(TypeFactory.defaultInstance().constructType(object));
        return true;
    }

    @Override
    public Object readFrom(Class<Object> object, Type genericType, Annotation[] annotations,
                            MediaType mediaType, MultivaluedMap<String,String> httpHeaders,
                                InputStream entityStream) throws IOException, WebApplicationException{
        return mapper.readValue(entityStream, genericType.getClass());
    }
}
