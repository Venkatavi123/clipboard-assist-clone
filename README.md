# Clipboard Assist — full feature project

This project adds a local pilgrim-profile manager, field-by-field copy, a user-triggered floating button using Android Accessibility permission, an Android Autofill service, and a quick-insert keyboard (IME).

## Features
- Save and edit pilgrim profiles and general contact/address details locally.
- Reorder profiles to match ticket order; delete unwanted profiles.
- Copy a field, a profile, or all saved details.
- Optional floating `+` button. In another app, tap it and choose a saved value. It attempts to put the selected value into the currently focused editable field; if the app does not allow this, it copies the value for manual paste.
- Android Autofill service offers saved profile datasets when a form exposes supported fields.
- Quick Insert keyboard offers saved values for manual insertion into the focused field.

## Important limitations
- Third-party booking apps and websites may block autofill or accessibility text insertion. The user remains in control and must review the form and submit it themselves.
- Android requires the user to enable the Autofill service, keyboard, and Accessibility service in Settings. Overlay permission is separately required for the floating button.
- This source has not been built on a local Android SDK in this workspace. GitHub Actions will compile it and expose any compiler errors.

## Build
Push to `main` or use Actions → Build Android APK → Run workflow. Download the `clipboard-assist-debug-apk` artifact.
