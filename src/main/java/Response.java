package PACKAGE_NAME;

/**
 * Created by Karthik on 5/28/17.
 */
public class Response {
    private static Response ourInstance = new Response();

    public static Response getInstance() {
        return ourInstance;
    }

    private Response() {
    }
}
