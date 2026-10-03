# Vivapp 🇨🇭

> **Field Kitchen Provision & Inventory Ledger for Kotlin Multiplatform (Android & iOS)**

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blueviolet?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-blue?logo=jetpackcompose)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green.svg)](https://github.com/your-org/vivapp)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

Vivapp is an open-source, offline-first mobile application designed to simplify field kitchen provision management and inventory logging for military logistics, emergency response units, and large-scale catering operations.

By replacing traditional paper logs and manual tally sheets with rapid camera barcode scanning and structured digital auditing, Vivapp cuts field inventory logging time by up to 80% while eliminating critical data entry errors.

---

## 🚀 Key Features

* **⚡ Rapid Barcode & QR Code Scanning:** Scan product barcodes, catalog numbers, and food provision packages instantly using the device camera.
* **📦 Session-Based Inventory Auditing:** Group food items by ration type, weight, and unit counts within dedicated active logging sessions.
* **📡 100% Offline-Capable Ledger:** Perform full field inventory counts without requiring an active cellular or internet connection.
* **📊 Direct CSV Export:** Generate standardized CSV summaries instantly and dispatch them via email, local storage, or messaging to central supply chain processing units.
* **🎨 Cross-Platform UI:** Built with Compose Multiplatform for a consistent, native UI experience on both Android and iOS.

---

## 🏗 Architecture & Tech Stack

Vivapp is built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**, allowing shared business logic and UI across platforms while executing natively on each OS.

* **Shared Core (`:composeApp`):** Shared Kotlin code containing ViewModels, domain models, database/storage logic, and Compose Multiplatform UI components.
* **iOS Target (`iosApp`):** Xcode wrapper project hosting the SwiftUI entry point and compiling the shared Kotlin framework via Gradle.
* **Android Target (`androidApp`):** Native Android compilation target integrated into the root project build.

---

## 📂 Project Structure

```text
Vivapp/
├── composeApp/                 # Shared KMP Module (Kotlin & Compose UI)
│   ├── src/
│   │   ├── commonMain/         # Platform-independent logic, screens, and UI
│   │   ├── androidMain/        # Android-specific implementations
│   │   └── iosMain/            # iOS-specific bindings and native bridges
│   └── build.gradle.kts        # Shared KMP build and dependency declarations
├── iosApp/                     # Native Xcode Project / Workspace
│   ├── iosApp.xcworkspace
│   ├── iosApp.xcodeproj
│   └── iosApp/                 # SwiftUI entry point, Assets, Info.plist
├── build.gradle.kts            # Root Gradle build script
└── settings.gradle.kts         # Subproject & plugin settings
```

---

## 🛠️ Prerequisites

Ensure your development environment meets the following requirements:

| Component | Minimum Requirement |
| :--- | :--- |
| **JDK** | OpenJDK 17 or 21 |
| **Android Studio** | Ladybug / Koala (or newer) with Kotlin Multiplatform Plugin |
| **Xcode** | Xcode 15.0+ (Required for building and running the iOS target on macOS) |
| **macOS** | Required for iOS development and simulator/device execution |

---

## 💻 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/vivapp.git
cd vivapp
```

### 2. Run the Android App
Open the project in **Android Studio**, select the `composeApp` / `androidApp` run configuration, and press **Run**.

Alternatively, run via terminal:
```bash
./gradlew :composeApp:assembleDebug
```

### 3. Run the iOS App

1. Open the Xcode workspace:
   ```bash
   open iosApp/iosApp.xcworkspace
   ```
2. Select your target device or simulator in Xcode.
3. Press **`Cmd + R`** to build and launch.

> **Note:** Xcode automatically executes `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode` during its build phase to recompile shared Kotlin changes into the native iOS framework bundle.

---

## 📄 Standard CSV Export Specification

Exported session summaries follow standard RFC 4180 CSV formatting for direct ingestion into central logistics database systems:

```csv
session_id,timestamp,item_id,barcode,item_name,ration_type,quantity,unit
SES-20261003-01,2026-10-03T20:30:00Z,SKU-8821,7610000123456,Field Ration Type A,Ration,50,BOX
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to open issues or submit pull requests.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📜 License

Distributed under the **Apache License 2.0**. See `LICENSE` for more information.
