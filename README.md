# Mancala
This project  is a Spring/Angular web application to play Mancala using websockets.

## Installation
-  Make sure you have Node.JS installed and update the node and npm versions in `mancala-angular/build.gradle` to match yours.
-  Install the angular cli globally: `npm i @angular/cli -g`

## Running the application
Both the angular app and api server are served from the Spring backend.
To start the application, run the following from the root folder:

UNIX:
```
./gradlew bootRun
```

Windows:
```
gradlew.bat bootRun
```

## To Do's
The following items, I would like to have done but didn't have time for unfortunately:
-  Writing Unit tests
-  Setting up a CI/CD pipeline