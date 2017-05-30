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

        if(!areParametersCorrect(parameterMap))
            return new ResponseMessageWithStatusCode("Invalid parameter in the request", 400);

        return getProject(projectId, parameterMap);
    }

    /**
     *
     * @param projectId
     * @return
     */
    private Response getProject(String projectId, Map<String, String[]> parameterMap) {

        if(parameterMap.keySet().contains("projectid"))
            return getProjectBasedOnId(parameterMap);

        else
            return getProjectsBasedOnParams(parameterMap);
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
                if(passesAllBasicRules(project) && project.getProjectCost() > highestCost) {
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
     * @param parameterMap
     * @return
     */
    private Response getProjectBasedOnId(Map<String, String[]> parameterMap) {

        int projectId = Integer.parseInt(parameterMap.get("projectid")[0]);

        try(BufferedReader br = new BufferedReader(new FileReader("projects.txt"))) {
            for(String line; (line = br.readLine()) != null; ) {
                Project project = new Gson().fromJson(line, Project.class);
                if(passesAllBasicRules(project) && project.getId() == projectId) {
                    return new ResponseProject(project.getProjectName(), project.getProjectCost(), project.getProjectUrl());
                }
            }
            return new ResponseMessage("no project found");
        } catch (Exception e) {
            return new ResponseMessageWithStatusCode(e + ": Error during file read", 500);
        }
    }

    /**
     * Not project ID
     * @param parameterMap
     * @return
     */
    private Response getProjectsBasedOnParams(Map<String, String[]> parameterMap) {

        try(BufferedReader br = new BufferedReader(new FileReader("projects.txt"))) {
            Project highestCostProject = null;
            double highestCost = Double.MIN_VALUE;

            for(String line; (line = br.readLine()) != null; ) {
                Project project = new Gson().fromJson(line, Project.class);
                if(passesAllBasicRules(project) && checkParameterValueMatches(project, parameterMap)) {
                    if(project.getProjectCost() > highestCost) {
                        highestCost = project.getProjectCost();
                        highestCostProject = project;
                    }
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
     * @param project
     * @param parameterMap
     * @return
     */
    private boolean checkParameterValueMatches(Project project, Map<String, String[]> parameterMap) {

        for(String p: parameterMap.keySet()) {

            if(p.equals("country")) {
                if(! project.getTargetCountries().contains(parameterMap.get(p)[0].toUpperCase()))
                    return false;
            }

            else if(p.equals("number")) {
                for (TargetKeys t : project.getTargetKeys()) {
                    if(Integer.parseInt(parameterMap.get(p)[0]) <= t.getNumber()) {
                        if(parameterMap.keySet().contains("keyword")) {
                            if(t.getKeyword().equals(parameterMap.get("keyword")[0]))
                                return true;
                            else continue;
                        }
                        else return true;
                    }
                    else continue;
                }
                return false;
            }

            else if(p.equals("keyword")) {
                for (TargetKeys t : project.getTargetKeys()) {
                    if(t.getKeyword().equals(parameterMap.get(p)[0])) {
                        if(parameterMap.keySet().contains("number")) {
                            if(Integer.parseInt(parameterMap.get("number")[0]) < t.getNumber())
                                return true;
                            else continue;
                        }
                        else return true;
                    }
                    else continue;
                }
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param parameterMap
     * @return
     */
    private boolean areParametersCorrect(Map<String, String[]> parameterMap) {
        String[] tempValidParameters = new String[] { "projectid", "country", "number","keyword" };
        Set<String> validParameters = new HashSet<String >(Arrays.asList(tempValidParameters));
        Set<String> parameters = parameterMap.keySet();
        return validParameters.containsAll(parameters);
    }

    /**
     *
     * @param project
     * @return
     */
    private boolean passesAllBasicRules(Project project) {
        if(!project.isValid() || project.getEnabled().equals(false) || isExpired(project.getExpiryDate()) || project.getProjectUrl().equals(null))
            return false;
        return true;
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
