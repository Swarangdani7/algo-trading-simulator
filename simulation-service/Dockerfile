####### Stage-1 : Build the application #########
#Setting the Java Runtime
FROM maven:3.9.9-eclipse-temurin-17 AS builder
#Setting working directory inside the container
WORKDIR /build
#Copying dependencies to the container working directory
COPY pom.xml .
COPY api-gateway ./api-gateway
COPY proto-contract ./proto-contract
COPY market-service ./market-service
COPY strategy-service ./strategy-service
COPY portfolio-service ./portfolio-service
COPY simulation-service ./simulation-service
COPY discovery-server ./discovery-server

RUN mvn -pl ./simulation-service -am clean package -DskipTests

###### Stage-2 : Run the application #########
FROM eclipse-temurin:17-jre-alpine
#Setting working directory inside the container
WORKDIR /app
#Copying the jar file created during build stage
COPY --from=builder build/simulation-service/target/*.jar app.jar
#Exposing the Port on which our server runs
EXPOSE 8084
#Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


