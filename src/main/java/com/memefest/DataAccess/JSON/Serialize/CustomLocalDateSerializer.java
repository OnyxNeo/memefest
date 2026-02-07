package com.memefest.DataAccess.JSON.Serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateSerializer extends StdSerializer<LocalDate> {
  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  
  public CustomLocalDateSerializer() {
    this(null);
  }
  
  public CustomLocalDateSerializer(Class<LocalDate> t) {
    super(t);
  }
  
  public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
    String formattedDateTime = value.format(formatter);
    gen.writeString(formattedDateTime);
  }
}

