# Smart Pantry Manager

A Java-based Android application for managing pantry ingredients and suggesting recipes when all required ingredients and quantities are available.

**Repository:** https://github.com/andieKU22/Smart-Pantry-Manager

## Technology

- Java and Android Studio
- SQLite using `SQLiteOpenHelper`
- Android Activities and explicit Intents
- Custom ListView adapters
- XML layouts, resources, and Android manifest

## Project structure

```text
Smart-Pantry-Manager/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── assets/
│       │   ├── java/com/example/smartpantrymanager/
│       │   └── res/
│       ├── test/
│       └── androidTest/
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle.kts
```

## Main features

1. **Pantry management:** view, add, edit, and delete pantry ingredients.
2. **Ingredient validation:** validate ingredient details and quantities.
3. **Recipe suggestions:** suggest recipes only when all required ingredients are available in sufficient quantities.
4. **Recipe details:** view recipe ingredients and preparation instructions.
5. **Settings:** save a user preference locally.

## Database

The application uses SQLite to store pantry items, recipes, recipe ingredients, and settings on the device. The database does not require a separate server or network connection. The bundled database asset is located at `app/src/main/assets/smart_pantry.db`.

## Requirements

- Android Studio
- A compatible Android SDK
- JDK 11 or a JDK supported by the installed Android Gradle Plugin
- Internet connection for the initial Gradle dependency download

## Run the application

1. Clone or download this repository.
2. In Android Studio, select **Open** and choose the repository's root folder (the folder containing `settings.gradle.kts`).
3. Allow Gradle sync to complete.
4. Create an Android Virtual Device (AVD), or connect an Android phone with USB debugging enabled.
5. Select the **app** run configuration and click **Run**.

The Gradle wrapper is included. On Windows, you can also run:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Manual testing checklist

- [ ] Add a pantry item with a valid name, quantity, and unit.
- [ ] Test empty names and invalid or zero quantities.
- [ ] Edit an existing item and confirm the change is saved.
- [ ] Delete an item and confirm it disappears.
- [ ] Restart the app and confirm pantry data persists.
- [ ] Add sufficient ingredients for a seeded recipe and confirm it appears in Suggested Recipes.
- [ ] Remove or reduce one required ingredient and confirm the recipe is no longer suggested.
- [ ] Open a recipe detail screen and inspect its ingredients and preparation steps.
- [ ] Change the settings preference and confirm it persists.

## Repository hygiene

Generated build outputs, IDE files, and machine-specific `local.properties` files are excluded through `.gitignore`. Do not commit local SDK paths or Gradle build caches.

## Before submission

Build and run the app in Android Studio, complete the manual testing checklist, and confirm that the submitted repository matches the version tested locally.
