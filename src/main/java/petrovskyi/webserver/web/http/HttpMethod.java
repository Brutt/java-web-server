package petrovskyi.webserver.web.http;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }
}
