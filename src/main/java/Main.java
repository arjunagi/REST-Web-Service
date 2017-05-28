/**
 * Created by Karthik on 5/25/17.
 */

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import spark.Request;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {

        ProjectService projectService = new ProjectService();
        Gson gson = new Gson();
        post("/createProject", "application/json", (request, response) -> {
            try {
                response.type("application/json");
                JsonReader reader = new JsonReader(new StringReader(request.body()));
                ResponseMessage responseMessage = null;

                if(reader.peek() == JsonToken.BEGIN_OBJECT) {
                    responseMessage = createSingleProject(request, projectService);
                    response.status(responseMessage.getStatusCode());
                }
                else if(reader.peek() == JsonToken.BEGIN_ARRAY) {
                    responseMessage = createMultipleProjects(request, projectService);
                    response.status(responseMessage.getStatusCode());
                }
                else {
                    response.status(400);
                    responseMessage = new ResponseMessage("Data is invalid", 400);
                }
                return gson.toJson(responseMessage);
            } catch (JsonParseException e) {
                return gson.toJson(new ResponseMessage(e, 400));
            }
        });
    }

    private static ResponseMessage createSingleProject(Request request, ProjectService projectService) {
        Project project = new Gson().fromJson(request.body(), Project.class);
        return projectService.createProject(project);
    }

    private static ResponseMessage createMultipleProjects(Request request, ProjectService projectService) {
        Type listType = new TypeToken<List<Project>>() {}.getType();
        List<Project> projects = new Gson().fromJson(request.body(), listType);
        ResponseMessage responseMessage = null;

        if (!allProjectsValid(projects)) {
            responseMessage = new ResponseMessage("Data is invalid", 400);
        }

        for (Project p : projects) {
            responseMessage = projectService.createProject(p);
            if (responseMessage.getStatusCode() != 200)
                break;
        }
        return responseMessage;
    }

    private static Boolean allProjectsValid(List<Project> projectArrayList) {
        for(Project p: projectArrayList) {
            if(!p.isValid()) return false;
        }
        return true;
    }
}
