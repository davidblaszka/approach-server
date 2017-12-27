

FROM openjdk:8u151-jre-alpine

WORKDIR /theapproach

COPY target/scala-2.12/approach-server-assembly-1.0.jar server-jar.jar

ENTRYPOINT java -jar server-jar.jar