package petrovskyi.webserver.session;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.web.http.session.WebServerSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionRegistry {
    private Map<String, Map<String, WebServerSession>> appNameToIdToSessionMap = new ConcurrentHashMap<>();

    public void register(String applicationName, WebServerSession webServerSession) {
        Map<String, WebServerSession> idToSession = appNameToIdToSessionMap.get(applicationName);
        if (idToSession == null) {
            idToSession = new ConcurrentHashMap<>();
        }
        idToSession.put(webServerSession.getSessionId(), webServerSession);
        appNameToIdToSessionMap.put(applicationName, idToSession);
    }

    public WebServerSession getSession(String applicationName, String sessionId) {
        Map<String, WebServerSession> idToSession = appNameToIdToSessionMap.get(applicationName);

        return idToSession == null ? null : idToSession.get(sessionId);
    }
}
