FROM gradle:7.5.1-jdk17  as builder
USER root

ADD . .

RUN ["gradle", "build", "-x", "test", "--stacktrace"]


FROM eclipse-temurin:17.0.4.1_1-jre-jammy
USER root

RUN mkdir /opt/app-root
WORKDIR /opt/app-root
RUN chmod g+w /opt/app-root
COPY --from=builder /home/gradle/build/container .
EXPOSE 8080

ENV TZ="Asia/Bangkok"

ENTRYPOINT ["java", "-jar", "boilerplate.jar"]
