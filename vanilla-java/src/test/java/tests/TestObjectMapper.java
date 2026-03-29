package tests;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class TestObjectMapper {

    @Test
    void testJacksonMapNullBehavior() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        Map<String, String> testMap = new HashMap<>();
        testMap.put("Identifier_Passport", "1234");
        testMap.put("Identifier_Null_1", null);
        testMap.put("Identifier_Null_2", null);

        String json = mapper.writeValueAsString(testMap);
        System.out.println("Serialized JSON: " + json);

        // With Jackson 2.16.0: {"Identifier_Passport":"1234","Identifier_Null_1":null,"Identifier_Null_2":null}
        // With Jackson 2.17.2: {"Identifier_Passport":"1234"}
    }
}
