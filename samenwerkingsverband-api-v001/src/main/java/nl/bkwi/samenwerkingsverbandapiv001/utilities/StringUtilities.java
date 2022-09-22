package nl.bkwi.samenwerkingsverbandapiv001.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringUtilities {

    private StringUtilities() {}

    public static String objectToJson(Object object) throws JsonProcessingException {
            return new ObjectMapper().writeValueAsString(object);
    }
}
