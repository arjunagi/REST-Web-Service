import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.Request;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Karthik on 5/27/17.
 */
public class ProjectService {

    public ResponseMessage createSingleProject(Request request) {
        Project project = new Gson().fromJson(request.body(), Project.class);
        return createProject(project);
    }

    public ResponseMessage createMultipleProjects(Request request) {
        Type listType = new TypeToken<List<Project>>() {}.getType();
        List<Project> projects = new Gson().fromJson(request.body(), listType);
        ResponseMessage responseMessage = null;

        if (!allProjectsValid(projects)) {
            responseMessage = new ResponseMessage("Data is invalid", 400);
        }

        for (Project p : projects) {
            responseMessage = createProject(p);
            if (responseMessage.getStatusCode() != 200)
                break;
        }
        return responseMessage;
    }

    private ResponseMessage createProject(Project project) {

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

    private Boolean allProjectsValid(List<Project> projectArrayList) {
        for(Project p: projectArrayList) {
            if(!p.isValid()) return false;
        }
        return true;
    }

    public String requestProject(String projectId) {
        getProject(projectId);
        return new String("Success: " + projectId);
    }

    private String getProject(String projectId) {
        try(BufferedReader br = new BufferedReader(new FileReader("projects.txt"))) {
            for(String line; (line = br.readLine()) != null; ) {
                System.out.println(line);
            }
            // line is not visible here.
        } catch (Exception e) {

        }
        return null;
    }
}
