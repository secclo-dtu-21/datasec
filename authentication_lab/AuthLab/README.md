# Decription
This project is a preliminary simulation of a printing service integrated a databased based authentication system. Vital components are as follows:

- **DatabaseTest** (test/java/DatabaseTest.java) :

  Unit test cases of the database manipulation function.

- **AuthenticationTest** (test/java/AuthenticationTest.java):

  Integration test cases of the authentication function.

- **ApplicationServer** (package dk.dtu.server):

  The class to launch the printing service.

- **Client** (package dk.dtu.client):

  Simulate a typical use case of the printing service.

- **Log4j2** (AuthLab/log4j2)

  Stores the server logs. 

  Example:
  
  ```
  2022-11-04 18:03:57 INFO  User 'user1' authenticate successfully
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'start'
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'print'
  2022-11-04 18:03:57 INFO  printer1-test1.txt has been added to the printing queue
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'print'
  2022-11-04 18:03:57 INFO  printer1-test2.txt has been added to the printing queue
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'print'
  2022-11-04 18:03:57 INFO  printer1-test3.txt has been added to the printing queue
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'print'
  2022-11-04 18:03:57 INFO  printer2-test4.txt has been added to the printing queue
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'print'
  2022-11-04 18:03:57 INFO  printer1-test5.txt has been added to the printing queue
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'queue'
  2022-11-04 18:03:57 INFO  printer1-The current tasks are: 
  <1> <test1.txt>
  <2> <test2.txt>
  <3> <test3.txt>
  <4> <test5.txt>
  
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'topQueue'
  2022-11-04 18:03:57 INFO  printer1-The 3th job has been moved to the top
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'queue'
  2022-11-04 18:03:57 INFO  printer1-The current tasks are: 
  <1> <test3.txt>
  <2> <test1.txt>
  <3> <test2.txt>
  <4> <test5.txt>
  
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'status'
  2022-11-04 18:03:57 INFO  printer1-There are 4 tasks in the Queue
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'restart'
  2022-11-04 18:03:57 INFO  printer1-The Queue is cleared
  2022-11-04 18:03:57 INFO  printer2-The Queue is cleared
  2022-11-04 18:03:57 INFO  User 'user1' authenticate successfully
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'queue'
  2022-11-04 18:03:57 INFO  printer1-The current tasks are: 
  None
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'queue'
  2022-11-04 18:03:57 INFO  printer2-The current tasks are: 
  None
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'setConfig'
  2022-11-04 18:03:57 INFO  The 'printers' configuration has been set to '2'
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'readConfig'
  2022-11-04 18:03:57 INFO  The value of the printers configuration is 2.
  2022-11-04 18:03:57 INFO  User 'user1' is requesting the use of service 'stop'
  
  ```
  
  The above logs records the running trace of operations described in the client class.

# How to Run the Application

1. Open IntelliJ Idea and import the project.
2. Install all the dependencies defined in `build.gradle`.

3. Run the class `ApplicationServer.java` under the package dk.dtu.server, which start the printing service.
4. Run the class `Client.java` under the package dk.dtu.client, which sinulates the operations that could be launched by the user.
5. The test are under the test/java directory, to run the AuthenticationTest, please run the ApplciationServer class first.
