FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/bff-agendador-0.0.1-SNAPSHOT.jar /app/bff-agendador-tarefas.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/bff-agendador-tarefas.jar"]
