import lombok.Data;
import java.util.ArrayList;

/**
 * Created by Karthik on 5/27/17.
 */

@Data
public class Project {
    private int id;
    private String projectName;
    private String creationDate;
    private String expiryDate;
    private Boolean enabled;
    private ArrayList<String> targetCountries = new ArrayList<>();
    private double projectCost;
    private String projectUrl;
    private ArrayList<TargetKeys> targetKeys = new ArrayList<>();

    public boolean isValid() {
        return id>0 && projectName!=null && !projectName.isEmpty() && creationDate!=null && !creationDate.isEmpty() &&
                expiryDate!=null && !expiryDate.isEmpty() && enabled!=null &&targetCountries!=null && !targetCountries.isEmpty() &&
                projectCost>=0 && projectUrl!=null && !projectUrl.isEmpty();
    }
}

@Data
class TargetKeys {
    TargetKeys(TargetKeys targetKeys) {
        this.number = targetKeys.number;
        this.keyword = targetKeys.keyword;
    }
    private int number;
    private String keyword;
}

