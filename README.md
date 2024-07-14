# Magic BT - Bluetooth Accessory Control App

This is an Android application for controlling accessories via Bluetooth using an ESP32 microcontroller. The app allows you to add, delete, and toggle accessories, and it displays the status of the connected device.

## Features

- Connect to a Bluetooth device
- Add, delete, and toggle accessories
- Display the status of the connected device
- Show toast messages for various actions
- Custom popup menu for additional options

## Technologies Used

- Kotlin
- Android Jetpack Libraries
- Dagger-Hilt for dependency injection
- Gson for JSON serialization/deserialization
- Material Components for UI

## Project Structure

- `MainActivity`: The main activity that handles UI interactions and Bluetooth connections.
- `MainViewModel`: ViewModel that manages UI-related data and handles business logic.
- `Repository`: Handles data operations and provides a clean API for data access.
- `Accessory`: Data model representing an accessory.
- `CommandModel`: Data model for sending commands.
- `BTUtil`: Utility class for handling Bluetooth operations.
- `AccListAdapter`: RecyclerView adapter for displaying a list of accessories.

## Getting Started

### Prerequisites

- Android Studio Bumblebee or later
- An Android device or emulator running Android 5.0 (Lollipop) or higher
- ESP32 microcontroller with Bluetooth capabilities

### Installation

1. Clone the repository:

    ```sh
    git clone https://github.com/MagicBulletPro/BluetoothAccessoryControl-Android.git
    ```

2. Open the project in Android Studio.

3. Sync the project with Gradle files.

4. Build and run the app on your device or emulator.

### Usage

1. On launching the app, it will attempt to connect to a Bluetooth device. Ensure your ESP32 is powered on and discoverable.

2. Use the menu button to add accessories, search for Bluetooth devices, or exit the app.

3. Add a new accessory by providing a name and GPIO pin number. You can toggle the accessory's state (on/off) by tapping on it in the list.

4. The app will display the current status of the connected device and show toast messages for various actions.

## Code Explanation

### MainActivity

The `MainActivity` class is the entry point of the app. It sets up the UI, connects to the Bluetooth device, and observes ViewModel data to update the UI.

- `setupActivity()`: Sets up the popup menu, item list adapter, and Bluetooth connection. Observes ViewModel data to update the UI accordingly.
- `setupItemListAdapter()`: Sets up the RecyclerView adapter for the accessory list.
- `setupPopup()`: Sets up the popup menu with options to add accessories, search for Bluetooth devices, and exit the app.
- `searchBluetooth()`: Opens a dialog to search for Bluetooth devices.
- `showDialog()`: Displays a custom dialog with a title, message, and buttons.
- `addAccessory()`: Displays a dialog to add a new accessory.

### MainViewModel

The `MainViewModel` class manages UI-related data and handles business logic. It communicates with the repository to perform data operations and update the UI.

- `getBTData()`: Collects Bluetooth data from the repository and updates the UI based on the data's status.
- `syncController()`: Sends a sync command to the ESP32 to synchronize the accessory states.
- `addAccessory()`, `getAllAccessory()`, `updateAccessory()`, `deleteAccessory()`, `sendCommand()`: Functions to perform various operations on accessories.


## Screenshots

### App Menu

<img src="/screenshots/scr1.png" alt="App menu screenshot" width="220"/>

### Select a Bluetooth Device

<img src="/screenshots/scr2.png" alt="Select a Bluetooth Device screenshot" width="220"/>

### Add new Accessory

<img src="/screenshots/scr3.png" alt="Add new Accessory" width="220"/>

### Accessory List

<img src="/screenshots/scr4.png" alt="Accessory List" width="220"/>

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

- [Android Jetpack](https://developer.android.com/jetpack)
- [Dagger-Hilt](https://dagger.dev/hilt/)
- [Gson](https://github.com/google/gson)
- [ESP32](https://www.espressif.com/en/products/socs/esp32)

## Contact

For any inquiries, please contact [magicbulletsoft@gmail.com](mailto:magicbulletsoft@gmail.com).
