import lombok.Data;

/**
 * Class to create responses with status code and message.
 */
@Data
public class ResponseMessageWithStatusCode extends Response{

    private int statusCode;

    public ResponseMessageWithStatusCode(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
    public ResponseMessageWithStatusCode(Exception e, int statusCode) {
        this.message = e.getMessage();
        this.statusCode = statusCode;
    }
}
