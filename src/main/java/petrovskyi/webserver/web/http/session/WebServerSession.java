package petrovskyi.webserver.web.http.session;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class WebServerSession extends HttpSessionAdapter {
    private Map<String, Object> attributes = new HashMap<>();
    @Getter
    private String sessionId;

    public WebServerSession(String jsessionid) {
        this.sessionId = jsessionid;
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }
}
