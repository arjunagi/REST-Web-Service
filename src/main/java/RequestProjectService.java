import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Karthik on 5/28/17.
 */
public class RequestProjectService {

    public Response requestProject(String projectId) {
        return getProject(projectId);
    }

    private Response getProject(String projectId) {

        Gson gson = new Gson();
        try(BufferedReader br = new BufferedReader(new FileReader("projects.txt"))) {
            for(String line; (line = br.readLine()) != null; ) {
                Project project = new Gson().fromJson(line, Project.class);
                if(!project.isValid())
                    return new ResponseMessageWithStatusCode("Project data error", 500);
                if(project.getId() == Integer.parseInt(projectId)) {
                    return new ResponseProject(project.getProjectName(), project.getProjectCost(), project.getProjectUrl());
                }
                else {
                    return  new ResponseMessage("no project found");
                }
            }
            // line is not visible here.
        } catch (Exception e) {

        }
        return null;
    }
}
