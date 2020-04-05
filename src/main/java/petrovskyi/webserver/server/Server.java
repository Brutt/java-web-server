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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Server {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private PropertyHolder propertyHolder;
    private ExecutorService requestHandlerThreadPool;
    private ApplicationRegistry applicationRegistry = new ApplicationRegistry();
    private SessionRegistry sessionRegistry = new SessionRegistry();
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;

    public Server(PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    public void start() {
        LOG.info("Server starts");

        int threadCount = propertyHolder.getInt("server.thread_count");
        requestHandlerThreadPool = Executors.newFixedThreadPool(threadCount, createThreadFactory());

        WebAppDirector webAppDirector = new WebAppDirector(applicationRegistry);

        webAppDirector.manageAtStartup();

        Thread webAppDirectorThread = new Thread(webAppDirector::manage);
        webAppDirectorThread.setDaemon(true);
        webAppDirectorThread.setName("WebAppDirector");
        webAppDirectorThread.start();

        try {
            serverSocket = new ServerSocket(propertyHolder.getInt("server.port"));
            while (true) {
                Socket accept = serverSocket.accept();
                if (!isRunning) {
                    serverSocket.close();
                    break;
                }
                requestHandlerThreadPool.execute(new RequestHandler(accept, applicationRegistry, sessionRegistry));
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

        requestHandlerThreadPool.shutdown();
        try {
            if (!requestHandlerThreadPool.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                requestHandlerThreadPool.shutdownNow();
                LOG.info("Trying to stop the server with shutdownNow");
            }
        } catch (InterruptedException e) {
            LOG.error("Error while stopping the server", e);
            requestHandlerThreadPool.shutdownNow();
        }

        try {
            //create new Socket to start process accept() method of serverSocket, thus interrupt it
            new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort());
        } catch (IOException e) {
            LOG.error("Error trying to create new Socket", e);
            throw new RuntimeException("Error trying to create new Socket", e);
        }

        LOG.info("Server is " + (requestHandlerThreadPool.isShutdown() ? "shutdown" : "still running"));
    }

    private ThreadFactory createThreadFactory(){
        return runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("RequestHandler");
            thread.setDaemon(true);

            return thread;
        };
    }
}