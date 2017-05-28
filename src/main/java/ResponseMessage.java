import lombok.Data;

/**
 * Created by Karthik on 5/27/17.
 */
@Data
public class ResponseMessage {
    private int statusCode;
    private String message;
    public ResponseMessage(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
    public ResponseMessage(Exception e, int statusCode) {
        this.message = e.getMessage();
        this.statusCode = statusCode;
    }
}
