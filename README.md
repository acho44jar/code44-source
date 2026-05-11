# code44 (Minecraft Mod)

This repository contains the full source code for the `code44` mod.

This source is intended for project maintenance and, when needed, private moderation review.

## Environment

- Java: 21
- Gradle Wrapper: included (`gradlew`, `gradlew.bat`)
- Minecraft target: configured in `build.gradle`

## Build (Windows)

```powershell
.\gradlew.bat clean build
```

## Build (Linux/macOS)

```bash
./gradlew clean build
```

## Build Output

After successful build, the mod jar is generated in:

`build/libs/`

Current artifact name:

- `code44-0.1.1-realese.jar`

## Project Structure

- Java sources: `src/main/java`
- Resources/assets/data: `src/main/resources`
- Gradle config: `build.gradle`, `settings.gradle`, `gradle.properties`

## Notes for Review

- This repository is the source used to build the submitted jar.
- If moderation needs any specific commit/tag, it can be provided on request.
