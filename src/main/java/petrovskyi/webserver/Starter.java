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

        Server server = new Server();

        Signal.handle(new Signal("TERM"), signal -> {
            log.info("Terminate signal {} ({}) interrupted program execution", signal.getName(), signal.getNumber());
            try {
                server.stop();
            } catch (InterruptedException e) {
                log.error("Error while trying to interrupt program execution", e);
                throw new RuntimeException("Error while trying to interrupt program execution", e);
            }
        });

        int port = Integer.parseInt(serverProperties.getProperties().get("port"));
        server.setPort(port);
        server.start();
    }
}