package petrovskyi.webserver.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import petrovskyi.webserver.web.http.session.WebServerSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SessionRegistryTest {
    @Mock
    WebServerSession webServerSession;

    private SessionRegistry sessionRegistry = new SessionRegistry();
    private String sessionId = "123456";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(webServerSession.getSessionId()).thenReturn(sessionId);

        sessionRegistry.register("test1", webServerSession);
    }

    @Test
    void getSession() {
        WebServerSession sessionResult = sessionRegistry.getSession("test1", sessionId);

        assertEquals(webServerSession, sessionResult);
    }
}