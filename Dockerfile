FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Setze das Arbeitsverzeichnis
WORKDIR /app

# Kopiere das Projekt in den Container
COPY . /app

# Installiere Bibliotheken
RUN apt-get update && apt-get install -y libgtk-3-0 libglib2.0-0 libxext6 libxrender1 libxtst6 libx11-6 xvfb

# Setze die Umgebungsvariable für das Display
ENV DISPLAY=host.docker.internal:0.0

# Baue das Projekt
RUN sbt update && sbt compile

# Starte die Anwendung
CMD ["sbt", "run"]

