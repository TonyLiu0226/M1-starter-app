# M1

## New Feature 1

**Name:** Play music

**Short description:** [WRITE_A_SHORT_DESCRIPTION_OF_THE_CODE]

**Location and code:** 
Primarily in the frontend. The following files have been created to allow the music player to be added to the home screen:
- frontend/app/src/main/java/com/cpen321/usermanagement/ui/viewmodels/MusicPlayerViewModel.kt
- frontend/app/src/main/java/com/cpen321/usermanagement/data/model/Song.kt
- frontend/app/src/main/java/com/cpen321/usermanagement/data/repository/MusicRepository.kt
- frontend/app/src/main/java/com/cpen321/usermanagement/service/MusicPlayerService.kt
- frontend/app/src/main/java/com/cpen321/usermanagement/ui/components/MusicPlayer.kt
- frontend/app/src/main/java/com/cpen321/usermanagement/ui/viewmodels/MusicPlayerViewModel.kt

The following existing files need to be modified to provide the functionality and integrate it into the home page:
- frontend/app/build.gradle.kts (only add dependencies for exo music player)
- frontend/app/src/main/java/com/cpen321/usermanagement/di/RepositoryModule.kt (import music repository)
- frontend/app/src/main/java/com/cpen321/usermanagement/ui/screens/MainScreen.kt (actual screen)

And the following are xml files for icons that are used in the feature:
- frontend/app/src/main/res/drawable/ic_music_note.xml
- frontend/app/src/main/res/drawable/ic_pause.xml
- frontend/app/src/main/res/drawable/ic_play_arrow.xml
- frontend/app/src/main/res/drawable/ic_skip_next.xml
- frontend/app/src/main/res/drawable/ic_skip_previous.xml

And the following are the actual songs in the list that can be played
- frontend/app/src/main/res/raw/song1.mp3
- frontend/app/src/main/res/raw/song2.mp3
- frontend/app/src/main/res/raw/song3.mp3
- frontend/app/src/main/res/raw/song4.mp3
- frontend/app/src/main/res/raw/song5.mp3
- frontend/app/src/main/res/raw/song6.mp3
- frontend/app/src/main/res/raw/song7.mp3
- frontend/app/src/main/res/raw/song8.mp3
- frontend/app/src/main/res/raw/song9.mp3
- frontend/app/src/main/res/raw/song10.mp3
- frontend/app/src/main/res/raw/song11.mp3
- frontend/app/src/main/res/raw/song12.mp3
- frontend/app/src/main/res/raw/song13.mp3
- frontend/app/src/main/res/raw/song14.mp3
- frontend/app/src/main/res/raw/song15.mp3
- frontend/app/src/main/res/raw/song16.mp3
- frontend/app/src/main/res/raw/song17.mp3

## New Feature 2

