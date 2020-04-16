package petrovskyi.webserver;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.server.Server;
import petrovskyi.webserver.util.PropertyHolder;
import petrovskyi.webserver.util.YamlReader;
import sun.misc.Signal;

import java.io.FileNotFoundException;


@Slf4j
class Starter {
    public static void main(String[] args) {
        log.info("Starting the main method");

        PropertyHolder propertyHolder = initializePropertyHolder(args);

        Server server = new Server(propertyHolder);

        Signal.handle(new Signal("TERM"), signal -> {
            log.info("Terminate signal {} ({}) interrupted program execution", signal.getName(), signal.getNumber());
            server.stop();
        });

        server.start();
    }

    private static PropertyHolder initializePropertyHolder(String[] args) {
        int indexOfConfig = -1;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-conf")) {
                indexOfConfig = i;
                break;
            }
        }

        YamlReader yamlReader = new YamlReader();

        if (indexOfConfig != -1 && args.length > 1) {
            String configFileName = args[indexOfConfig + 1];
            log.debug("Config file {} was passed as an argument to Main method", configFileName);
            try {
                yamlReader.readPropertyFileFromFS(configFileName);
            } catch (FileNotFoundException e) {
                log.error("Error! Cannot read the properties from {}", configFileName, e);
                throw new RuntimeException("Error! Cannot read the properties from " + configFileName, e);
            }
        } else {
            log.debug("Reading properties from default config file");
            yamlReader.readPropertyFileFromResources("web_server_properties.yml");
        }

        return new PropertyHolder(yamlReader.getProperties());
    }
}