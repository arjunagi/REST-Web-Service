# REST Based Webservice

## Environment and Additional Libraries Used
* Java 8
* Maven
* Spark Java http://sparkjava.com/
* Gson
* Lombok https://projectlombok.org/
* log4j http://logging.apache.org/log4j/1.2/
* slf4j 1.7.25 https://www.slf4j.org/

## Running the Webservice
1. Clone the project.
2. In command line, navigate to REST-Web-Service/ and execute the following to start the service
```
java -jar RestWebService.jar
```
3. Send the POST or GET requests (use url http://localhost:5000/)
4. The projects will be stored in the file "projects.txt" in the same location as the jar
5. The server logs will be stored in the file "web_service_logs.txt" in the same location as the jar

## Testing the Webservice Using Postman
* Import the file "TestRESTWebService.postman_collection.json" to Postman (https://www.getpostman.com/). This will create a "TestRESTWebService" folder in Postman.
* Keep the server running.
### Sending Large Number of createproject Requests
1. Click on Runner and choose the collection TestRESTWebService -> POST - Many successful request
2. Set Iterations as 1000
3. Start Run
### Running All Testcases
1. Click on Runner and choose the collection TestRESTWebService
2. Start Run

If you want to run individual folders:
### Sending PUT request
1. Run the "PUT request" testcase.
### Sending createProject with Json Array
1. Click on Runner and choose the collection TestRESTWebService -> Post - Json Array
2. Start Run
### Sending Invalid Data in createproject Requests
1. Click on Runner and choose the collection TestRESTWebService -> POST - Invalid Requests
2. Start Run
### Sending Invalid parameters in requestproject Requests
1. Click on Runner and choose the collection TestRESTWebService -> GET - Invalid Requests
2. Start Run
### Sending Valid Data in createproject Requests
1. Click on Runner and choose the collection TestRESTWebService -> GET - Valid Requests
2. Start Run

### Test Result
Import the file "TestRESTWebService.postman_test_run.json" to Postman (Runner -> Import Test Run)
