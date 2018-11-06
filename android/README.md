# Read me! 🤖

This folder contains resources pertaining to the development of the Android application, including the Android Studio project and design elements such as icons or mock-ups.

## External libraries

### Android Jetpack

 [Jetpack](https://developer.android.com/jetpack/) is a collection of Android software components to make it easier for you to develop great Android apps. We will use some of these components.

#### Data Binding Library

The [Data Binding Library](https://developer.android.com/topic/libraries/data-binding/) is a support library that allows you to bind UI components in your layouts to data sources in your app using a declarative format rather than programmatically.

#### LiveData

[LiveData](https://developer.android.com/topic/libraries/architecture/livedata) is an observable data holder class. Unlike a regular observable, LiveData is lifecycle-aware, meaning it respects the lifecycle of other app components, such as activities, fragments, or services. This awareness ensures LiveData only updates app component observers that are in an active lifecycle state. LiveData classes are a significant part of the MVVM pattern.

#### Room Persistence Library

The [Room persistence library](https://developer.android.com/topic/libraries/architecture/room) provides an abstraction layer over SQLite to allow for more robust database access while harnessing the full power of SQLite.

#### ViewModel

The [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) class is designed to store and manage UI-related data in a lifecycle conscious way. The ViewModel class allows data to survive configuration changes such as screen rotations.