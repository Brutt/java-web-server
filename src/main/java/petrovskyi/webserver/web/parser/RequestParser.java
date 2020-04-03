package petrovskyi.webserver.web.parser;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.session.SessionRegistry;
import petrovskyi.webserver.web.http.HttpMethod;
import petrovskyi.webserver.web.http.request.WebServerServletRequest;
import petrovskyi.webserver.web.http.session.WebServerSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class RequestParser {
    private SessionRegistry sessionRegistry;

    public RequestParser(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public WebServerServletRequest parseRequest(BufferedReader socketReader) throws IOException {
        log.debug("Starting to parse request");
        WebServerServletRequest request = new WebServerServletRequest();
        String message = socketReader.readLine();
        injectData(request, message);
        injectHeaders(request, socketReader);
        injectBody(request, socketReader);
        injectSession(request);

        return request;
    }

    void injectData(WebServerServletRequest request, String requestLine) {
        log.info("Injecting data [{}] into request", requestLine);
        String[] firstLine = requestLine.split(" ");
        String[] uris = firstLine[1].split("/");

        request.setHttpMethod(HttpMethod.valueOf(firstLine[0]));
        if (uris.length > 1) {
            request.setAppName(uris[1]);
            request.setUri(firstLine[1].substring(uris[1].length() + 1));
        }
    }

    void injectHeaders(WebServerServletRequest request, BufferedReader socketReader) throws IOException {
        log.info("Injecting headers into request");
        Map<String, String> headers = new HashMap<>();
        String message;
        while (!(message = socketReader.readLine()).equals("")) {
            String key = message.substring(0, message.indexOf(":"));
            String value = message.substring(message.indexOf(":") + 2);
            headers.put(key, value);
            log.debug("Injecting header {} with value {} into request", key, value);
        }

        headers.put("Cache-Control", "no-store");

        request.setHeaders(headers);
    }

    void injectBody(WebServerServletRequest request, BufferedReader socketReader) throws IOException {

        String contentLength = request.getHeader("Content-Length");
        if (contentLength == null || Integer.parseInt(contentLength) == 0) {
            return;
        }

        char[] buf = new char[Integer.parseInt(contentLength)];
        socketReader.read(buf);

        String params = String.valueOf(buf);

        Map<String, String> parameterMap = new HashMap<>();
        String[] splitParams = params.split("&");
        for (String splitParam : splitParams) {
            String[] keyValue = splitParam.split("=");
            parameterMap.put(keyValue[0], keyValue[1]);
        }

        request.setParameters(parameterMap);
    }

    void injectSession(WebServerServletRequest request) {
        WebServerSession webServerSession = null;
        String cookies = request.getHeader("Cookie");
        if (cookies != null) {
            String[] cookieSplit = cookies.split("; ");
            for (String cookie : cookieSplit) {
                String[] keyValue = cookie.split("=");
                if (keyValue[0].equals("SESSIONID")) {
                    webServerSession = sessionRegistry.getSession(request.getAppName(), keyValue[1]);
                    break;
                }
            }
        }

        if (webServerSession == null) {
            webServerSession = new WebServerSession(UUID.randomUUID().toString());
            sessionRegistry.register(request.getAppName(), webServerSession);
        }
        request.setWebServerSession(webServerSession);
    }
}
