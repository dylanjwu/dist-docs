FROM openjdk:17

WORKDIR /app

COPY . .

RUN ./mvnw package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/postgre-0.0.1-SNAPSHOT.jar"]

# ENTRYPOINT ["java",  "-D", "RMI_SERVER_HOST=my-server-1", "-D", "RMI_SERVER_PORT=1111", "-jar", "target/postgre-0.0.1-SNAPSHOT.jar"]






