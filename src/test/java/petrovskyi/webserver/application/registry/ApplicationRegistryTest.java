package petrovskyi.webserver.application.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import petrovskyi.webserver.application.entity.ApplicationInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationRegistryTest {
    private ApplicationRegistry applicationRegistry = ApplicationRegistry.getInstance();
    private HttpServlet httpServlet1 = new HttpServlet() {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    };
    private HttpServlet httpServlet2 = new HttpServlet() {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    };

    @BeforeEach
    void setUp() {
        HashMap<String, HttpServlet> urlToServlet = new HashMap<>();
        urlToServlet.put("servlet1", httpServlet1);
        urlToServlet.put("servlet2", httpServlet2);
        ApplicationInfo applicationInfo1 = new ApplicationInfo("test1", urlToServlet);
        applicationRegistry.register(applicationInfo1);

        HashMap<String, HttpServlet> urlToServlet2 = new HashMap<>();
        urlToServlet2.put("servlet2", httpServlet2);
        ApplicationInfo applicationInfo2 = new ApplicationInfo("test2", urlToServlet2);
        applicationRegistry.register(applicationInfo2);
    }

    @Test
    void remove() {
        ApplicationInfo test1 = applicationRegistry.getApplication("test1");
        assertEquals("test1", test1.getName());
        ApplicationInfo removed = applicationRegistry.remove("test1");
        assertEquals(test1, removed);

        assertNull(applicationRegistry.getApplication("test1"));
    }

    @Test
    void getApplication() {
        ApplicationInfo test1 = applicationRegistry.getApplication("test1");
        assertEquals("test1", test1.getName());
        ApplicationInfo test2 = applicationRegistry.getApplication("test2");
        assertEquals("test2", test2.getName());

        assertNull(applicationRegistry.getApplication("test3"));
    }

    @Test
    void cleanAll() {
        getApplication();

        applicationRegistry.cleanAll();

        assertNull(applicationRegistry.getApplication("test1"));
        assertNull(applicationRegistry.getApplication("test2"));
        assertNull(applicationRegistry.getApplication("test3"));
    }


}