package petrovskyi.webserver.web.http.request;

import lombok.Getter;
import lombok.Setter;
import petrovskyi.webserver.web.http.HttpMethod;
import petrovskyi.webserver.web.http.request.inputstream.CachedBodyServletInputStream;
import petrovskyi.webserver.web.http.session.WebServerSession;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Setter
public class WebServerServletRequest extends HttpServletRequestAdapter {
    private HttpMethod httpMethod;
    private String uri;
    private Map<String, String> headers;
    private String appName;
    private Map<String, String> parameters;
    private WebServerSession webServerSession;
    private ServletContext servletContext;
    private Map<String, Object> attributes = new HashMap<>();
    private Cookie[] cookies = new Cookie[0];
    private String contentType;
    private int contentLength;
    private byte[] cachedBody;
    @Getter
    private String tempSessionCookie;

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    @Override
    public String getHeader(String s) {
        return headers.get(s);
    }

    @Override
    public String getContextPath() {
        return "/" + appName;
    }

    @Override
    public String getMethod() {
        return httpMethod.name();
    }

    @Override
    public String getRequestURI() {
        return uri;
    }

    @Override
    public String getServletPath() {
        return uri;
    }

    @Override
    public HttpSession getSession(boolean b) {
        return webServerSession;
    }

    @Override
    public HttpSession getSession() {
        return webServerSession;
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getParameter(String s) {
        return parameters.get(s);
    }

    @Override
    public String getProtocol() {
        return "HTTP/1.1";
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getAppName() {
        return appName;
    }

}
