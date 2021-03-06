package petrovskyi.webserver.web.http;

public enum HttpStatusCode {
    OK(200, "OK"),
    REDIRECT_FOUND(302, "Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String name;

    HttpStatusCode(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getFullName() {
        return code + " " + name;
    }
}
