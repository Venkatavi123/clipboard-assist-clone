# Clipboard Assist Clone — starter project

This is a starter Android project with a GitHub Actions workflow to build a debug APK in the cloud.

## Build with GitHub (no Android Studio)
1. Create a new GitHub repository named `clipboard-assist-clone`.
2. Upload all files and folders from this project to the repository root (not the ZIP itself).
3. Open the repository's **Actions** tab and select **Build Android APK**.
4. Click **Run workflow** (or push to `main` to trigger a build).
5. Open the completed workflow run, scroll to **Artifacts**, and download `clipboard-assist-debug-apk`.
6. Extract the downloaded artifact ZIP; it contains `app-debug.apk`.
7. Copy `app-debug.apk` to your Android phone and install it. You may need to allow installs from your file manager.

## Important
This is only a starter/template. It includes editable fields and a copy-to-clipboard button, but it does **not** yet reproduce every screen or behavior of the original APK/videos. It does not connect to TTD or any booking service, and it does not persist saved profiles yet. Do not enter real identity details into a prototype until you are comfortable with its privacy/security.
