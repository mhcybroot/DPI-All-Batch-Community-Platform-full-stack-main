FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/libs
COPY --from=build ${DEPENDENCY}/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
