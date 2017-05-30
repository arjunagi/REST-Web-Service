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
1. Download the UnityRestWebService.jar
2. Go to command line and execute the following to start the service
```
java -jar UnityRestWebService.jar
```
3. Send the POST or GET requests (http://localhost:5000/)
4. The projects will be stored in the file "projects.txt" in the same location as the jar
5. The server logs will be stored in the file "web_service_logs.txt" in the same location as the jar

## Testing the Webservice Using Postman
Import the file "TestRESTWebService.postman_collection.json" to Postman. This will create a "TestRESTWebService" folder in Postman.
### Sending Large Number of createproject Requests
1. Click on Runner and choose the collection TestRESTWebService -> POST - Many successful request
2. Set Iterations as 1000
3. Start Run
### Sending Invalid Data in createproject Requests
1. Click on Runner and choose the collection TestRESTWebService -> POST - Invalid Requests
2. Start Run
