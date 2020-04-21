package petrovskyi.webserver.web.parser;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import petrovskyi.webserver.web.http.HttpMethod;
import petrovskyi.webserver.web.http.request.WebServerServletRequest;
import petrovskyi.webserver.web.http.session.WebServerSession;

import javax.servlet.ServletInputStream;
import java.io.*;
import java.util.HashMap;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestParserTest {
    private WebServerServletRequest request;
    private final RequestParser requestParser = new RequestParser();

    @BeforeEach
    void setUp() {
        request = new WebServerServletRequest();
    }

    @Test
    void injectData() {
        String data = "GET /test/hello HTTP/1.1";

        requestParser.injectData(request, data);

        assertEquals(HttpMethod.GET.name(), request.getMethod());
        assertEquals("test", request.getAppName());
        assertEquals("/hello", request.getRequestURI());
    }

    @Test
    void testFavicon() {
        String data = "GET /favicon.ico HTTP/1.1";

        requestParser.injectData(request, data);

        assertEquals(HttpMethod.GET.name(), request.getMethod());
        assertNull(request.getAppName());
        assertNull(request.getRequestURI());
    }

    @Test
    void injectUrlencodedBody() throws IOException {
        String test = "name=login&pass=123";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        request.setContentLength(test.length());

        requestParser.injectUrlencodedBody(request, reader);

        assertEquals("login", request.getParameter("name"));
        assertEquals("123", request.getParameter("pass"));
    }

    @Test
    void injectHeaders() throws IOException {
        String test = "Host: test.com\n" +
                "Cache-Control: no-cache\n\n";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

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

        requestParser.injectHeaders(request, reader);
    }

    @Test
    void injectTempSessionCookie() throws IOException {
        injectCookieHeader();

        requestParser.injectCookies(request);
        requestParser.injectTempSessionCookie(request);

        assertEquals("123456", request.getTempSessionCookie());
    }

    @Test
    void injectNotFoundInRegistrySession() {
        request.setAppName("app1");
        request.setHeaders(new HashMap<>());

        requestParser.injectTempSessionCookie(request);

        assertNull(request.getTempSessionCookie());
    }

    @Test
    void injectMultipartBody() throws IOException {
        String headContent = "POST / HTTP/1.1\r\n" +
                "Host: localhost:8000\r\n" +
                "Content-Type: multipart/form-data; boundary=---------------------------9051914041544843365972754266\r\n" +
                "Content-Length: 554\r\n";
        String bodyContent = "-----------------------------9051914041544843365972754266\r\n" +
                "Content-Disposition: form-data; name=\"text\"\r\n" +
                "\r\n" +
                "text default\r\n" +
                "-----------------------------9051914041544843365972754266\r\n" +
                "Content-Disposition: form-data; name=\"file1\"; filename=\"a.txt\"\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "Content of a.txt.\r\n" +
                "\r\n" +
                "-----------------------------9051914041544843365972754266\r\n" +
                "Content-Disposition: form-data; name=\"file2\"; filename=\"a.html\"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<!DOCTYPE html><title>Content of a.html.</title>\r\n" +
                "\r\n" +
                "-----------------------------9051914041544843365972754266--";
        String testHttpRequest = headContent + "\r\n" + bodyContent;

        BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(testHttpRequest.getBytes()));
        bufferedInputStream.mark(16 * 1024);

        long skipped = headContent.getBytes().length;

        request.setContentLength(554);
        request.setContentType("Content-Type: multipart/form-data; boundary=---------------------------9051914041544843365972754266");
        requestParser.injectMultipartBody(request, bufferedInputStream, skipped);

        ServletInputStream inputStream = request.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringJoiner stringJoiner = new StringJoiner("\r\n");
        String output;
        while ((output = bufferedReader.readLine()) != null) {
            stringJoiner.add(output);
        }

        assertEquals(bodyContent, stringJoiner.toString());
    }
}