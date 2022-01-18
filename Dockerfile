FROM eclipse-temurin:17
WORKDIR /app


ADD netstats-*.jar ./app.jar

ENV TZ="Europe/Budapest"
RUN date



ENTRYPOINT ["java","-Dspring.datasource.url=jdbc:postgresql://<DB_HOST>:5432/<DB_DB>?user=<DB_USER>&password=<DB_PASSWORD>&ApplicationName=<DB_APP_NAME>","-jar","app.jar"]