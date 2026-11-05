# Smart Pantry Manager

A Java-based Android application that helps users manage pantry ingredients and find recipes based on the ingredients they have available.

## Features

- View and manage pantry ingredients.
- Add, edit and delete ingredients.
- Validate ingredient names and quantities.
- View recipe suggestions based on available ingredients.
- Check the ingredients and preparation instructions for each recipe.
- Save application preferences locally.

## Built With

- Java
- Android Studio
- SQLite
- Android XML layouts
- Android Activities and Intents

## Project Structure

The application source code, layouts and resources are located in the `app/src/main` directory.

- `java/` – application classes and database functionality
- `res/layout/` – application screen layouts
- `res/values/` – strings, colours and themes
- `assets/` – bundled SQLite database

## Getting Started

### Requirements

- Android Studio
- Android SDK
- JDK compatible with the project's Android Gradle Plugin

### Running the App

1. Clone or download this repository.
2. Open the project folder in Android Studio.
3. Wait for Gradle sync to complete.
4. Start an Android emulator or connect an Android device with USB debugging enabled.
5. Select the app configuration and click **Run**.

To build the debug version on Windows, run:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Database

The application uses SQLite for local data storage. Pantry information, recipes, recipe ingredients and settings are stored on the device.

## Testing

The following areas can be tested:

- Adding, editing and deleting pantry ingredients.
- Validation of ingredient names and quantities.
- Saving pantry data between application sessions.
- Displaying recipes when all required ingredients are available in sufficient quantities.
- Hiding recipes when one or more required ingredients are unavailable.
- Viewing recipe details.
- Saving settings preferences.
