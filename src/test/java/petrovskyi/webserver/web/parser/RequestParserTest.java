package petrovskyi.webserver.web.parser;

import org.junit.jupiter.api.Test;
import petrovskyi.webserver.web.http.HttpMethod;
import petrovskyi.webserver.web.http.request.WebServerServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {

    @Test
    void testInjectData() {
        WebServerServletRequest request = new WebServerServletRequest();
        String data = "GET /test/hello HTTP/1.1";

        RequestParser requestParser = new RequestParser();
        requestParser.injectData(request, data);

        assertEquals(HttpMethod.GET.name(), request.getMethod());
        assertEquals("test", request.getAppName());
        assertEquals("/hello", request.getRequestURI());
    }
}