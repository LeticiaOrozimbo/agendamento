# Multi-stage Dockerfile para Sistema de Agendamento

# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder

# Instalar Maven
RUN apk add --no-cache maven

# Definir diretório de trabalho
WORKDIR /app

# Copiar pom.xml primeiro (para cache de dependências)
COPY pom.xml .

# Baixar dependências (será cacheado se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Fazer build da aplicação
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Instalar curl para health check
RUN apk add --no-cache curl

# Criar usuário não-root para segurança
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR do stage de build
COPY --from=builder /app/target/agendamento-1.0.0.jar app.jar

# Mudar para usuário não-root
USER spring:spring

# Expor porta
EXPOSE 8080

# Definir variáveis de ambiente padrão
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
