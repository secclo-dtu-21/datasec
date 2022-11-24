# Description

## Configuration

Based on the second lab, the lab added the access control functions. Vital components are as follows:
- **service.properties**

  Configure the `accessControlModel` item to pinpoint which concrete access control mechanism to use.

  ```properties
  # Role based access control
  accessControlModel = roleBasedAccessControl
  
  # AccessControlList
  accessControlModel = accessControlList
  ```

## Tests

- **AccessControListTest**

  Test whether the access control list mechanism can function properly.

- **StaffChangeOnAccessControlListTest**

  Test whether the access control list can adpat the change of employees and their permissions.

- **RoleBasedControlTest**

  Test whether the role base access control mechanism can function properly.

- **StaffChangeOnRoleBasedControlTest**

  Test whether the role base access control mechanism adpat the change of employees and their permissions.

## Logs

- **Log4j2** (AuthLab/log4j2)

  Store the server logs.

  Example:

  ```
  2022-11-15 15:29:08 INFO  User 'Alice' authenticate successfully
  2022-11-15 15:29:08 INFO  User 'Alice' is requesting the use of service 'setConfig'
  2022-11-15 15:29:09 INFO  User 'Alice' is allowed to use service 'setConfig'
  2022-11-15 15:29:09 INFO  The 'paramAlice' configuration has been set to 'Alice'
  2022-11-15 15:29:09 INFO  User 'Alice' is requesting the use of service 'readConfig'
  2022-11-15 15:29:09 INFO  User 'Alice' is allowed to use service 'readConfig'
  2022-11-15 15:29:09 INFO  The value of the paramAlice configuration is Alice.
  2022-11-15 15:29:09 INFO  User 'Alice' is requesting the use of service 'stop'
  2022-11-15 15:29:09 INFO  User 'Alice' is allowed to use service 'stop'
  2022-11-15 16:44:44 INFO  User 'Alice' authenticate successfully
  2022-11-15 16:44:44 INFO  User 'Alice' is requesting the use of service 'start'
  2022-11-15 16:44:45 INFO  User 'Alice' is allowed to use service 'start'
  2022-11-15 16:44:45 INFO  User 'George' authenticate successfully
  2022-11-15 16:44:45 INFO  User 'George' is requesting the use of service 'print'
  2022-11-15 16:44:45 INFO  User 'George' is allowed to use service 'print'
  2022-11-15 16:44:45 INFO  printer1-file_George has been added to the printing queue
  2022-11-15 16:44:45 INFO  User 'George' is requesting the use of service 'status'
  2022-11-15 16:44:46 INFO  User 'George' is allowed to use service 'status'
  2022-11-15 16:44:46 INFO  printer1-There are 1 tasks in the Queue
  2022-11-15 16:44:46 INFO  User 'Henry' authenticate successfully
  2022-11-15 16:44:46 INFO  User 'Henry' is requesting the use of service 'print'
  2022-11-15 16:44:46 INFO  User 'Henry' is allowed to use service 'print'
  2022-11-15 16:44:46 INFO  printer1-file_Henry has been added to the printing queue
  2022-11-15 16:44:46 INFO  User 'Henry' is requesting the use of service 'stop'
  2022-11-15 16:44:47 INFO  User 'Henry' is forbidden to use service 'stop'
  2022-11-15 16:44:47 INFO  User 'Ida' authenticate successfully
  2022-11-15 16:44:47 INFO  User 'Ida' is requesting the use of service 'print'
  2022-11-15 16:44:47 INFO  User 'Ida' is allowed to use service 'print'
  2022-11-15 16:44:47 INFO  printer1-file_Ida has been added to the printing queue
  2022-11-15 16:44:47 INFO  User 'Ida' is requesting the use of service 'restart'
  2022-11-15 16:44:48 INFO  User 'Ida' is allowed to use service 'restart'
  2022-11-15 16:44:48 INFO  printer1-The Queue is cleared
  2022-11-15 16:44:48 INFO  printer2-The Queue is cleared
  2022-11-15 16:46:06 INFO  User 'Alice' authenticate successfully
  2022-11-15 16:46:06 INFO  User 'Alice' is requesting the use of service 'start'
  2022-11-15 16:46:07 INFO  User 'Alice' with role 'manager' is allowed to use service 'start'
  2022-11-15 16:46:07 INFO  User 'George' authenticate successfully
  2022-11-15 16:46:07 INFO  User 'George' is requesting the use of service 'print'
  2022-11-15 16:46:08 INFO  User 'George' with role 'ordinary_user&service_tech' is allowed to use service 'print'
  2022-11-15 16:46:08 INFO  printer1-file_George has been added to the printing queue
  2022-11-15 16:46:08 INFO  User 'George' is requesting the use of service 'status'
  2022-11-15 16:46:09 INFO  User 'George' with role 'ordinary_user&service_tech' is allowed to use service 'status'
  2022-11-15 16:46:09 INFO  printer1-There are 1 tasks in the Queue
  2022-11-15 16:46:09 INFO  User 'Henry' authenticate successfully
  2022-11-15 16:46:09 INFO  User 'Henry' is requesting the use of service 'print'
  2022-11-15 16:46:10 INFO  User 'Henry' with role 'ordinary_user' is allowed to use service 'print'
  2022-11-15 16:46:10 INFO  printer1-file_Henry has been added to the printing queue
  2022-11-15 16:46:10 INFO  User 'Henry' is requesting the use of service 'stop'
  2022-11-15 16:46:10 INFO  User 'Henry' with role 'ordinary_user' is forbidden to use service 'stop'
  2022-11-15 16:46:11 INFO  User 'Ida' authenticate successfully
  2022-11-15 16:46:11 INFO  User 'Ida' is requesting the use of service 'print'
  2022-11-15 16:46:11 INFO  User 'Ida' with role 'power_user' is allowed to use service 'print'
  2022-11-15 16:46:11 INFO  printer1-file_Ida has been added to the printing queue
  2022-11-15 16:46:11 INFO  User 'Ida' is requesting the use of service 'restart'
  2022-11-15 16:46:12 INFO  User 'Ida' with role 'power_user' is allowed to use service 'restart'
  2022-11-15 16:46:12 INFO  printer1-The Queue is cleared
  2022-11-15 16:46:12 INFO  printer2-The Queue is cleared
  ```

# How to Run the Application

1. Open IntelliJ Idea and import the project.
2. Install all the dependencies defined in `build.gradle`. This should be down by Idea automatically.
3. Run the class `ApplicationServer.java` under the package dk.dtu.server, which starts the printing service.
4. Run the tests under the test package `AccessControl` (src/test/java/AccessControl). Remember to configure the corresponding access control mechanism in the server.properties file.
5. If you want to check the logs, go to the folder `log4j2/logs`, the latest logs are in the file `auth.log`. Old logs will be achived according to date. (You can also check our old logs there)