package petrovskyi.webserver.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Map;

@Slf4j
public class PropertyHolder {
    private Map<String, Object> properties;

    public PropertyHolder(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Integer getInt(String propertyName) {
        log.debug("Get integer property for {}", propertyName);

        Integer intValue = (Integer) separateAndGetProperty(propertyName);
        log.debug("Get value {} for {}", intValue, propertyName);

        return intValue;
    }

    public String getString(String propertyName) {
        log.debug("Get string property for {}", propertyName);

        String stringValue = separateAndGetProperty(propertyName).toString();
        log.debug("Get value {} for {}", stringValue, propertyName);

        return stringValue;
    }

    Object separateAndGetProperty(String propertyName) {
        Object returnValue;

        String[] split = propertyName.split("\\.");
        if (split.length > 1) {
            Map<String, Object> tempProperty = (Map<String, Object>) getProperty(split[0]);
            for (int i = 1; i < split.length - 1; i++) {
                tempProperty = (Map<String, Object>) tempProperty.get(split[i]);
            }
            returnValue = tempProperty.get(split[split.length - 1]);
        } else {
            returnValue = properties.get(propertyName);
        }

        return returnValue;
    }

    public Object getProperty(String propertyName) {
        log.debug("Get object property for {}", propertyName);

        Object value = properties.get(propertyName);
        log.debug("Get value {} for {}", value, propertyName);

        return value;
    }

    //test purposes
    void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}