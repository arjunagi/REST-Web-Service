import com.google.gson.*;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import java.io.StringReader;
import static spark.Spark.*;

/**
 * Main class where handling of the POST and GET requests start.
 */

public class Main {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(Main.class);
        CreateProjectService createProjectService = new CreateProjectService();
        RequestProjectService requestProjectService = new RequestProjectService();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        port(5000);

        /**
         * Perform these actions before processing every request.
         */
        before((request, response) -> {
            if(!request.requestMethod().equals("POST") && ! request.requestMethod().equals("GET")) {
                response.status(405);
                response.header("Allow", "GET, POST");
                response.body(gson.toJson(new ResponseMessageWithStatusCode("Invalid request method", response.status())));
            }
            String params = "";
            if(request.queryMap().hasKeys()) {
                params = getFormattedParameters(request);
            }
            logger.info("\n" + request.requestMethod() + " " + request.url() + params + request.body() +"\n");
        });

        /**
         * Perform these actions after processing every request.
         */
        after((request, response) -> {
            logger.info("\n" + response.raw().toString() + response.body()+ "\n");
        });

        /**
         * Process the POST request
         */
        post("/createproject", "application/json", (request, response) -> {
            try {
                response.type("application/json");
                JsonReader reader = new JsonReader(new StringReader(request.body()));
                ResponseMessageWithStatusCode responseMessageWithStatusCode = null;

                /**
                 * Check if the body contains an array of Json or a single Json object
                 * and process accordingly.
                 */
                if(reader.peek() == JsonToken.BEGIN_OBJECT) {
                    responseMessageWithStatusCode = createProjectService.createSingleProject(request);
                    response.status(responseMessageWithStatusCode.getStatusCode());
                }
                else if(reader.peek() == JsonToken.BEGIN_ARRAY) {
                    responseMessageWithStatusCode = createProjectService.createMultipleProjects(request);
                    response.status(responseMessageWithStatusCode.getStatusCode());
                }
                else {
                    response.status(400);
                    responseMessageWithStatusCode = new ResponseMessageWithStatusCode("Data is invalid", response.status());
                }
                return gson.toJson(responseMessageWithStatusCode);
            } catch (JsonParseException e) {
                response.status(400);
                return gson.toJson(new ResponseMessageWithStatusCode(e, 400));
            }
        });

        /**
         * Process the GET request.
         * Response can be of two types:
         * 1. Just message when project is found/not found
         * 2. Message with status code for error scenarios
         */
        get("/requestproject", "application/json", (request, response) -> {
            Object responseMessage = null;
            response.type("application/json");
            responseMessage = requestProjectService.requestProject(request);
            if(responseMessage instanceof ResponseMessageWithStatusCode) {
                response.status(((ResponseMessageWithStatusCode) responseMessage).getStatusCode());
            }
            return gson.toJson(responseMessage);
        });
    }

    /**
     * Get the parameters in the request and return it in the URL format.
     * @param request
     * @return parameters in URL format. Example: "?country=india&number=30&keyword=xyz"
     */
    private static String getFormattedParameters(Request request) {
        StringBuilder formattedParams = new StringBuilder();
        formattedParams.append("?");

        for(String k : request.queryMap().toMap().keySet()) {
            formattedParams.append(k + "=" + request.queryParams(k)+"&");
        }
        // length()-1 to ignore the "&" appended in the end.
        return formattedParams.substring(0,formattedParams.length()-1).toString();
    }
}
