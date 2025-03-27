# Сборка приложения
FROM jelastic/maven:3.9.5-openjdk-21 as build
WORKDIR /app
COPY target/jira-1.0.jar app-jira.jar
COPY resources ./resources

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app-jira.jar", "--spring.profiles.active=prod"]