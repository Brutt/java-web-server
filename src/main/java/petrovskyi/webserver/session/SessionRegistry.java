package petrovskyi.webserver.session;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.web.http.request.WebServerServletRequest;
import petrovskyi.webserver.web.http.session.WebServerSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionRegistry {
    private Map<String, Map<String, WebServerSession>> appNameToIdToSessionMap = new ConcurrentHashMap<>();

    public void register(String applicationName, WebServerSession webServerSession) {
        Map<String, WebServerSession> idToSession = appNameToIdToSessionMap.get(applicationName);
        if (idToSession == null) {
            idToSession = new ConcurrentHashMap<>();
        }
        idToSession.put(webServerSession.getId(), webServerSession);
        appNameToIdToSessionMap.put(applicationName, idToSession);
    }

    public WebServerSession getSession(String applicationName, String sessionId) {
        Map<String, WebServerSession> idToSession = appNameToIdToSessionMap.get(applicationName);

        return idToSession == null || sessionId == null ? null : idToSession.get(sessionId);
    }

    public void injectSession(WebServerServletRequest request) {
        WebServerSession webServerSession = getSession(request.getAppName(), request.getTempSessionCookie());

        if (webServerSession == null) {
            webServerSession = new WebServerSession(UUID.randomUUID().toString());
            register(request.getAppName(), webServerSession);
            log.debug("Session was not found. Create new session {}", webServerSession);
        } else {
            log.debug("Session {} was found in registry", webServerSession);
        }
        request.setWebServerSession(webServerSession);
    }
}
