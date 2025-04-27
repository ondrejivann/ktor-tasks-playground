# ---- Build Stage ----
# Použij oficiální Gradle image obsahující JDK (vyber verzi JDK, kterou používáš, např. 17)
FROM gradle:8.5.0-jdk17 AS builder

# Nastav pracovní adresář uvnitř kontejneru
WORKDIR /app

# Zkopíruj build skripty a wrapper pro cachování závislostí
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# Stáhni závislosti (tento krok se cachuje, pokud se build skripty nezmění)
# Můžeš spustit jenom 'dependencies' task, pokud chceš jen stáhnout závislosti
RUN ./gradlew --no-daemon build --info || true

# Zkopíruj zbytek zdrojového kódu
COPY src ./src

# Sestav aplikaci a vytvoř fat JAR (nebo použij installDist)
# -x test přeskočí testy při buildování image
RUN ./gradlew --no-daemon shadowJar -x test
# Alternativa s installDist:
# RUN ./gradlew --no-daemon installDist -x test

# ---- Runtime Stage ----
# Použij štíhlý Java Runtime image (zde Eclipse Temurin JRE na Alpine Linuxu)
FROM eclipse-temurin:17-jre-alpine

# Nastav pracovní adresář
WORKDIR /app

# Zkopíruj pouze sestavený JAR z build stage
COPY --from=builder /app/build/libs/ktor-exposed-task-app-all.jar ./app.jar
# Alternativa s installDist (zkopíruje celý adresář s binárkami a libs):
# COPY --from=builder /app/build/install/ktor-exposed-task-app ./

# Port, na kterém bude aplikace naslouchat uvnitř kontejneru.
# Ktor aplikace by měla číst proměnnou prostředí PORT od Renderu.
# EXPOSE zde jen dokumentuje, který port je určený k vystavení. Render se o mapování postará.
# Hodnota 10000 je častý default u Renderu, ale aplikace by měla číst env var PORT.
EXPOSE 10000

# Příkaz pro spuštění aplikace
# Použije se proměnná PORT poskytnutá Renderem. Ktor musí být nakonfigurován, aby ji četl.
# Použij "0.0.0.0" jako host, aby server naslouchal na všech rozhraních uvnitř kontejneru.
# Použij exec formu CMD pro správné zpracování signálů
CMD ["java", "-jar", "./app.jar"]
# Alternativa s installDist:
# CMD ["./bin/ktor-exposed-task-app"]
