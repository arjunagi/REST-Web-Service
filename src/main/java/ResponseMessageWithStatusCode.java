import lombok.Data;

/**
 * Created by Karthik on 5/27/17.
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
