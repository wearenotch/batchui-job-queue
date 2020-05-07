# batchui-job-queue

Database based Job Queue for the **batch-ui** project. 
This project produces single jar that periodically checks if there
are manually scheduled batch jobs and if finds some, starts their execution.

**Table of contest**
1. Usage
2. Development
3. Changelog
4. Credits 

## 1. Usage
For detailed instruction on how to use this libarary please see main **batch-ui** docs. 
In practice using this library requires to simply add this jar to your project and possibly configuring it to suit your needs.


### Configuration
Library can be customized by configuring the following variables in the appropriate spring application.yml or application.properties file.

```
batchui.dbqueue.poll-interval=30 # poll interval in seconds
 
```
## 2. Development

### Requirements
- [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)


### Setup (First time)

1. Clone the repository: `git clone git@bitbucket.org:ag04/ag04-jpaqueue.git`
2. Build project with: ` ./gradlew clean build `

### Release

Make sure that file gradle.properties in folder ${USER_HOME}/.gradle/ contains the following two variables defined

- ag04_nexus_username
- ag04_nexus_password

1) Commit everything
2) ./gradlew release

And simply follow the instructions on the console

## 3. Changelog

### v. 0.1.0 - Base implementation

## 4. Credits
- Domagoj Madunić
- Vjeran Marčinko (*)

(*) Core of this library is jpa queue mechanism described by Vjeran Marčinko in the following articles:
- [Part 1: Practical queueing using SQL - Rationale and general design](https://medium.com/agency04/practical-queueing-using-sql-part-1-rationale-and-general-design-d180d6848030)
- [Part 2: Practical queueing using SQL - Do it simply using Spring Boot and JPA](https://medium.com/agency04/practical-queueing-using-sql-part-2-do-it-simply-using-spring-boot-and-jpa-e9cb53f91f36)

For the simplicity of the use of this jar, classes from the article are reproduced here, so that client app need to import only this jar.