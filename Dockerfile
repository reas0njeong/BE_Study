# syntax=docker/dockerfile:1

# 1) Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# gradle wrapper 복사 (분리해서 안전하게)
COPY gradlew gradlew.bat ./
COPY gradle ./gradle

# 프로젝트 파일 복사
COPY build.gradle settings.gradle ./
COPY src ./src

RUN chmod +x gradlew
# 테스트 오래 걸리면 -x test 유지
RUN ./gradlew bootJar --no-daemon -x test

# 2) Runtime stage (JRE)
FROM eclipse-temurin:21-jre
WORKDIR /app
# 만들어진 JAR 복사 (스냅샷 파일명 커버)
COPY --from=build /app/build/libs/*SNAPSHOT*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
