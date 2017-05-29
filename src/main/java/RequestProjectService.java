import com.google.gson.Gson;
import spark.Request;
import java.io.BufferedReader;;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
        if(parameterMap.isEmpty())
            return getHighestCostProject();
        if(!validateParameterMap(parameterMap))
            return new ResponseMessageWithStatusCode("Invalid parameter in the request", 400);
        return getProject(projectId, parameterMap);
    }

    /**
     *
     * @param projectId
     * @return
     */
    private Response getProject(String projectId, Map<String, String[]> parameterMap) {

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

    /**
     *
     * @param parameterMap
     * @param project
     * @return
     */
    private boolean passesAllRules(Map<String,String[]> parameterMap, Project project) {

        return true;
    }

    /**
     *
     * @return
     */
    private Response getHighestCostProject() {

        Project highestCostProject = null;
        double highestCost = Double.MIN_VALUE;

        try(BufferedReader br = new BufferedReader(new FileReader("projects.txt"))) {

            for(String line; (line = br.readLine()) != null; ) {

                Project project = new Gson().fromJson(line, Project.class);

                if(!project.isValid() || project.getEnabled().equals(false) || isExpired(project.getExpiryDate()) || project.getProjectUrl().equals(null))
                    continue;
                else if(project.getProjectCost() > highestCost) {
                    highestCost = project.getProjectCost();
                    highestCostProject = project;
                }
            }

            if(highestCost == Double.MIN_VALUE )
                return new ResponseMessage("no project found");
            else {
                return new ResponseProject(highestCostProject.getProjectName(), highestCostProject.getProjectCost(), highestCostProject.getProjectUrl());
            }

        } catch (Exception e) {
            return new ResponseMessageWithStatusCode(e + ": Error during file read", 500);
        }
    }

    /**
     *
     * @param ed
     * @return
     */
    private boolean isExpired(String ed) {
        DateFormat df = new SimpleDateFormat("MMddyyyy HH:mm:ss");
        try {
            Date expiryDate = df.parse(ed);
            Date currentDate = new Date();
            if(currentDate.after(expiryDate)) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }


}
