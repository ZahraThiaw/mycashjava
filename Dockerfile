# Utilise l'image OpenJDK
FROM openjdk:17-jdk

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le code source dans le répertoire de travail du conteneur
COPY . .

# Rendre le fichier mvnw exécutable
RUN chmod +x ./mvnw

# Compiler l'application sans exécuter les tests
RUN ./mvnw clean package -DskipTests

# Exposer le port de l'application (remplacez si nécessaire)
EXPOSE 3000

# Copier le fichier JAR compilé vers le répertoire de travail du conteneur
COPY target/*.jar app.jar

# Commande pour démarrer l'application
CMD ["java", "-jar", "app.jar"]
