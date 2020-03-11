package petrovskyi.webserver.web.http;

import lombok.Setter;
import petrovskyi.webserver.web.http.adapter.HttpServletRequestAdapter;

import java.util.Map;

@Setter
public class WebServerServletRequest extends HttpServletRequestAdapter {
    private String httpMethod;
    private String uri;
    private Map<String, String> headers;
    private String appName;

    @Override
    public String getHeader(String s) {
        return headers.get(s);
    }

    @Override
    public String getMethod() {
        return httpMethod;
    }

    @Override
    public String getRequestURI() {
        return uri;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
