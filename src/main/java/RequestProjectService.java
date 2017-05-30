import com.google.gson.Gson;
import spark.Request;
import java.io.BufferedReader;;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class to handle requestproject request.
 */
public class RequestProjectService {

    //Map of parameters and its values.
    private Map<String, String[]> parameterMap = new HashMap<>();

    /**
     * If request is valid, searches for project based on rules.
     * @param request
     * @return project data if found
     *         403 for invalid parameters
     *         500 for errors during file read
     */
    public Response requestProject(Request request) {

        this.parameterMap = request.queryMap().toMap();

        // No parameters - get project with highest cost
        if(this.parameterMap.isEmpty())
            return getHighestCostProject();

        if(!areParametersCorrect())
            return new ResponseMessageWithStatusCode("Invalid parameter in the request", 403);

        return getProject();
    }

    /**
     * Get project based on parameters and rules
     * @return project data if found or else appropriate messages with status codes.
     */
    private Response getProject() {

        if(this.parameterMap.keySet().contains("projectid"))
            return getProjectBasedOnId();
        else
            return getProjectsBasedOnParams();
    }

    /**
     * Get the project with the highest cost amongst all the stored projects.
     * @return project data if found or else appropriate messages with status codes.
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
     * Get the project with the given project id.
     * @return project data if found or else appropriate messages with status codes.
     */
    private Response getProjectBasedOnId() {

        try(BufferedReader br = new BufferedReader(new FileReader("projects.txt"))) {
            int projectId = Integer.parseInt(this.parameterMap.get("projectid")[0]);
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
     * Get project based on the parameters - country, number, keyword.
     * @return project data if found or else appropriate messages with status codes.
     */
    private Response getProjectsBasedOnParams() {

        try(BufferedReader br = new BufferedReader(new FileReader("projects.txt"))) {
            Project highestCostProject = null;
            double highestCost = Double.MIN_VALUE;

            for(String line; (line = br.readLine()) != null; ) {
                Project project = new Gson().fromJson(line, Project.class);
                if(passesAllBasicRules(project) && checkParameterValueMatches(project)) {
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
     * Checks if the parameter values match the corresponding values of the project.
     * @param project
     * @return true if the values match, else false.
     */
    private boolean checkParameterValueMatches(Project project) {

        for(String p: this.parameterMap.keySet()) {

            if(p.equals("country")) {
                if(! project.getTargetCountries().contains(this.parameterMap.get(p)[0].toUpperCase()))
                    return false;
            }

            /**
             * If parameter has number and the value matches, check for matching of keyword (if that was part of request parameter).
             * If number matches and keyword doesn't, ignore this TargetKey object.
             */
            else if(p.equals("number")) {
                for (TargetKeys t : project.getTargetKeys()) {
                    if(Integer.parseInt(this.parameterMap.get(p)[0]) <= t.getNumber()) {
                        if(this.parameterMap.keySet().contains("keyword")) {
                            if(t.getKeyword().equals(this.parameterMap.get("keyword")[0]))
                                return true;
                            else continue;
                        }
                        else return true;
                    }
                    else continue;
                }
                return false;
            }

            /**
             * If parameter has keyword and the value matches, check for matching of number (if that was part of request parameter).
             * If number matches and keyword doesn't, ignore this TargetKey object.
             */
            else if(p.equals("keyword")) {
                for (TargetKeys t : project.getTargetKeys()) {
                    if(t.getKeyword().equals(this.parameterMap.get(p)[0])) {
                        if(this.parameterMap.keySet().contains("number")) {
                            if(Integer.parseInt(this.parameterMap.get("number")[0]) < t.getNumber())
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
     * Check if the project passes all the basic rules:
     * 1. Project data is valid
     * 2. Project is enabled
     * 3. Project is not expired
     * 4. Project URL is not null
     * @param project
     * @return true if project passes all these rules, else false.
     */
    private boolean passesAllBasicRules(Project project) {
        if(!project.isValid() || !project.isEnabled() || isExpired(project.getExpiryDate()) || project.getProjectUrl().equals(null))
            return false;
        return true;
    }

    /**
     * Check if the parameters are from the list of accepted parameters.
     * @return true if parameters are correct, else false.
     */
    private boolean areParametersCorrect() {
        String[] tempValidParameters = new String[] { "projectid", "country", "number","keyword" };
        Set<String> validParameters = new HashSet<String >(Arrays.asList(tempValidParameters));
        Set<String> parameters = this.parameterMap.keySet();
        for(String p : parameters) {
            if(this.parameterMap.get(p)[0].equals("") || !validParameters.contains(p))
                return false;
        }
        return true;
    }

    /**
     * Check if expiry date has passed.
     * @param ed expiry date stored in project
     * @return true if expired, else false.
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
