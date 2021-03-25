A tool to eliminate duplicate classes from a set of JAR files.

Every duplicate class is only kept in the first JAR encountered and
removed from any other JAR. The tool shows warnings when removing
duplicate classes with different sizes.

Usage:

```
./gradlew run --args="path/to/file1.jar path/to/file2.jar ..."
```
