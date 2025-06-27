FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y iputils-ping
COPY target/admin.jar admin.jar
ENTRYPOINT ["java", "-jar", "admin.jar"]