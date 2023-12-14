# Use an official OpenJDK image as a parent image
FROM openjdk:21-jdk

# Set the working directory to /app
WORKDIR /app

# Copy the Java source code into the container at /app
COPY . /app

# Compile the Java code
RUN javac LoadGenerator.java

# Run the application when the container lauches
CMD ["java", "LoadGenerator"]