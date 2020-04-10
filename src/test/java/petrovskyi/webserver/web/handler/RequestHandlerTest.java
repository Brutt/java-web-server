package petrovskyi.webserver.web.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import petrovskyi.webserver.application.entity.ApplicationInfo;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestHandlerTest {
    @Mock
    private HttpServlet httpServlet1;
    @Mock
    private HttpServlet httpServlet2;
    @Mock
    Filter filter1;
    @Mock
    Filter filter2;
    @Mock
    Filter filter3;
    @Mock
    Filter filter4;

    private RequestHandler requestHandler = new RequestHandler(null, null, null);
    private ApplicationInfo applicationInfo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        HashMap<String, HttpServlet> urlToServlet = new HashMap<>();
        urlToServlet.put("servlet1", httpServlet1);
        urlToServlet.put("servlet2", httpServlet2);

        HashMap<String, List<Filter>> urlToFilters = new HashMap<>();
        List<Filter> filters1 = new ArrayList<>();
        filters1.add(filter1);
        filters1.add(filter2);

        List<Filter> filters2 = new ArrayList<>();
        filters2.add(filter3);

        List<Filter> filters3 = new ArrayList<>();
        filters3.add(filter4);

        urlToFilters.put("/*", filters1);
        urlToFilters.put("/test1", filters2);
        urlToFilters.put("/test1/test2", filters3);

        applicationInfo = new ApplicationInfo("test1", urlToServlet, urlToFilters);
    }

    @Test
    void getFilters() {
        List<Filter> filters = requestHandler.getFilters(applicationInfo, "/test1/test2");

        assertEquals(3, filters.size());
        Iterator<Filter> iterator = filters.iterator();
        while (iterator.hasNext()){
            Filter next = iterator.next();
            if(next.equals(filter1)){
                iterator.remove();
            }else if(next.equals(filter2)){
                iterator.remove();
            }else if(next.equals(filter4)){
                iterator.remove();
            }
        }

        assertEquals(0, filters.size());
    }

    @Test
    void testRootUrl() {
        List<Filter> filters = requestHandler.getFilters(applicationInfo, "/hello");

        assertEquals(2, filters.size());
        Iterator<Filter> iterator = filters.iterator();
        while (iterator.hasNext()){
            Filter next = iterator.next();
            if(next.equals(filter1)){
                iterator.remove();
            }else if(next.equals(filter2)){
                iterator.remove();
            }
        }

        assertEquals(0, filters.size());
    }
}