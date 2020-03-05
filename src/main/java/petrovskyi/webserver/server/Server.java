package petrovskyi.webserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.webapp.WebAppDirector;
import petrovskyi.webserver.web.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private int port;
    private ExecutorService service;

    public void setPort(int port) {
        this.port = port;
    }

    public void start() {
        LOG.info("Server starts");
        service = Executors.newCachedThreadPool();

        WebAppDirector webAppDirector = new WebAppDirector();
        service.submit(() -> webAppDirector.manage());

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                service.submit(new RequestHandler(serverSocket.accept()));
            }

        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void stop() throws InterruptedException {
        LOG.info("Server is stopping");
        service.shutdown();

        Thread.sleep(1000);

        LOG.info("Server is " + (service.isShutdown() ? "shutdown" : "still running"));

        System.exit(0);
    }
}