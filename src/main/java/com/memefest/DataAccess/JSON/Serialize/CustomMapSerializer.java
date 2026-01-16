package com.memefest.DataAccess.JSON.Serialize;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public class CustomMapSerializer extends JsonSerializer<Map<Object,Object>> implements ContextualSerializer{

    private Class<?> keyAs;

    private Class<?> contentAs;


    @Override
    public void serialize(Map<Object,Object> fromValues, JsonGenerator gen,SerializerProvider provider){
        try {
            gen.writeStartArray();
            ListIterator<Map.Entry<Object,Object>> values = fromValues.entrySet().stream().toList().listIterator();
            while (values.hasNext()) {
                Entry<Object,Object> value = values.next();
                gen.writeStartObject();
                    gen.writeObjectField("key", value.getKey());
                    gen.writeObjectField("value", value.getValue());
                gen.writeEndObject();
            }
            gen.writeEndArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider context, BeanProperty props) throws JsonMappingException{
        JsonSerialize jsonDeserialize = props.getAnnotation(JsonSerialize.class);
        this.keyAs = jsonDeserialize.keyAs();
        this.contentAs = jsonDeserialize.contentAs();
        return this;
    }
}
