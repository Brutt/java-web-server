package petrovskyi.webserver.web.handler;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.application.entity.ApplicationInfo;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.web.http.WebServerServletRequest;
import petrovskyi.webserver.web.http.WebServerServletResponse;
import petrovskyi.webserver.web.parser.RequestParser;
import petrovskyi.webserver.web.stream.WebServerOutputStream;

import javax.servlet.http.HttpServlet;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
public class RequestHandler implements Runnable, AutoCloseable {
    private Socket socket;
    private BufferedReader socketReader;
    private BufferedOutputStream socketWriter;
    private ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        log.info("Starting to handle new request");
        try {
            socketWriter = new BufferedOutputStream(socket.getOutputStream());
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            RequestParser requestParser = new RequestParser();
            WebServerServletRequest webServerServletRequest = requestParser.parseRequest(socketReader);

            ApplicationInfo application = applicationRegistry.getApplication(webServerServletRequest.getAppName());
            if (application == null) {
                log.info("Cannot find an application with the name {}", webServerServletRequest.getAppName());
                return;
            }

            log.info("Request to {} application", application);

            String requestURI = webServerServletRequest.getRequestURI();
            log.info("Request to {} endpoint", requestURI);

            HttpServlet httpServlet = application.getUrlToServlet().get(requestURI);
            if (httpServlet == null) {
                log.info("Cannot find a servlet by URI {}", requestURI);
                return;
            }

            WebServerOutputStream webServerOutputStream = new WebServerOutputStream(socket.getOutputStream());
            webServerOutputStream.startGoodOutputStream();
            WebServerServletResponse webServerServletResponse = new WebServerServletResponse(webServerOutputStream);

            httpServlet.service(webServerServletRequest, webServerServletResponse);
            webServerServletResponse.flush();

        } catch (Exception e) {
            log.error("Error while handling request", e);
            throw new RuntimeException("Error while handling request", e);
        } finally {
            try {
                socketWriter.close();
            } catch (IOException e) {
                log.error("Error while closing socket", e);
                throw new RuntimeException("Error while closing socket", e);
            }
        }
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}