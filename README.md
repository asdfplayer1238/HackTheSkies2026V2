# HackTheSkies2026V2

A small Java game built with Gradle and LWJGL. Use **W**, **A**, **S**, and **D**
to move the player around the window. Movement speed is consistent across frame
rates, and the player stays inside the window when it is resized.

## Run

Requirements: a JDK and Gradle.

```sh
gradle run
```

Gradle selects the matching LWJGL native libraries for Windows, macOS (including
Apple Silicon), or Linux.
