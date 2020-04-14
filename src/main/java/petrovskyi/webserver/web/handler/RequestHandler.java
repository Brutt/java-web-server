package petrovskyi.webserver.web.handler;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.application.entity.ApplicationInfo;
import petrovskyi.webserver.application.registry.ApplicationRegistry;
import petrovskyi.webserver.session.SessionRegistry;
import petrovskyi.webserver.web.http.request.WebServerServletRequest;
import petrovskyi.webserver.web.http.response.WebServerServletResponse;
import petrovskyi.webserver.web.parser.RequestParser;
import petrovskyi.webserver.web.stream.WebServerOutputStream;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
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
        log.debug("Starting to handle new request");

        try (InputStream inputStream = socket.getInputStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024000);
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(bufferedInputStream));) {

            RequestParser requestParser = new RequestParser(bufferedInputStream, socketReader, sessionRegistry);
            WebServerServletRequest webServerServletRequest = requestParser.parseRequest();

            ApplicationInfo application = applicationRegistry.getApplication(webServerServletRequest.getAppName());
            if (application == null) {
                log.warn("Cannot find an application with the name {}", webServerServletRequest.getAppName());
                return;
            }

            log.info("Request to {} application", application.getName());

            String requestURI = webServerServletRequest.getRequestURI();
            log.info("Request to {} endpoint", requestURI);

            HttpServlet httpServlet = getHttpServlet(application, requestURI);
            if (httpServlet == null) {
                log.warn("Cannot find a servlet by URI {}", requestURI);
                return;
            }
            webServerServletRequest.setServletContext(httpServlet.getServletContext());

            WebServerOutputStream webServerOutputStream = new WebServerOutputStream(socket.getOutputStream(),
                    webServerServletRequest.getSession());
            webServerOutputStream.setAppName(application.getName());
            try (WebServerServletResponse webServerServletResponse = new WebServerServletResponse(webServerOutputStream);) {
                List<Filter> filters = getFilters(application, requestURI);

                for (Filter filter : filters) {
                    filter.doFilter(webServerServletRequest, webServerServletResponse, (servletRequest, servletResponse) -> {
                    });
                }

                if (!webServerOutputStream.onlyHeaders) {
                    httpServlet.service(webServerServletRequest, webServerServletResponse);
                }
            }
        } catch (Exception e) {
            log.error("Error while handling request", e);
            throw new RuntimeException("Error while handling request", e);
        }
    }

    private HttpServlet getHttpServlet(ApplicationInfo application, String requestURI) {
        log.debug("Getting servlet from application {} for uri {}", application, requestURI);
        HttpServlet httpServlet = application.getUrlToServlet().get(requestURI);
        /*
         Servlet 2.5 specification SRV.11:
         The container will recursively try to match the longest path-prefix.
         This is done by stepping down the path tree a directory at a time, using the ’/’ character as a path separator.
         The longest match determines the servlet selected.
        */
        int longestMapping = 999;
        if (httpServlet == null) {
            for (String key : application.getUrlToServlet().keySet()) {
                if (key.endsWith("*") && requestURI.startsWith(key.replace("*", ""))) {
                    int lengthOfMapping = requestURI.replace(key.replace("*", ""), "").length();
                    if (longestMapping > lengthOfMapping) {
                        httpServlet = application.getUrlToServlet().get(key);
                    }
                    longestMapping = lengthOfMapping;
                }
            }
        }
        return httpServlet;
    }

    List<Filter> getFilters(ApplicationInfo application, String requestURI) {
        log.debug("Getting filters from application {} for uri {}", application, requestURI);
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