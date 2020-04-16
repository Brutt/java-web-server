package petrovskyi.webserver.web.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import petrovskyi.webserver.web.http.HttpMethod;
import petrovskyi.webserver.web.http.request.WebServerServletRequest;
import petrovskyi.webserver.web.http.session.WebServerSession;

import javax.servlet.http.Cookie;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class RequestParser {
    private long skipped;

    public WebServerServletRequest parseRequest(BufferedInputStream bufferedInputStream, BufferedReader socketReader) throws IOException {
        log.debug("Starting to parse request");

        bufferedInputStream.mark(1024000);

        WebServerServletRequest request = new WebServerServletRequest();

        String firstLine = socketReader.readLine();
        incSkipped(firstLine);

        injectData(request, firstLine);
        if (request.getAppName() != null) {
            injectHeaders(request, socketReader);
            injectBody(request, socketReader, bufferedInputStream);
            injectCookies(request);
            injectTempSessionCookie(request);
        }

        return request;
    }

    private void incSkipped(String data) {
        skipped += data.getBytes().length + 2;
    }

    void injectData(WebServerServletRequest request, String requestLine) {
        log.debug("Injecting data [{}] into request", requestLine);
        String[] firstLine = requestLine.split(" ");
        String[] uris = firstLine[1].split("/");

        request.setHttpMethod(HttpMethod.valueOf(firstLine[0]));
        if (uris.length > 1 && !"favicon.ico".equals(uris[1])) {
            request.setAppName(uris[1]);
            request.setUri(firstLine[1].substring(uris[1].length() + 1));
        }
    }

    void injectHeaders(WebServerServletRequest request, BufferedReader socketReader) throws IOException {
        log.debug("Injecting headers into request");
        Map<String, String> headers = new HashMap<>();
        String message;
        while (!(message = socketReader.readLine()).equals("")) {
            incSkipped(message);

            String key = message.substring(0, message.indexOf(":"));
            String value = message.substring(message.indexOf(":") + 2);
            headers.put(key, value);
            log.debug("Injecting header {} with value {} into request", key, value);
        }

        request.setHeaders(headers);
    }

    void injectBody(WebServerServletRequest request, BufferedReader socketReader, BufferedInputStream bufferedInputStream) throws IOException {
        log.debug("Injecting body into request");

        String contentLengthStr = request.getHeader("Content-Length");
        if (contentLengthStr == null || Integer.parseInt(contentLengthStr) <= 0) {
            return;
        }
        int contentLength = Integer.parseInt(contentLengthStr);

        request.setContentLength(contentLength);

        String contentTypeHeader = request.getHeader("Content-Type");
        request.setContentType(contentTypeHeader);
        if (contentTypeHeader != null && contentTypeHeader.contains("multipart/form-data")) {
            bufferedInputStream.reset();
            bufferedInputStream.skip(skipped + 2);

            ByteBuffer byteBuffer = ByteBuffer.allocate(contentLength);
            while (bufferedInputStream.available() > 0) {
                byteBuffer.put((byte) bufferedInputStream.read());
            }

            request.setCachedBody(byteBuffer.array());

            return;
        }

        char[] buf = new char[contentLength];
        socketReader.read(buf);

        String params = String.valueOf(buf);

        Map<String, String> parameterMap = new HashMap<>();
        String[] splitParams = params.split("&");
        for (String splitParam : splitParams) {
            String[] keyValue = splitParam.split("=");
            if (keyValue.length == 2) {
                parameterMap.put(keyValue[0], keyValue[1]);
            }
        }

        request.setParameters(parameterMap);

        log.debug("Map with parameters {} was injected", parameterMap);
    }

    void injectTempSessionCookie(WebServerServletRequest request) {
        log.debug("Injecting session into request");
        WebServerSession webServerSession = null;

        String suffix = DigestUtils.sha1Hex(request.getAppName());
        String sessionCookieName = WebServerSession.SESSIONID + "." + suffix;

        for (Cookie cookie : request.getCookies()) {
            if (sessionCookieName.equals(cookie.getName())) {
                request.setTempSessionCookie(cookie.getValue());
                break;
            }
        }
    }

    void injectCookies(WebServerServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader != null) {
            String[] cookieSplit = cookieHeader.split("; ");
            List<Cookie> cookieList = new CopyOnWriteArrayList<>();

            for (String cookie : cookieSplit) {
                String[] keyValue = cookie.split("=");

                if (keyValue.length == 2) {
                    cookieList.add(new Cookie(keyValue[0], keyValue[1]));
                }
            }
            request.setCookies(cookieList.toArray(new Cookie[0]));
        }
    }
}
