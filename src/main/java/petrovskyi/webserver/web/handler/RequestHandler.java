package petrovskyi.webserver.web.handler;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.application.entity.ApplicationInfo;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.web.http.WebServerServletRequest;
import petrovskyi.webserver.web.http.WebServerServletResponse;
import petrovskyi.webserver.web.parser.RequestParser;
import petrovskyi.webserver.web.stream.WebServerOutputStream;

import javax.servlet.http.HttpServlet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
public class RequestHandler implements Runnable {
    private Socket socket;
    private ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        log.info("Starting to handle new request");
        try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

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
            try (WebServerServletResponse webServerServletResponse = new WebServerServletResponse(webServerOutputStream);) {
                httpServlet.service(webServerServletRequest, webServerServletResponse);
            }

        } catch (Exception e) {
            log.error("Error while handling request", e);
            throw new RuntimeException("Error while handling request", e);
        }
    }

}