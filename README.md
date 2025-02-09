# Multicast Chat

This repository contains a simple chat application implemented in Java. The application allows users to join chat rooms, send messages, and receive messages asynchronously using multicast sockets.

This was built as an assignment for the **Laboratory of Mobile and Distributed Applications Development** course at the PUC Minas university.

## Features

- Join and leave chat rooms
- Send and receive messages
- Asynchronous packet receiving
- Exception handling for connection states

## Requirements

- Java 23
- Maven

## Installation

1. Clone the repository.
2. Build the project using Maven:
```sh
mvn clean install
```

## Usage

1. Run the application:
```sh
mvn exec:java -Dexec.mainClass="br.ldamd.Main"
```

2. Follow the prompts to enter your username and the room number.

## Running Tests

To run the unit tests, use the following Maven command:
```sh
mvn test
```