FROM amazoncorretto:21.0.1 AS build
WORKDIR /var/ip-worker
ADD . .
RUN ./gradlew clean build -x test

# Run stage
FROM amazoncorretto:21.0.1
WORKDIR /var/ip-worker
COPY --from=build /var/ip-worker/build/libs/CentralConnector-24Dec-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

#ENTRYPOINT ["java", "-jar", "app.jar"]
ENTRYPOINT java -jar -DtaskToDomain=$taskToDomain app.jar
