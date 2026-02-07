package com.memefest.DataAccess.JSON.Deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CustomLocalDateDeserializer extends StdDeserializer<LocalDateTime> {
  private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
  
  public CustomLocalDateDeserializer() {
    this(null);
  }
  
  public CustomLocalDateDeserializer(Class<?> vc) {
    super(vc);
  }
  
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
    String date = parser.getText();
    try {
      return LocalDateTime.ofInstant(formatter.parse(date).toInstant(), ZoneId.systemDefault());
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    } 
  }
}