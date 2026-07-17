# Remedy 💊

Remedy is a reliable, lightweight Android application designed to help users manage their daily medication schedules effortlessly. Built using modern Android development practices, the app ensures you never miss a dose by providing customizable alerts, robust background scheduling, and intuitive persistent settings.


## Features ✨

* **Customizable Reminders:** Schedule alarms for different medications with precise timing configurations.
* **Audio & Haptic Alerts:** Tailor notifications with personalized alarm sounds and vibration toggles.
* **Persistent Preferences:** Uses Jetpack DataStore for fast, reliable, and asynchronous storage of user configurations.
* **Clean Architecture:** Built following clean development principles using modern UI components and robust background coroutine management.

## Tech Stack 🛠️

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose
* **Asynchronous Flow:** Kotlin Coroutines & Jetpack Flows
* **Local Storage:** Jetpack Preferences DataStore
* **Dependency Injection:** Dagger Hilt

## Architecture Overview 🏗️

The application follows the recommended modern Android architecture guidelines, leveraging unidirectional data flow (UDF):

* **Presentation Layer:** Jetpack Compose UI communicating with State-driven ViewModels.
* **Domain/Data Layer:** Unified managers (like `SettingsManager`) responsible for handling background states, data streams, and safe preference emission.

## Installation & Setup 🚀

To get a local copy up and running, follow these steps:

1. **Clone the repository:**
```bash
git clone https://github.com/YOUR_USERNAME/remedy.git

```


2. **Open in Android Studio:**
* Launch Android Studio.
* Select **File > Open** and navigate to the cloned directory.


3. **Sync and Run:**
* Allow Gradle to sync dependencies automatically.
* Select your target emulator or physical device.
* Click the **Run** button (green play icon).


**This is AI gen README.md.**

## License 📄

This project is licensed under the MIT License - see the [LICENSE](https://www.google.com/search?q=LICENSE) file for details.
