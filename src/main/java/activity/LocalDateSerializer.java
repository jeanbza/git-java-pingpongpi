// Credit: http://blog.chris-ritchie.com/2014/09/localdate-java-8-custom-serializer.html

package Activity;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void serialize(LocalDate date, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {

        String dateString = date.format(formatter);
        generator.writeString(dateString);
    }
}
