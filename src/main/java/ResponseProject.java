import lombok.Data;

/**
 * Created by Karthik on 5/28/17.
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
