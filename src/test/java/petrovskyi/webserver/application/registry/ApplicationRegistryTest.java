package petrovskyi.webserver.application.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import petrovskyi.webserver.application.entity.ApplicationInfo;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationRegistryTest {
    private ApplicationRegistry applicationRegistry = new ApplicationRegistry();
    @Mock
    private HttpServlet httpServlet1;
    @Mock
    private HttpServlet httpServlet2;
    @Mock
    private Filter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        HashMap<String, HttpServlet> urlToServlet = new HashMap<>();
        urlToServlet.put("servlet1", httpServlet1);
        urlToServlet.put("servlet2", httpServlet2);
        List<Filter> filters = new ArrayList<>();
        filters.add(filter);
        HashMap<String, List<Filter>> urlToFilters = new HashMap<>();
        urlToFilters.put("/", filters);

        ApplicationInfo applicationInfo1 = new ApplicationInfo("test1", urlToServlet, urlToFilters, null);
        applicationRegistry.register(applicationInfo1);

        HashMap<String, HttpServlet> urlToServlet2 = new HashMap<>();
        urlToServlet2.put("servlet2", httpServlet2);
        ApplicationInfo applicationInfo2 = new ApplicationInfo("test2", urlToServlet2, urlToFilters, null);
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
    void verifyDestroy() {
        ApplicationInfo test2 = applicationRegistry.getApplication("test2");
        assertEquals("test2", test2.getName());
        ApplicationInfo removed = applicationRegistry.remove("test2");
        assertEquals(test2, removed);

        Mockito.verify(httpServlet1, Mockito.never()).destroy();
        Mockito.verify(httpServlet2).destroy();
        Mockito.verify(filter).destroy();

        assertNull(applicationRegistry.getApplication("test2"));
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