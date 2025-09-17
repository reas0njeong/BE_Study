# syntax=docker/dockerfile:1

# 1) 빌드 스테이지
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY gradlew gradle/ ./        # gradle wrapper
COPY build.gradle settings.gradle ./
COPY src ./src
RUN chmod +x gradlew
# 테스트 오래 걸리면 -x test
RUN ./gradlew bootJar --no-daemon -x test

# 2) 런타임 스테이지(슬림 JRE)
FROM eclipse-temurin:21-jre
WORKDIR /app
# 만들어진 JAR 복사(파일명이 바뀌어도 와일드카드로 커버)
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
