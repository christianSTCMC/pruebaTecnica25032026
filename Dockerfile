# Etapa de construccion: compila el artefacto con Maven Wrapper para no depender de Maven instalado en el host.
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -q -DskipTests clean package

# Etapa de ejecucion: usa JRE para reducir tamano e iniciar solo el jar generado.
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

COPY --from=build /workspace/target/api-franquicias-*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
