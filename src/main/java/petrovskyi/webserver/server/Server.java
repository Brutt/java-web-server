package petrovskyi.webserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.session.SessionRegistry;
import petrovskyi.webserver.util.PropertyHolder;
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
    private PropertyHolder propertyHolder;
    private int port;
    private ExecutorService service;
    private ApplicationRegistry applicationRegistry = new ApplicationRegistry();
    private SessionRegistry sessionRegistry = new SessionRegistry();
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;

    public Server(int port, PropertyHolder propertyHolder) {
        this.port = port;
        this.propertyHolder = propertyHolder;
    }

    public void start() {
        LOG.info("Server starts");

        int threadCount = propertyHolder.getInt("server.thread_count");
        service = Executors.newFixedThreadPool(threadCount, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("RequestHandler");
            thread.setDaemon(true);

            return thread;
        });

        WebAppDirector webAppDirector = new WebAppDirector(applicationRegistry);

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
                    serverSocket.close();
                    break;
                }
                service.execute(new RequestHandler(accept, applicationRegistry, sessionRegistry));
            }

        } catch (IOException e) {
            LOG.error("Error while starting the server", e);
            throw new RuntimeException("Error while starting the server", e);
        }

        LOG.debug("Server`s start method over");
    }

    public void stop() {
        LOG.info("Server is stopping now");
        isRunning = false;

        applicationRegistry.cleanAll();

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
            //create new Socket to start process accept() method of serverSocket, thus interrupt it
            new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort());
        } catch (IOException e) {
            LOG.error("Error trying to create new Socket", e);
            throw new RuntimeException("Error trying to create new Socket", e);
        }

        LOG.info("Server is " + (service.isShutdown() ? "shutdown" : "still running"));
    }
}