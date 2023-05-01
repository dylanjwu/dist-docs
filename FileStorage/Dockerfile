FROM maven:3.8.3-openjdk-17


WORKDIR /app

COPY . .


RUN mvn clean package

# RUN chmod +x start_up.sh

ENTRYPOINT ["java", "-jar", "/app/target/coordinator-1.0.0.jar"]


