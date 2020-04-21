package petrovskyi.webserver.server;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.server.exception.WebServerStopException;
import petrovskyi.webserver.server.factory.WebServerThreadFactory;
import petrovskyi.webserver.session.SessionRegistry;
import petrovskyi.webserver.util.PropertyHolder;
import petrovskyi.webserver.web.handler.RequestHandler;
import petrovskyi.webserver.webapp.WebAppDirector;
import petrovskyi.webserver.webapp.webxml.exception.WebXmlNotFoundException;

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

        webAppDirectorFlow(true, exceptionHandler);

        Integer serverPort = propertyHolder.getInt("server.port");
        try (ServerSocket localServerSocket = serverSocket = new ServerSocket(serverPort)) {
            LOG.info("Server is running on {}", localServerSocket.getLocalSocketAddress());
            while (true) {
                Socket accept = localServerSocket.accept();
                if (!isRunning) {
                    break;
                }

                requestHandlerThreadPool.execute(new RequestHandler(accept, applicationRegistry, sessionRegistry));
            }
        } catch (IOException e) {
            LOG.error("Error while starting the server", e);
            throw new RuntimeException("Error while starting the server", e);
        }
    }

    public void stop() {
        LOG.debug("Server is stopping now");

        Thread.currentThread().setUncaughtExceptionHandler(new WebServerExceptionHandler());

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
            throw new WebServerStopException("Error trying to stop server socket", e);
        }

        LOG.info("Server is " + (requestHandlerThreadPool.isShutdown() ? "shutdown" : "still running"));
    }

    private void webAppDirectorFlow(boolean scanAtStartup, WebServerExceptionHandler exceptionHandler) {
        LOG.debug("Start webapp director flow");
        WebAppDirector webAppDirector = new WebAppDirector(applicationRegistry);
        if (scanAtStartup) {
            webAppDirector.manageAtStartup();
        }

        Thread webAppDirectorThread = new Thread(webAppDirector::manage);
        webAppDirectorThread.setDaemon(true);
        webAppDirectorThread.setName("WebAppDirector");
        webAppDirectorThread.setUncaughtExceptionHandler(exceptionHandler);
        webAppDirectorThread.start();
    }

    class WebServerExceptionHandler implements Thread.UncaughtExceptionHandler {
        private final Logger LOG = LoggerFactory.getLogger(getClass());

        @Override
        public void uncaughtException(Thread thread, Throwable t) {
            LOG.error("Uncaught exception is detected! {} at {}", t, Arrays.toString(t.getStackTrace()));

            if (t instanceof WebServerStopException) {
                //exception while stopping server
                System.exit(0);
            } else if (t instanceof WebXmlNotFoundException) {
                //restart webapp flow
                webAppDirectorFlow(false, this);
            }
        }
    }
}