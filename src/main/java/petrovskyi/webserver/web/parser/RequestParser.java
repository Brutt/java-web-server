package petrovskyi.webserver.web.parser;

import petrovskyi.webserver.web.http.WebServerServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {
    public WebServerServletRequest parseRequest(BufferedReader socketReader) throws IOException {
        WebServerServletRequest request = new WebServerServletRequest();
        String message = socketReader.readLine();
        injectUriAndHttpMethod(request, message);
        injectHeaders(request, socketReader);

        return request;
    }

    void injectUriAndHttpMethod(WebServerServletRequest request, String requestLine) throws IOException {
        String[] firstLine = requestLine.split(" ");
        //HttpMethods.method httMethod = HttpMethods.method.valueOf(firstLine[0]);
        String uri = firstLine[1];

        request.setHttpMethod("GET");
        request.setUri(uri);
    }

    void injectHeaders(WebServerServletRequest request, BufferedReader socketReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String message;
        while (!(message = socketReader.readLine()).equals("")) {
            String key = message.substring(0, message.indexOf(":"));
            String value = message.substring(message.indexOf(":") + 1);
            headers.put(key, value);
        }

        request.setHeaders(headers);
    }
}
