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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class RequestParser {
    private static final byte END_BYTES_COUNT = 2;
    private static final String MULTIPART_CONTENT_TYPE = "multipart/form-data";
    private static final int HTTP_HEADER_LENGTH_BUFFER = 16 * 1024; //16KB

    public WebServerServletRequest parseRequest(BufferedInputStream bufferedInputStream) throws IOException {
        log.debug("Starting to parse request");

        bufferedInputStream.mark(HTTP_HEADER_LENGTH_BUFFER);

        WebServerServletRequest request = new WebServerServletRequest();

        BufferedReader socketReader = new BufferedReader(new InputStreamReader(bufferedInputStream));

        String firstLine = socketReader.readLine();
        long skipped = getBytesCount(firstLine);

        injectData(request, firstLine);
        if (request.getAppName() == null) {
            return request;
        }

        skipped += injectHeaders(request, socketReader);
        injectContentTypeAndLength(request);
        if (request.getContentType() != null && request.getContentType().contains(MULTIPART_CONTENT_TYPE)) {
            injectMultipartBody(request, bufferedInputStream, skipped);
        } else {
            injectUrlencodedBody(request, socketReader);
        }

        injectCookies(request);
        injectTempSessionCookie(request);

        return request;
    }

    private long getBytesCount(String data) {
        return data.getBytes().length + END_BYTES_COUNT;
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

    long injectHeaders(WebServerServletRequest request, BufferedReader socketReader) throws IOException {
        log.debug("Injecting headers into request");
        long bytesCount = 0;

        Map<String, String> headers = new HashMap<>();
        String header;
        while (!(header = socketReader.readLine()).equals("")) {
            bytesCount += getBytesCount(header);

            String key = header.substring(0, header.indexOf(":"));
            String value = header.substring(header.indexOf(":") + 2);
            headers.put(key, value);
            log.debug("Injecting header {} with value {} into request", key, value);
        }

        request.setHeaders(headers);

        return bytesCount;
    }

    void injectContentTypeAndLength(WebServerServletRequest request) {
        log.debug("Injecting content-type and content length into request");

        String contentLengthStr = request.getHeader("Content-Length");
        if (contentLengthStr == null || Integer.parseInt(contentLengthStr) <= 0) {
            return;
        }
        int contentLength = Integer.parseInt(contentLengthStr);
        request.setContentLength(contentLength);

        String contentTypeHeader = request.getHeader("Content-Type");
        request.setContentType(contentTypeHeader);
    }

    void injectMultipartBody(WebServerServletRequest request, BufferedInputStream bufferedInputStream, long skipped) throws IOException {
        log.debug("Injecting multipart body into request");

        bufferedInputStream.reset();
        bufferedInputStream.skip(skipped + END_BYTES_COUNT);

        byte[] byteBuffer = new byte[request.getContentLength()];
        bufferedInputStream.read(byteBuffer);

        request.setCachedBody(byteBuffer);
    }

    void injectUrlencodedBody(WebServerServletRequest request, BufferedReader socketReader) throws IOException {
        log.debug("Injecting x-www-form-urlencoded body into request");

        char[] buf = new char[request.getContentLength()];
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
