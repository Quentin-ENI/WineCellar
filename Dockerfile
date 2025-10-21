# Étape 1 : Build du projet avec Gradle
FROM gradle:9.1.0-jdk21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration Gradle
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Télécharger les dépendances Gradle (cache pour accélérer les builds futurs)
RUN gradle dependencies --no-daemon || true

# Copier le reste du projet
COPY src ./src

# Build du jar exécutable
RUN gradle bootJar --no-daemon

# Étape 2 : Création de l'image finale légère
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copier le jar depuis l'étape de build
COPY --from=build /app/build/libs/*.jar app.jar

# Exposer le port par défaut de Spring Boot
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java","-jar","app.jar"]
