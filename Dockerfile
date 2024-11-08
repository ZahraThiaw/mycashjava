# Utilise l'image OpenJDK
FROM openjdk:17-jdk-slim

# Répertoire de travail dans le conteneur
WORKDIR /app

# Copie tous les fichiers dans le répertoire de travail du conteneur
COPY . .

# Rendre le fichier mvnw exécutable
RUN chmod +x ./mvnw

# Compiler l'application sans exécuter les tests
RUN ./mvnw clean package -DskipTests

# Exposer le port de votre application Spring Boot (remplacez si nécessaire)
EXPOSE 3000

# Commande pour démarrer l'application
CMD ["java", "-jar", "target/flutter-0.0.1-SNAPSHOT.jar"]
