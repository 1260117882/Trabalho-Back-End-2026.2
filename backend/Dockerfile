# Build
# Imagem com Maven + JDK 17 (mesma versão do pom.xml) para compilar o projeto
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copia só o pom primeiro: o Docker reaproveita o cache das dependências
# enquanto o pom.xml não mudar (builds seguintes ficam bem mais rápidos)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o código e gera o .jar
# -DskipTests: o teste contextLoads precisa de um banco, que não existe durante o build
COPY src ./src
RUN mvn -B package -DskipTests

# Execução
# Imagem final só com o JRE: menor e sem Maven/código-fonte
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Traz apenas o .jar gerado no estágio anterior
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
