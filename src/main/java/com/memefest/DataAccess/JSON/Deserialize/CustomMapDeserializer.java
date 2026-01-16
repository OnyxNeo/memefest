package com.memefest.DataAccess.JSON.Deserialize;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

public class CustomMapDeserializer extends JsonDeserializer<Map<Object,Object>> implements ContextualDeserializer{

    private Class<?> keyAs;

    private Class<?> contentAs;


    @Override
    public Map<Object,Object> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException{
        return deserialize(parser, context,new HashMap<>());
    }

    @Override
    public Map<Object, Object> deserialize(JsonParser parser, DeserializationContext context, Map<Object, Object> intoValue)throws IOException, JsonProcessingException{
        JsonNode node = parser.readValueAsTree();
        ObjectCodec codec = parser.getCodec();
        if(node.isArray()){
            node.forEach(entry ->{
                try{
                    JsonNode keyNode = entry.get(0);
                    JsonNode valueNode = entry.get(1);
                    intoValue.put(keyNode.traverse(codec).readValueAs(this.keyAs),
                                valueNode.traverse(codec).readValueAs(this.contentAs));
                } catch (NullPointerException | IOException ex){

                }
            }); 
        }
        return intoValue;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty props) throws JsonMappingException{
        JsonDeserialize jsonDeserialize = props.getAnnotation(JsonDeserialize.class);
        this.keyAs = jsonDeserialize.keyAs();
        this.contentAs = jsonDeserialize.contentAs();
        return this;
    }
    
}
