package petrovskyi.webserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class YamlReader {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private Map<String, Object> properties;

    public void readPropertyFileFromFS(String fileName) throws FileNotFoundException {
        URL jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        String jarFolderPath = new File(jarLocation.getPath()).getParent();
        File fileWithPropertiesFS = new File(jarFolderPath, fileName);

        LOG.debug("Try to read properties from {}", fileWithPropertiesFS);

        Yaml yaml = new Yaml();

        properties = yaml.load(new FileInputStream(fileWithPropertiesFS));
    }

    public void readPropertyFileFromResources(String fileName) {
        LOG.debug("Prepare to read property file {}", fileName);

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);

        properties = yaml.load(inputStream);
    }

    public Map<String, Object> getProperties() {
        LOG.debug("Get all properties");

        return new HashMap<>(properties);
    }


}