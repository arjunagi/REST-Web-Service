import lombok.Data;

/**
 * Class to create the response message with project data.
 */
@Data
public class ResponseProject extends Response{

    private String projectName;
    private double projectCost;
    private String projectUrl;

    public ResponseProject(String projectName, double projectCost, String projectUrl) {
        this.projectName = projectName;
        this.projectCost = projectCost;
        this.projectUrl = projectUrl;
    }

}
