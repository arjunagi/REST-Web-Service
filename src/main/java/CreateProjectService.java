import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.Request;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Karthik on 5/27/17.
 */
public class CreateProjectService {

    public ResponseMessageWithStatusCode createSingleProject(Request request) {
        Project project = new Gson().fromJson(request.body(), Project.class);
        if (!project.isValid()) {
            return new ResponseMessageWithStatusCode("Data is invalid", 400);
        }
        return createProject(project);
    }

    public ResponseMessageWithStatusCode createMultipleProjects(Request request) {
        Type listType = new TypeToken<List<Project>>() {}.getType();
        List<Project> projects = new Gson().fromJson(request.body(), listType);
        ResponseMessageWithStatusCode responseMessageWithStatusCode = null;

        if (!allProjectsValid(projects)) {
            responseMessageWithStatusCode = new ResponseMessageWithStatusCode("Data is invalid", 400);
        }

        for (Project p : projects) {
            responseMessageWithStatusCode = createProject(p);
            if (responseMessageWithStatusCode.getStatusCode() != 200)
                break;
        }
        return responseMessageWithStatusCode;
    }

    private ResponseMessageWithStatusCode createProject(Project project) {

        Gson gson = new Gson();
        BufferedWriter writer = null;
        ResponseMessageWithStatusCode responseMessageWithStatusCode = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("projects.txt", true), "UTF-8"));
            writer.write(gson.toJson(project));
            writer.newLine();
            responseMessageWithStatusCode = new ResponseMessageWithStatusCode("campaign is successfully created", 200);
        } catch (IOException e) {
            responseMessageWithStatusCode = new ResponseMessageWithStatusCode("Error writing to file: " + e, 500);
        } finally {
            try {
                writer.flush();
                writer.close();
                return responseMessageWithStatusCode;
            } catch (IOException e) {
                return new ResponseMessageWithStatusCode("Error closing file: " + e, 500);
            }
        }
    }

    private Boolean allProjectsValid(List<Project> projectArrayList) {
        for(Project p: projectArrayList) {
            if(!p.isValid()) return false;
        }
        return true;
    }
}
