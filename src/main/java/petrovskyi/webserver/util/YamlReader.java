package petrovskyi.webserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

class YamlReader {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private String propertyFileName;

    YamlReader(String propertyFileName) {
        LOG.info("Prepare to read property file {}", propertyFileName);
        this.propertyFileName = propertyFileName;
    }

    <T> T getProperty(Class<T> type) {
        LOG.info("Read properties for class {}", type);

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(propertyFileName);
        return yaml.loadAs(inputStream, type);
    }
}