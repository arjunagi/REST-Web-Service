import com.google.gson.Gson;
import java.io.*;

/**
 * Created by Karthik on 5/27/17.
 */
public class ProjectService {

    public ResponseMessage createProject(Project project) {

        Gson gson = new Gson();
        BufferedWriter writer = null;
        ResponseMessage responseMessage = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("projects.txt", true), "UTF-8"));
            writer.write(gson.toJson(project));
            writer.newLine();
            responseMessage = new ResponseMessage("campaign is successfully created", 200);
        } catch (IOException e) {
            responseMessage = new ResponseMessage("Error writing to file: " + e, 500);
        } finally {
            try {
                writer.flush();
                writer.close();
                return responseMessage;
            } catch (IOException e) {
                return new ResponseMessage("Error closing file: " + e, 500);
            }
        }
    }
}
