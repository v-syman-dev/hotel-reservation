FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd -ms /bin/bash javauser
USER javauser

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", "app.jar"]

EXPOSE 8080
