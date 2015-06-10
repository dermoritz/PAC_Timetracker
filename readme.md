# Pac Timetracker

This application is arose within the "Prodyna Architecture Certificate". It demonstrates the use of JavaEE 7 technologies to provide a RESTful back-end service in conjunction with a pure Html/Js (AngularJs, BootstrapCss) front-end leveraging the rest interface.

## Gitflow

This repository uses [GitFlow](http://nvie.com/posts/a-successful-git-branching-model/) as development model. In short it means: To get last snapshot you pull branch **develop** and to get last release (including last hotfix) you pull **master**. On master all previous releases are tagged.

## How to build

This application is a multi module maven project (you need [maven setup and running](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)). There are 2 modules:

- **timetracker-backend**, contains JavaEE 7 application packaged as war
- **timtracker-web**, pure html/js project packaged as war

The build of both modules could be commenced from parent timetracker/pom:
 
 ```
 mvn clean package
 ```
 
 This will build both war files. During build of backend a Wildfly application server will be downloaded to commence all tests.
 
## How to deploy

### Prerequisites

This application was tested on Wildfly. I will describe the deployment process for Wildfly. Since the application uses pure JavaEE 7 it should also run on other JavaEE 7 compliant Servers.

#### Database

The application relies on a datasource with path: `java:jboss/datasources/timetracker`. It should be a MySQL database since DB-Dialect is set to MySql for Schemageneration.

#### Security

The application relies on basic authentication from security domain "other". This one is set by default. The application will accept users of every role but user with role "ADMIN" will get admin permissions. The admin role could not be detracted from within application. So it is not possible to (accidentally) lock administrators out. To detract administrative right you must detract the role within security domain.

### Deployment

Deploy the war files (one in each target folder) as usual. For wildfly you could use the gui (http://localhost:9990/console/App.html#deployments). You could reach the frontend via <wildflyadress:ip>/timetracker-web the rest services are exposed via <wildflyadress:ip>/timetracker-backend/timetracker/...



 
 
 
 