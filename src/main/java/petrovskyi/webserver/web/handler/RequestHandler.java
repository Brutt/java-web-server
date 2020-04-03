package petrovskyi.webserver.web.handler;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.application.entity.ApplicationInfo;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.session.SessionRegistry;
import petrovskyi.webserver.web.http.request.WebServerServletRequest;
import petrovskyi.webserver.web.http.response.WebServerServletResponse;
import petrovskyi.webserver.web.parser.RequestParser;
import petrovskyi.webserver.web.stream.WebServerOutputStream;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RequestHandler implements Runnable {
    private Socket socket;
    private ApplicationRegistry applicationRegistry;
    private SessionRegistry sessionRegistry;

    public RequestHandler(Socket socket, ApplicationRegistry applicationRegistry, SessionRegistry sessionRegistry) {
        this.socket = socket;
        this.applicationRegistry = applicationRegistry;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void run() {
        log.info("Starting to handle new request");
        try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            RequestParser requestParser = new RequestParser(sessionRegistry);
            WebServerServletRequest webServerServletRequest = requestParser.parseRequest(socketReader);

            ApplicationInfo application = applicationRegistry.getApplication(webServerServletRequest.getAppName());
            if (application == null) {
                log.info("Cannot find an application with the name {}", webServerServletRequest.getAppName());
                return;
            }

            log.info("Request to {} application", application);

            String requestURI = webServerServletRequest.getRequestURI();
            log.info("Request to {} endpoint", requestURI);

            HttpServlet httpServlet = getHttpServlet(application, requestURI);
            if (httpServlet == null) {
                log.info("Cannot find a servlet by URI {}", requestURI);
                return;
            }

            WebServerOutputStream webServerOutputStream = new WebServerOutputStream(socket.getOutputStream(),
                    webServerServletRequest.getSession().getId());
            try (WebServerServletResponse webServerServletResponse = new WebServerServletResponse(webServerOutputStream);) {
                List<Filter> filters = getFilters(application, requestURI);

                for (Filter filter : filters) {
                    filter.doFilter(webServerServletRequest, webServerServletResponse, (servletRequest, servletResponse) -> {
                    });
                }

                httpServlet.service(webServerServletRequest, webServerServletResponse);
            }

        } catch (Exception e) {
            log.error("Error while handling request", e);
            throw new RuntimeException("Error while handling request", e);
        }
    }

    private HttpServlet getHttpServlet(ApplicationInfo application, String requestURI) {
        HttpServlet httpServlet = application.getUrlToServlet().get(requestURI);
        if (httpServlet == null) {
            for (String key : application.getUrlToServlet().keySet()) {
                if (key.endsWith("*") && requestURI.startsWith(key.replace("*", ""))) {
                    httpServlet = application.getUrlToServlet().get(key);
                    break;
                }
            }
        }
        return httpServlet;
    }

    private List<Filter> getFilters(ApplicationInfo application, String requestURI) {
        List<Filter> filters = new ArrayList<>();

        if (application.getUrlToFilters().get(requestURI) != null) {
            filters.addAll(application.getUrlToFilters().get(requestURI));
        }

        for (String key : application.getUrlToFilters().keySet()) {
            if (key.endsWith("*") && requestURI.startsWith(key.replace("*", ""))) {
                filters.addAll(application.getUrlToFilters().get(key));
            }
        }

        return filters;
    }

}