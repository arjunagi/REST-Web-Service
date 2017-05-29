/**
 * Created by Karthik on 5/25/17.
 */

import com.google.gson.*;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import java.io.StringReader;
import java.util.Map;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(Main.class);
        CreateProjectService createProjectService = new CreateProjectService();
        RequestProjectService requestProjectService = new RequestProjectService();
        Gson gson = new Gson();
        port(5000);

        before((request, response) -> {
            logger.info(request.requestMethod() + "\n" +request.body());
        });

        after((request, response) -> {
            logger.info(response.raw().toString() + response.body()+ "\n");
        });

        post("/createproject", "application/json", (request, response) -> {
            try {
                response.type("application/json");
                JsonReader reader = new JsonReader(new StringReader(request.body()));
                ResponseMessageWithStatusCode responseMessageWithStatusCode = null;

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
                    responseMessageWithStatusCode = new ResponseMessageWithStatusCode("Data is invalid", 400);
                }
                return gson.toJson(responseMessageWithStatusCode);
            } catch (JsonParseException e) {
                return gson.toJson(new ResponseMessageWithStatusCode(e, 400));
            }
        });

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
}
