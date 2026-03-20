## Этап сборки — используем Maven образ с нужной версией JD
#FROM maven:3.9.8-eclipse-temurin-21 AS builder
#WORKDIR /app
#COPY . .
#RUN mvn clean package -DskipTests
#
## Этап запуска — лёгкий образ с JRE
#FROM eclipse-temurin:21-jre
#WORKDIR /app
#COPY --from=builder /app/target/*.jar app.jar
#EXPOSE 8080d
#ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk21-temurin

COPY --from=builder /app/target/UrlShortener-1.0-SNAPSHOT.war \
     /usr/local/tomcat/webapps/ROOT.war

# To build execute: docker build -t urlshortener:urlshortener .

