import com.google.gson.*;
import spark.Request;
import java.io.*;
import java.util.*;

/**
 * Class to handle the creatproject request.
 */
public class CreateProjectService {

    /**
     * Handles request which has a single Json object.
     * Checks if the keys in the json data is from the list of accepted keys.
     * Checks if the data values are valid.
     * @param request
     * @return Success response on project creation.
     *         403 for invalid values, if keys are invalid.
     *         500 for error during file writing
     */
    public ResponseMessageWithStatusCode createSingleProject(Request request) {

        if(!isProjectKeysValid(request.body())) {
            return new ResponseMessageWithStatusCode("Invalid key in data", 400);
        }
        Project project = new Gson().fromJson(request.body(), Project.class);
        if (!project.isValid()) {
            return new ResponseMessageWithStatusCode("Data is invalid", 400);
        }
        return writeProjectToFile(project);

    }

    /**
     * Handles request which has a array of Json.
     * For each project, checks if the keys in the json data is from the list of accepted keys.
     * For each project, checks if the data values are valid.
     * @param request
     * @return Success response on project creation.
     *         403 for invalid values, if keys are invalid.
     *         500 for error during file writing
     */
    public ResponseMessageWithStatusCode createMultipleProjects(Request request) {

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(request.body());
        JsonArray jsonArray = element.getAsJsonArray();
        ResponseMessageWithStatusCode responseMessageWithStatusCode = null;

        for (int i = 0; i < jsonArray.size(); ++i) {
            JsonElement jsonElement = jsonArray.get(i);

            if(!isProjectKeysValid(jsonElement.toString()))
                return new ResponseMessageWithStatusCode("Invalid key in data", 400);

            Project project = new Gson().fromJson(jsonElement.toString(), Project.class);

            if (!project.isValid()) {
                return new ResponseMessageWithStatusCode("Data is invalid", 400);
            }

            responseMessageWithStatusCode = writeProjectToFile(project);
            if (responseMessageWithStatusCode.getStatusCode() != 200)
                return responseMessageWithStatusCode;
        }

        return responseMessageWithStatusCode;
    }

    /**
     * Writes the valid project to projects.txt.
     * @param project
     * @return Response with appropriate status code and message.
     */
    private ResponseMessageWithStatusCode writeProjectToFile(Project project) {

        Gson gson = new Gson();
        BufferedWriter writer = null;
        ResponseMessageWithStatusCode responseMessageWithStatusCode = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("projects.txt", true), "UTF-8"));
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

    /**
     * Checks if the keys in Json data are from the list of accepted keys.
     * @param request
     * @return true if the keys in the data are correct, else false.
     */
    private boolean isProjectKeysValid(String request) {

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(request);
        JsonObject obj = element.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();

        String[] tempValidKeys = new String[] { "id", "projectName", "creationDate","expiryDate", "enabled",
                "targetCountries", "projectCost", "projectUrl", "targetKeys"};
        Set<String> validKeys = new HashSet<String >(Arrays.asList(tempValidKeys));

        for (Map.Entry<String, JsonElement> entry: entries) {
            if(!validKeys.contains(entry.getKey())) {
                return false;
            }
        }
        return true;
    }
}
