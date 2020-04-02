package petrovskyi.webserver.web.http.request;

import lombok.Setter;
import petrovskyi.webserver.web.http.HttpMethod;
import petrovskyi.webserver.web.http.session.WebServerSession;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Setter
public class WebServerServletRequest extends HttpServletRequestAdapter {
    private HttpMethod httpMethod;
    private String uri;
    private Map<String, String> headers;
    private String appName;
    private WebServerSession webServerSession;

    @Override
    public String getHeader(String s) {
        return headers.get(s);
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
    public HttpSession getSession() {
        return webServerSession;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
