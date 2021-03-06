package petrovskyi.webserver.application.creator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petrovskyi.webserver.application.entity.ApplicationInfo;
import petrovskyi.webserver.classloader.ChildFirstClassLoader;
import petrovskyi.webserver.web.filter.config.WebServerFilterConfig;
import petrovskyi.webserver.web.servlet.config.WebServletConfig;
import petrovskyi.webserver.web.servlet.context.WebServletContext;
import petrovskyi.webserver.webapp.entity.WebXmlDefinition;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationInfoCreator {
    private static final String WEB_INF = "WEB-INF";
    private static final String CLASSES = "classes";
    private static final String LIB = "lib";
    private static final String JAR = ".jar";
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public ApplicationInfo create(String appDir, WebXmlDefinition webXmlDefinition) {
        String appName = appDir.substring(appDir.lastIndexOf("/") + 1);
        LOG.info("Start to create new application with name {}", appName);

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        URL[] urls = getUrls(appDir);
        ChildFirstClassLoader classLoader = new ChildFirstClassLoader(urls);

        Map<String, HttpServlet> urlToServlet;
        Map<String, List<Filter>> urlToFilters;


        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            WebServletContext webServletContext = new WebServletContext(classLoader);
            urlToServlet = createUrlToServletMap(webXmlDefinition.getUrlToServletClassName(), classLoader, webServletContext);
            urlToFilters = createUrlToFiltersMap(webXmlDefinition.getUrlToFiltersClassName(), classLoader, webServletContext);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

        ApplicationInfo applicationInfo = new ApplicationInfo(appName, urlToServlet, urlToFilters);

        LOG.info("Application {} was successfully created", applicationInfo.getName());

        return applicationInfo;
    }

    private Map<String, HttpServlet> createUrlToServletMap(Map<String, List<String>> urlToClassName,
                                                           ChildFirstClassLoader classLoader,
                                                           WebServletContext webServletContext) {
        LOG.debug("Start to transform urlToClassName map into urlToServlet");
        Map<String, HttpServlet> servletMap = new HashMap<>();

        for (String url : urlToClassName.keySet()) {
            try {
                Class<?> aClass = classLoader.loadClass(urlToClassName.get(url).get(0));
                HttpServlet httpServlet = (HttpServlet) aClass.getDeclaredConstructor().newInstance();

                httpServlet.init(new WebServletConfig(webServletContext));

                servletMap.put(url, httpServlet);
                LOG.debug("Servlet for class {} was successfully instantiated", aClass);
            } catch (Exception e) {
                LOG.error("Error while trying to get servlet", e);
                throw new RuntimeException("Error while trying to get servlet", e);
            }
        }

        return servletMap;
    }

    private Map<String, List<Filter>> createUrlToFiltersMap(Map<String, List<String>> urlToClassName,
                                                            ChildFirstClassLoader classLoader,
                                                            WebServletContext webServletContext) {
        LOG.debug("Start to transform urlToClassName map into urlToFilters");
        Map<String, List<Filter>> filtersMap = new HashMap<>();

        for (String url : urlToClassName.keySet()) {
            for (String filterClassName : urlToClassName.get(url)) {
                try {
                    Class<?> aClass = classLoader.loadClass(filterClassName);
                    Filter filter = (Filter) aClass.getDeclaredConstructor().newInstance();

                    filter.init(new WebServerFilterConfig(webServletContext));

                    List<Filter> filterList = filtersMap.get(url);
                    if (filterList == null) {
                        filterList = new ArrayList<>();
                    }
                    filterList.add(filter);

                    filtersMap.put(url, filterList);
                    LOG.debug("Filter for class {} was successfully instantiated", aClass);
                } catch (Exception e) {
                    LOG.error("Error while trying to get filter", e);
                    throw new RuntimeException("Error while trying to get filter", e);
                }
            }
        }

        return filtersMap;
    }

    private URL[] getUrls(String appDir) {
        LOG.debug("Start to get class loader in folder {}", appDir);
        File classDir = Paths.get(appDir, WEB_INF, CLASSES).toFile();
        File libDir = Paths.get(appDir, WEB_INF, LIB).toFile();

        URL[] urls;

        try {
            List<URL> urlsList = new ArrayList<>();

            URL classUrl = classDir.toURI().toURL();
            urlsList.add(classUrl);
            LOG.debug("{} was added to class loader", classDir);

            try (Stream<Path> walk = Files.walk(libDir.toPath())) {
                List<File> result = walk.map(Path::toFile)
                        .filter(x -> x.getName().endsWith(JAR))
                        .collect(Collectors.toList());

                for (File jarFile : result) {
                    LOG.debug("JAR-file {} was found and added to class loader", jarFile);
                    urlsList.add(jarFile.toURI().toURL());
                }
            } catch (IOException e) {
                LOG.error("Error while walking through {} to find jar files", libDir, e);
                throw new RuntimeException("Error while walking through " + libDir + " to find jar files", e);
            }

            urls = urlsList.toArray(new URL[0]);
        } catch (Exception e) {
            LOG.error("Error while trying to transform file {} into URL", classDir, e);
            throw new RuntimeException("Error while trying to transform file " + classDir + " into URL", e);
        }

        return urls;
    }
}
