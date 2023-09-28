# Travelbook
Travelbook is an Android app developed during the Mobile Applications Development course in university. The app allows users to create their own travel diaries by uploading photos or taking photos in the app and adding some good memories. Travelbook is designed by Matrial 3 and offers light and dark mode options. 

## Features 
Travelbook has the following features: 

- create a new travelbook and save it in the app
- upload photos (if the user allows the app to access photos on the device)
- take pictures inside the app (if the user allows the app to take pictures)
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
- Kotlin Corountine (Usefull library for Asynchronous Programming)

## TODOs:
Travelbook isn't production ready, it still has some bugs, that need to be fixed, features that should be implemented and the UX/UI should be optimized for a better usability: 

**Bugs:**
- add UI tests
- the new added fotos are just displayed when going out of the travelbook and enter it again

**Features:**
- the travelbook should be exportable, so the user can show it to others
- the location should be possible to add

**UX/UI:**
- the travelbooks should start in the right corner after the plus-button
- optimize the delete-dialog regarding the UX (when clicking on the travelbook for a long time):
  - animated travelbook by clicking on it (Feedback)
  - optimize the position of the buttons
- beautify the screen where the user can choose between capture image and upload image
- beautify the screen inside the travelbook

