package petrovskyi.webserver.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyHolderTest {
    private PropertyHolder propertyHolder = new PropertyHolder(new HashMap<>());

    @BeforeEach
    void setUp() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("test1", "hello");

        Map<String, Object> childMap = new HashMap<>();
        childMap.put("test21", 3000);
        childMap.put("test22", "how are you?");

        properties.put("test2", childMap);

        propertyHolder.setProperties(properties);
    }

    @Test
    void separateAndGetProperty() {
        String value1 = (String) propertyHolder.separateAndGetProperty("test1");
        int value21 = (int) propertyHolder.separateAndGetProperty("test2.test21");
        String value22 = (String) propertyHolder.separateAndGetProperty("test2.test22");

        assertEquals("hello", value1);
        assertEquals(3000, value21);
        assertEquals("how are you?", value22);

    }

    @Test
    void getInt() {
        assertEquals(3000, (int) propertyHolder.getInt("test2.test21"));
    }

    @Test
    void getString() {
        assertEquals("hello", propertyHolder.getString("test1"));
        assertEquals("how are you?", propertyHolder.getString("test2.test22"));
    }


    @Test
    void getProperty() {
        Map<String, Object> test2Map = (Map<String, Object>) propertyHolder.getProperty("test2");
        assertEquals(2, test2Map.size());
        assertEquals(3000, test2Map.get("test21"));
        assertEquals("how are you?", test2Map.get("test22"));
    }
}