package petrovskyi.webserver.web.http.session;

public class WebServerSession extends HttpSessionAdapter {
    @Override
    public Object getAttribute(String s) {
        return "Test";
    }
}
