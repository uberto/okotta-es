# How to Build and Run

* Make sure you have a java jdk installed, version 8 or later
* Get the code e.g. `git clone git@github.com:uberto/okotta-es.git`
* Build using gradle wrapper (this will auto-download gradle and all dependencies) e.g. 
```
cd <directory-where-code-has-been-checked-out-from-git>
./gradlew clean build
```
* To Run the helpdesk example web-application:
```
./gradlew :example-ticketing:run
```
