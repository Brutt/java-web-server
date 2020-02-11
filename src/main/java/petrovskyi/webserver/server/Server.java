package petrovskyi.webserver.server;

import petrovskyi.webserver.handler.RequestHandler;
import petrovskyi.webserver.scanner.WebAppWarScanner;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private ExecutorService service;

    public void setPort(int port) {
        this.port = port;
    }

    public void start() {
        service = Executors.newCachedThreadPool();

        service.submit(new WebAppWarScanner());


        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                service.submit(new RequestHandler(serverSocket.accept()));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void stop() throws InterruptedException {
        service.shutdown();

        Thread.sleep(1000);

        System.out.println("Server is " + (service.isShutdown() ? "shutdown" : "still running"));

        System.exit(0);
    }
}