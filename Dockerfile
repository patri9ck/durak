FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

WORKDIR /app

COPY . /app

RUN apt-get update && apt-get install -y libgtk-3-0 libglib2.0-0 libxext6 libxrender1 libxtst6 libx11-6 xvfb

ENV DISPLAY=host.docker.internal:0.0

RUN sbt update && sbt compile

CMD ["sbt", "run"]