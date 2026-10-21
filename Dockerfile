    FROM maven:3.9-eclipse-temurin-21 AS build
    WORKDIR /app
    COPY pom.xml .
    RUN mvn -q dependency:go-offline
    COPY src ./src
    RUN mvn -q clean package -DskipTests

    FROM eclipse-temurin:21-jre
    RUN apt-get update \
        && apt-get install -y --no-install-recommends fontconfig \
        && rm -rf /var/lib/apt/lists/*
    ENV TZ=America/Sao_Paulo
    ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -Duser.timezone=America/Sao_Paulo"
    WORKDIR /app
    COPY --from=build /app/target/*.jar app.jar
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "app.jar"]