package petrovskyi.webserver.util;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.server.ServerProperties;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PropertyRegistry {
    private static final Map<Class<?>, Object> PROPERTIES = new HashMap<>();

    static {
        ServerProperties serverProperties = new YamlReader("server_props.yml").getProperty(ServerProperties.class);
        register(ServerProperties.class, serverProperties);
    }

    public static <T> T getProperty(Class<T> propertyClass) {
        log.info("Get properties for class {}", propertyClass);

        return propertyClass.cast(PROPERTIES.get(propertyClass));
    }

    private static void register(Class<?> propertyClass, Object properties) {
        log.info("Register properties for class {}", propertyClass);

        PROPERTIES.put(propertyClass, properties);
    }
}