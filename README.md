# Ocado Basket Splitter

This project contains a Java class `BasketSplitter` that is used to split a list of items into different delivery types based on a configuration file. The class uses a greedy algorithm to perform an approximate solution for the set cover problem.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher

### Building

To build the project, navigate to the project directory and run the following command:

```bash
gradle build
```

This will compile the Java code, run the tests, and create a JAR file in the `build/libs` directory.

### Creating a Shadow JAR

To create a shadow JAR, which includes all of the project's dependencies, use the following command:

```bash
./gradlew shadowJar
```

This will create a JAR file in the `build/libs` directory that can be used to run the application on any machine with a compatible JVM.

## Usage

The `BasketSplitter` class is used by creating a new instance with the path to a configuration file. The `split` method can then be called with a list of items to split them into different delivery types.

```java
BasketSplitter basketSplitter = new BasketSplitter("path/to/config.json");
List<String> items = Arrays.asList("item1", "item2", "item3");
Map<String, List<String>> result = basketSplitter.split(items);
```

The `split` method returns a map where the keys are the delivery types and the values are lists of items for each delivery type.
