package petrovskyi.webserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class YamlReader {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private Map<String, Object> properties;

    public YamlReader(String propertyFileName) {
        LOG.debug("Prepare to read property file {}", propertyFileName);

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(propertyFileName);

        properties = yaml.load(inputStream);
    }

    public YamlReader(InputStream inputStream) {
        LOG.debug("Prepare to read property from inputStream {}", inputStream);

        Yaml yaml = new Yaml();

        properties = yaml.load(inputStream);
    }

    public Map<String, Object> getProperties() {
        LOG.debug("Get all properties");

        return new HashMap<>(properties);
    }


}