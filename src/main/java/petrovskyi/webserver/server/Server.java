package petrovskyi.webserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.server.factory.WebServerThreadFactory;
import petrovskyi.webserver.session.SessionRegistry;
import petrovskyi.webserver.util.PropertyHolder;
import petrovskyi.webserver.web.handler.RequestHandler;
import petrovskyi.webserver.webapp.WebAppDirector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private ApplicationRegistry applicationRegistry = new ApplicationRegistry();
    private SessionRegistry sessionRegistry = new SessionRegistry();
    private volatile boolean isRunning = true;

    private PropertyHolder propertyHolder;
    private ExecutorService requestHandlerThreadPool;
    private ServerSocket serverSocket;

    public Server(PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    public void start() {
        LOG.debug("Server starts");

        int threadCount = propertyHolder.getInt("server.thread_count");
        WebServerExceptionHandler exceptionHandler = new WebServerExceptionHandler();
        WebServerThreadFactory webServerThreadFactory =
                new WebServerThreadFactory(exceptionHandler, "RequestHandler", true);
        requestHandlerThreadPool = Executors.newFixedThreadPool(threadCount, webServerThreadFactory);

        WebAppDirector webAppDirector = new WebAppDirector(applicationRegistry);

        webAppDirector.manageAtStartup();

        Thread webAppDirectorThread = new Thread(webAppDirector::manage);
        webAppDirectorThread.setDaemon(true);
        webAppDirectorThread.setName("WebAppDirector");
        webAppDirectorThread.start();

        try {
            serverSocket = new ServerSocket(propertyHolder.getInt("server.port"));
            LOG.info("Server is running on {}", serverSocket.getLocalSocketAddress());
            while (true) {
                Socket accept = serverSocket.accept();
                if (!isRunning) {
                    break;
                }

                requestHandlerThreadPool.execute(new RequestHandler(accept, applicationRegistry, sessionRegistry));
            }
        } catch (IOException e) {
            LOG.error("Error while starting the server", e);
            throw new RuntimeException("Error while starting the server", e);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOG.error("Error while closing server socket", e);
                throw new RuntimeException("Error while closing server socket", e);
            }
        }
    }

    public void stop() {
        LOG.debug("Server is stopping now");
        isRunning = false;

        applicationRegistry.cleanAll();

        requestHandlerThreadPool.shutdown();
        try {
            if (!requestHandlerThreadPool.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                requestHandlerThreadPool.shutdownNow();
                LOG.debug("Trying to stop the server with shutdownNow");
            }
        } catch (InterruptedException e) {
            LOG.error("Error while stopping the server", e);
            requestHandlerThreadPool.shutdownNow();
        }

        try {
            //create new Socket to start process accept() method of serverSocket, thus interrupt it
            new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort());
        } catch (IOException e) {
            LOG.error("Error trying to stop server socket", e);
            throw new RuntimeException("Error trying to stop server socket", e);
        }

        LOG.info("Server is " + (requestHandlerThreadPool.isShutdown() ? "shutdown" : "still running"));
    }


    class WebServerExceptionHandler implements Thread.UncaughtExceptionHandler {
        private final Logger LOG = LoggerFactory.getLogger(getClass());

        @Override
        public void uncaughtException(Thread thread, Throwable t) {
            LOG.error("Uncaught exception is detected! {} at {}", t.getCause(), Arrays.toString(t.getCause().getStackTrace()));
        }
    }
}