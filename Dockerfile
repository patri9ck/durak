FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

WORKDIR /durak

COPY build.sbt /durak/
COPY project /durak/project/

RUN sbt update

COPY . /durak

# Verwende BuildKit für apt-get-Caching
RUN --mount=type=cache,target=/var/cache/apt \
    --mount=type=cache,target=/var/lib/apt \
    apt-get update && apt-get install -y --no-install-recommends \
        libgtk-3-0 libglib2.0-0 libxext6 libxrender1 libxtst6 libx11-6 xvfb && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

ENV DISPLAY=host.docker.internal:0.0

RUN sbt compile

CMD ["sbt", "run"]
