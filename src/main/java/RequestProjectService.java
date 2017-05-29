import com.google.gson.Gson;
import spark.Request;
import java.io.BufferedReader;;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Karthik on 5/28/17.
 */
public class RequestProjectService {

    /**
     *
     * @param request
     * @return
     */
    public Response requestProject(Request request) {
        String projectId = request.queryParams("projectid");

        Map<String, String[]> parameterMap = request.queryMap().toMap();
        if(!validateParameterMap(parameterMap))
            return new ResponseMessageWithStatusCode("Invalid parameter in the request", 400);
        return getProject(projectId);
    }

    /**
     *
     * @param projectId
     * @return
     */
    private Response getProject(String projectId) {

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
        } catch (Exception e) {
            return new ResponseMessageWithStatusCode(e + "Error during file read", 500);
        }
        return null;
    }

    /**
     *
     * @param parameterMap
     * @return
     */
    private boolean validateParameterMap(Map<String, String[]> parameterMap) {
        String[] tempValidParameters = new String[] { "projectid", "country", "number","keyword" };
        Set<String> validParameters = new HashSet<String >(Arrays.asList(tempValidParameters));
        Set<String> parameters = parameterMap.keySet();

        for(String param: parameters) {
            if(!validParameters.contains(param))
                return false;
        }
        return true;
    }
}
