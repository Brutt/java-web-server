package petrovskyi.webserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.util.PropertyRegistry;
import petrovskyi.webserver.webapp.WebAppDirector;
import petrovskyi.webserver.web.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private static ServerProperties serverProperties = PropertyRegistry.getProperty(ServerProperties.class);
    private int port;
    private ExecutorService service;
    private ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        LOG.info("Server starts");

        int threadCount = Integer.parseInt(serverProperties.getProperties().get("thread_count"));
        service = Executors.newFixedThreadPool(threadCount, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("RequestHandler");
            thread.setDaemon(true);

            return thread;
        });

        WebAppDirector webAppDirector = new WebAppDirector();

        webAppDirector.manageAtStartup();

        service.execute(() -> {
            Thread currentThread = Thread.currentThread();
            currentThread.setName("WebAppDirector");
            webAppDirector.manage();
        });

        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket accept = serverSocket.accept();
                if (!isRunning) {
                    break;
                }
                service.execute(new RequestHandler(accept));
            }

        } catch (IOException e) {
            LOG.error("Error while starting the server", e);
            throw new RuntimeException("Error while starting the server", e);
        }

        LOG.debug("Server stopped");
    }

    public void stop() {
        LOG.info("Server is stopping now");
        isRunning = false;

        applicationRegistry.destroyAllApplications();

        service.shutdown();
        try {
            if (!service.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                service.shutdownNow();
                LOG.info("Trying to stop the server with shutdownNow");
            }
        } catch (InterruptedException e) {
            LOG.error("Error while stopping the server", e);
            service.shutdownNow();
        }

        try {
            new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort()).close();
        } catch (IOException e) {
            LOG.error("Error while trying to close server socket", e);
            throw new RuntimeException("Error while trying to close server socket", e);
        }

        LOG.info("Server is " + (service.isShutdown() ? "shutdown" : "still running"));
    }
}