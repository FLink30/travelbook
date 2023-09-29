# Travelbook
Travelbook is an Android app developed during the Mobile Applications Development course in summer semester 2023 in university. The app allows users to create their own travel diaries by uploading photos or taking photos in the app and adding some good memories. Travelbook is designed by Matrial 3 and offers light and dark mode options. 

## Features 
Travelbook has the following features: 

- create a new travelbook and save it in the app
- upload photos (if the user allows the app to access photos on the device)
- take pictures inside the app (if the user allows it)
- add a lovely memory to each photo
- by clicking on the book for a long time the user can delete the travelbook or edit the name of it
- choose light or dark mode

## How to Use 
Travelbook isn't available in the Play Store for now. Until now you have to download the source and compile it on your own. 

## Technology Used 
Travelbook was built using the following technologies:

- Kotlin
- Material 3
- SQLite (Room persistence library)
- Kotlin Corountine (useful library for Asynchronous Programming)

## TODOs
Travelbook isn't production ready, it still has some bugs, that need to be fixed, features that should be implemented and the UX/UI should be optimized for a better usability: 

**Bugs:**
- add UI tests
- the new added fotos are just displayed when going out of the travelbook and enter it again

**Features:**
- the travelbook should be exportable, so the user can show it to others
- the location should be possible to add

**UX/UI:**
- the travelbooks should start in the right corner after the plus-button
- optimize the delete-dialog regarding the UX:
  - animate the travelbook by clicking on it (Feedback)
  - optimize the position of the buttons
- beautify the screen where the user can choose between capture image and upload image
- beautify the screen inside the travelbook

## Contributors: 
This project was developed by a group of two students.

## License
This project is licensed under the MIT License. See the [LICENSE](https://github.com/FLink30/travelbook/blob/main/LICENSE) file for details. 
