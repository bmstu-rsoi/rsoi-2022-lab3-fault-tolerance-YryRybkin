FROM openjdk:18 as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} payment.jar
RUN java -Djarmode=layertools -jar payment.jar extract

FROM openjdk:18
ENV PORT=8060
EXPOSE 8060
WORKDIR application
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Xmx512m", "-Xms512m", "org.springframework.boot.loader.JarLauncher"]