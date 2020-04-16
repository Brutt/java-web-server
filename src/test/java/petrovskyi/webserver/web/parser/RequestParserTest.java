package petrovskyi.webserver.web.parser;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import petrovskyi.webserver.web.http.HttpMethod;
import petrovskyi.webserver.web.http.request.WebServerServletRequest;
import petrovskyi.webserver.web.http.session.WebServerSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestParserTest {
    private WebServerServletRequest request;

    @BeforeEach
    void setUp() {
        request = new WebServerServletRequest();
    }

    @Test
    void injectData() {
        String data = "GET /test/hello HTTP/1.1";

        RequestParser requestParser = new RequestParser();
        requestParser.injectData(request, data);

        assertEquals(HttpMethod.GET.name(), request.getMethod());
        assertEquals("test", request.getAppName());
        assertEquals("/hello", request.getRequestURI());
    }

    @Test
    void testFavicon() {
        String data = "GET /favicon.ico HTTP/1.1";

        RequestParser requestParser = new RequestParser();
        requestParser.injectData(request, data);

        assertEquals(HttpMethod.GET.name(), request.getMethod());
        assertNull(request.getAppName());
        assertNull(request.getRequestURI());
    }

    @Test
    void injectBody() throws IOException {
        String test = "name=login&pass=123";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", String.valueOf(test.length()));
        request.setHeaders(headers);

        RequestParser requestParser = new RequestParser();
        requestParser.injectBody(request, reader, null);

        assertEquals("login", request.getParameter("name"));
        assertEquals("123", request.getParameter("pass"));
    }

    @Test
    void injectHeaders() throws IOException {
        String test = "Host: test.com\n" +
                "Cache-Control: no-cache\n\n";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        RequestParser requestParser = new RequestParser();
        requestParser.injectHeaders(request, reader);

        assertEquals("test.com", request.getHeader("Host"));
        assertEquals("no-cache", request.getHeader("Cache-Control"));
    }

    void injectCookieHeader() throws IOException {
        request.setAppName("app1");
        String suffix = DigestUtils.sha1Hex(request.getAppName());
        String sessionCookieName = WebServerSession.SESSIONID + "." + suffix;

        String test = "Cookie: " + sessionCookieName + "=123456\n\n";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        RequestParser requestParser = new RequestParser();
        requestParser.injectHeaders(request, reader);
    }

    @Test
    void injectTempSessionCookie() throws IOException {
        injectCookieHeader();

        RequestParser requestParser = new RequestParser();
        requestParser.injectCookies(request);
        requestParser.injectTempSessionCookie(request);

        assertEquals("123456", request.getTempSessionCookie());
    }

    @Test
    void injectNotFoundInRegistrySession() {
        request.setAppName("app1");
        request.setHeaders(new HashMap<>());

        RequestParser requestParser = new RequestParser();
        requestParser.injectTempSessionCookie(request);

        assertNull(request.getTempSessionCookie());
    }
}