## Redis - File-references Coding Challenge

### Test files
Test files can be found at `src/main/resources/input`. However, the path for the folder containing the test files can 
be configured in the `src/main/resources/application.properties` file.  

### Compiling the application
Open a terminal in the root folder and run `mvn clean install`.

### Running the application
Open a terminal in the root folder and run `java -jar target/cs-demo-0.0.1-SNAPSHOT.jar`.
By default, the application starts on port 8084, but the port can be configured in the 
`src/main/resources/application.properties` file.  
Once the application starts, the Swagger interface can be accessed at http://localhost:8084/swagger-ui/

### Redis data structures
The application uses three types of Redis keys:

* `task:<task-id>` (the value is a Hash that stores the status, results, creationDate, file-id and ID of a task)

* `children:<file-id>` (the value is a Set that stores the children of a file)

* `parents:<file-id>` (the value is a Set that stores the parents of a file)
