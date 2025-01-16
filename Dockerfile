FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Set the working directory
WORKDIR /app

# Copy the project into the container
COPY . /app

# Install necessary libraries
RUN apt-get update && apt-get install -y libgtk-3-0 libglib2.0-0 libxext6 libxrender1 libxtst6 libx11-6 xvfb

# Set the environment variable for the display
ENV DISPLAY=host.docker.internal:0.0

# Build the project
RUN sbt update && sbt compile

# Start the application
CMD ["sbt", "run"]