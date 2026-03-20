FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk21-temurin

COPY --from=builder /app/target/UrlShortener-1.0-SNAPSHOT.war \
     /usr/local/tomcat/webapps/ROOT.war

# To build execute: docker build -t urlshortener:urlshortener .

