# PassNote - A Simple & Secure Android Password Manager

PassNote is an open-source, offline-first password management application for Android, developed with the assistance of Gemini and built with modern Android development technologies. It allows you to securely store and manage your passwords locally on your device, giving you full control over your data.

## ✨ Features

- **Secure Local Storage**: All your data is stored locally in a Room database. Nothing is ever sent to the cloud.
- **Full CRUD Operations**: Easily add, view, edit, and delete your password entries through a clean and intuitive UI.
- **Powerful Search**: Quickly find the password you need with a fuzzy search for services/companies on the main screen.
- **Smart Suggestions**:
  - **Username Suggestions**: Automatically suggests frequently used usernames from a dropdown menu when adding a new entry.
  - **Customizable Password Generator**: Generate strong, random passwords with configurable length and a customizable set of special characters.
- **Data Management**:
  - **CSV Import/Export**: Easily back up and restore your data. The app supports exporting to a `passnote.csv` file and importing from CSVs (with optional `Notes` field).
  - **Bulk Delete**: Clear all data from the database with a single tap (with confirmation).
- **Personalized Settings**:
  - **Dynamic Threshold**: Customize the threshold for when username suggestions appear.
  - **Password Rules**: Tailor the random password generator to your needs (length, custom characters, enable/disable special characters).
  - **Persistent Settings**: All your preferences are saved locally using Jetpack DataStore.
- **Modern UI**: Built entirely with Jetpack Compose and Material 3, featuring a clean, modern design with adaptive theming (Light/Dark mode) that follows your system's settings.

## 🛠️ Tech Stack & Architecture

- **UI**: 100% Jetpack Compose & Material 3.
- **Architecture**: MVVM (Model-View-ViewModel).
- **Data Persistence**:
  - Room for structured data (passwords).
  - Jetpack DataStore for user settings.
- **Asynchronicity**: Kotlin Coroutines & Flow for all background tasks and reactive data streams.
- **Navigation**: Jetpack Navigation for Compose to manage screen transitions.
- **Dependency Injection**: Manual DI via a custom `Application` class for simplicity.

## 🚀 Getting Started

1.  Clone the repository:
    ```bash
    git clone https://github.com/your-username/PassNote.git
    ```
2.  Open the project in the latest stable version of Android Studio.
3.  Let Gradle sync the dependencies.
4.  Build and run on an emulator or a physical device (API 29+).

## 🤝 Contributing

Contributions are welcome! If you have ideas for improvements or find a bug, please feel free to open an issue or submit a pull request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
