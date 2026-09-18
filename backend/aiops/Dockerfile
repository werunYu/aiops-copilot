FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21.0.12_8-jre
ENV TZ=Asia/Shanghai
WORKDIR /app
COPY --from=build /workspace/target/aiops.jar app.jar
EXPOSE 10805
ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "/app/app.jar"]
