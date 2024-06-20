# AreUnemia Android Mobile App

Welcome to the AreUnemia Mobile application repository. This repository contains the source code and resources utilized to develop the AreUnemia application, aimed at early detection of anemia using smartphone images of the eye conjunctiva.

## Table of Contents

1. [Introduction](#introduction)
2. [Features](#key-features)
3. [Download APK from Link](#download-apk-from-link)
4. [Tech Used](#tech-used)
5. [Steps to Replicate](#steps-to-replicate)
6. [Dependencies](#dependencies)

## Introduction

The AreUnemia Mobile application leverages advanced cloud-based image processing and cloud-based machine learning techniques to analyze images of the eye conjunctiva. This innovative approach aims to provide a preliminary diagnosis of anemia, helping users identify potential health issues early and seek professional medical advice promptly.

## Features

- **Scan**
- **Medication Reminder**
- **Information**
- **History**
- **Authentication**

## Download APK from Link

You can download the latest version of the AreUnemia Mobile application APK from the following link:
[Download APK](https://www.dropbox.com/scl/fi/ah2jeyvdly7ddjoss7byp/AreUnemia-v02.apk?rlkey=alc0wcarc48xl6skg2oid2hao&st=3y2bpwgt&dl=1)

## Tech Used

- **Programming Language**: Kotlin
- **Prediction Models**: Cloud-based models served via API
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit
- **UI Design**: XML

## Steps to Replicate

To replicate and run the AreUnemia application on your local machine, follow these steps:

1. Clone the repository:
```
git clone https://github.com/AreUnemia-Project/AreUnemia-Mobile.git
```
2. Open the project in Android Studio:
   - Open Android Studio.
   - Click on `File` -> `Open`.
   - Navigate to the cloned repository and open it.
3. Open Android Studio:
   - Click on File -> Open.
   - Navigate to the cloned repository and open it.
4. Build the project:
   - Sync the project with Gradle files.
   - Build the project to ensure all dependencies are correctly resolved
5. Run the application:
   - Connect your Android device or start an emulator.
   - Click on the Run button in Android Studio.

## Dependencies

- Retrofit: For network requests.
- Gson: For JSON parsing.
- Glide: For image loading and caching.
- Jetpack Components: Including LiveData and ViewModel.
- StateProgressBar: For showing progress steps.
- uCrop: For image cropping.
- MonthYearPicker: For picking dates.

For more details, please refer to the documentation provided in the repository or contact us.

