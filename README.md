# Smart Pantry Manager

A Java Android application for tracking pantry ingredients and suggesting recipes only when all required ingredients and quantities are available.

**Repository:** https://github.com/andieKU22/Smart-Pantry-Manager

## Technology
- Java and Android Studio
- SQLite via `SQLiteOpenHelper`
- Custom ListView adapters
- Activities and explicit Intents
- Android XML manifest and theme resources

## Database choice
SQLite keeps pantry data locally on the device, persists it between sessions, and does not require network access or a separate server. The database is designed to store pantry items, recipes, recipe ingredients, and settings. The recipe catalogue is seeded when the database is first created.

## Screens
1. **Pantry list:** view pantry items; select an item to edit; long-press to delete.
2. **Add/Edit Ingredient:** validate ingredient details, quantity, unit, and optional date.
3. **Suggested Recipes:** show recipes that pass the strict ingredient and quantity check.
4. **Recipe Detail:** display required ingredients and preparation steps.
5. **Settings:** store a user preference.

## Run in Android Studio
1. Install Android Studio and a compatible Android SDK.
2. Open the `SmartPantryManager` project folder containing `settings.gradle`.
3. Allow Gradle sync to finish; an internet connection may be required to download Gradle dependencies.
4. Create an Android Virtual Device (AVD) or connect an Android phone with USB debugging enabled.
5. Select the app configuration and click **Run**.

If Android Studio reports that the Gradle wrapper is missing, use Android Studio's Gradle tooling or install a compatible Gradle distribution and generate the wrapper. Do not commit generated build caches.

## Strict matching
A recipe should be suggested only when every required ingredient is present in sufficient quantity. Partial matches must not appear in the main suggestions list. Verify ingredient-name normalization and unit conversion against the implementation before demonstrating these cases.

## Manual verification checklist
- [ ] Add a pantry item with a valid name, quantity, and unit.
- [ ] Test empty names, invalid quantities, and zero quantity.
- [ ] Edit an existing item and confirm the change is saved.
- [ ] Delete an item and confirm it disappears.
- [ ] Close and reopen the app; confirm pantry data persists.
- [ ] Add sufficient ingredients for a seeded recipe; confirm it appears in Suggested Recipes.
- [ ] Remove or reduce one required ingredient; confirm the recipe no longer appears.
- [ ] Open a recipe detail screen and inspect ingredients and preparation steps.
- [ ] Change the settings preference, leave the screen, and return to check persistence.

## Important notes
- Confirm the app builds and runs in Android Studio before submission.
- Check the assignment requirements and ensure the final repository includes the full Android Studio source project, not only documentation.
- Commit meaningful changes as they are actually made. Do not fabricate commit history.
