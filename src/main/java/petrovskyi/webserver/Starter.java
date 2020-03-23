package petrovskyi.webserver;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.server.Server;
import petrovskyi.webserver.server.ServerProperties;
import petrovskyi.webserver.util.PropertyRegistry;
import sun.misc.Signal;


@Slf4j
class Starter {
    private static ServerProperties serverProperties = PropertyRegistry.getProperty(ServerProperties.class);

    public static void main(String[] args) {
        log.info("Starting the main method");

        int port = Integer.parseInt(serverProperties.getProperties().get("port"));
        Server server = new Server(port);

        Signal.handle(new Signal("TERM"), signal -> {
            log.info("Terminate signal {} ({}) interrupted program execution", signal.getName(), signal.getNumber());
            server.stop();
        });

        server.start();
    }
}