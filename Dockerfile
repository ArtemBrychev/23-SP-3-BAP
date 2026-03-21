FROM tomcat:9.0-jdk21-temurin

COPY UrlShortener-1.0-SNAPSHOT.war \
     /usr/local/tomcat/webapps/ROOT.war

# To build execute: docker build -t urlshortener:urlshortener .

