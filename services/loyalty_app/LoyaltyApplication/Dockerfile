FROM openjdk:18 as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} loyalty.jar
RUN java -Djarmode=layertools -jar loyalty.jar extract

FROM openjdk:18
ENV PORT=8050
EXPOSE 8050
WORKDIR application
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Xmx512m", "-Xms512m", "org.springframework.boot.loader.JarLauncher"]