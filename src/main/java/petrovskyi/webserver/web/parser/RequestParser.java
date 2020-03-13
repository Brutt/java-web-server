package petrovskyi.webserver.web.parser;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.web.http.HttpMethod;
import petrovskyi.webserver.web.http.WebServerServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RequestParser {
    public WebServerServletRequest parseRequest(BufferedReader socketReader) throws IOException {
        log.debug("Starting to parse request");
        WebServerServletRequest request = new WebServerServletRequest();
        String message = socketReader.readLine();
        injectData(request, message);
        injectHeaders(request, socketReader);

        return request;
    }

    void injectData(WebServerServletRequest request, String requestLine) {
        log.info("Injecting data [{}] into request", requestLine);
        String[] firstLine = requestLine.split(" ");
        String[] uris = firstLine[1].split("/");

        request.setHttpMethod(HttpMethod.valueOf(firstLine[0]));
        request.setAppName(uris[1]);
        request.setUri(firstLine[1].substring(uris[1].length() + 1));
    }

    void injectHeaders(WebServerServletRequest request, BufferedReader socketReader) throws IOException {
        log.info("Injecting headers into request");
        Map<String, String> headers = new HashMap<>();
        String message;
        while (!(message = socketReader.readLine()).equals("")) {
            String key = message.substring(0, message.indexOf(":"));
            String value = message.substring(message.indexOf(":") + 1);
            headers.put(key, value);
            log.debug("Injecting header {} with value {} into request", key, value);
        }

        request.setHeaders(headers);
    }
}
