# Overview
This is mobile translate graphical client written in javafx

This client allows easy way to localize android and ios strings for
mobile development

The client requires the core module to be running since it acts 
as the server
# Features
- login and registration
- Easy submission of localization tasks
- preview of localized strings
- freighting of localized strings
- Light / Dark theme support
 # System Requirements ,building and running
At least java 22 and maven to build and  run the application

Execute the  next command to build

```
mvn clean package 
```
An uber jar will be created at /target/fatjar

To run the application run the command

```
java --add-opens java.base/java.lang=ALL-UNNAMED -jar desktop-<version>.jar
```


