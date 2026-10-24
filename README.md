# GOVINDA Clipboard Assist — profile manager v2

This Android project is a practical, offline profile manager inspired by the requested Clipboard Assist workflow. It is **not an exact copy** of the original APK.

## Included
- Multiple pilgrim profiles, with name, age, gender, ID type/number, mobile, email, address, city, state, country and PIN.
- Profiles persist on the phone after closing the app.
- Edit/delete profiles and move them up/down to match ticket order.
- Copy any individual field, one complete profile, or all profiles.
- No internet permission and no connection to TTD or any booking website. Copy and paste details manually; the app does not submit bookings or bypass site restrictions.

## Build using the existing GitHub Actions workflow
1. Download and extract this ZIP on your computer.
2. Open your `clipboard-assist-clone` GitHub repository.
3. Upload/replace the **contents** of this folder at the repository root (do not upload the ZIP itself).
4. Commit the changes to `main`.
5. Open **Actions** → **Build Android APK** and open the latest run.
6. Download the `clipboard-assist-debug-apk` artifact and extract `app-debug.apk`.
7. Transfer the APK to your phone and install it.

## Privacy note
The profile data is stored in the app's private local preferences on the device. This prototype has no PIN/biometric lock or encrypted export; avoid entering highly sensitive identity information until you have reviewed the app's security needs.
