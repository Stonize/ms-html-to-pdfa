# build stage build the jar with all our resources
FROM maven:3.6-jdk-11-openj9 as build
RUN mkdir -p /app
WORKDIR /app
COPY pom.xml /app
RUN mvn dependency:go-offline -B

COPY ./src /app/src
RUN mvn install package

FROM gcr.io/distroless/java:11
# EXPOSE 9101
# WORKDIR /a
# RUN mkdir -p /app
ENV TEMPLATES_DIR /templates
COPY ./src/main/resources/templates/test.html /templates/test.html

COPY --from=build /app/target/ms-html-to-pdfa*.jar /ms-html-to-pdfa.jar
COPY ./src/main/properties/dev.yml /config.yml
ENTRYPOINT [ "java", "-jar", "/ms-html-to-pdfa.jar", "server", "/config.yml" ]
