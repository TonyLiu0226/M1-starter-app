# M1

There are two features included here, with the second one building on top of the first one

## New Feature 1

**Name:** Play music

**Short description:** Create a music player to play songs from the application's main screen. This involves entirely frontend changes related to developing the
music player's UI, implementing the logic and functionality for playing, rewinding, and moving on to the next song, and shuffling the songs upon app load.

At this stage, the music player simply plays from a fixed list of songs that come with the app. In feature 2, we expand upon this functionality.

**Location and code:** 
Primarily in the frontend. The following files have been created to allow the music player to be added to the home screen:
- `frontend/app/src/main/java/com/cpen321/usermanagement/ui/viewmodels/MusicPlayerViewModel.kt`
- `frontend/app/src/main/java/com/cpen321/usermanagement/data/model/Song.kt`
- `frontend/app/src/main/java/com/cpen321/usermanagement/data/repository/MusicRepository.kt`
- `frontend/app/src/main/java/com/cpen321/usermanagement/service/MusicPlayerService.kt`
- `frontend/app/src/main/java/com/cpen321/usermanagement/ui/components/MusicPlayer.kt`
- `frontend/app/src/main/java/com/cpen321/usermanagement/ui/viewmodels/MusicPlayerViewModel.kt`

The following existing files need to be modified to provide the functionality and integrate it into the home page:
- `frontend/app/build.gradle.kts` (only add dependencies for exo music player)
- `frontend/app/src/main/java/com/cpen321/usermanagement/di/RepositoryModule.kt` (import music repository)
- `frontend/app/src/main/java/com/cpen321/usermanagement/ui/screens/MainScreen.kt` (actual screen)

And the following are xml files for icons that are used in the feature:
- `frontend/app/src/main/res/drawable/ic_music_note.xml`
- `frontend/app/src/main/res/drawable/ic_pause.xml`
- `frontend/app/src/main/res/drawable/ic_play_arrow.xml`
- `frontend/app/src/main/res/drawable/ic_skip_next.xml`
- `frontend/app/src/main/res/drawable/ic_skip_previous.xml`

And the following are the actual songs in the list that can be played
- `frontend/app/src/main/res/raw/song1.mp3`
- `frontend/app/src/main/res/raw/song2.mp3`
- `frontend/app/src/main/res/raw/song3.mp3`
- `frontend/app/src/main/res/raw/song4.mp3`
- `frontend/app/src/main/res/raw/song5.mp3`
- `frontend/app/src/main/res/raw/song6.mp3`
- `frontend/app/src/main/res/raw/song7.mp3`
- `frontend/app/src/main/res/raw/song8.mp3`
- `frontend/app/src/main/res/raw/song9.mp3`
- `frontend/app/src/main/res/raw/song10.mp3`
- `frontend/app/src/main/res/raw/song11.mp3`
- `frontend/app/src/main/res/raw/song12.mp3`
- `frontend/app/src/main/res/raw/song13.mp3`
- `frontend/app/src/main/res/raw/song14.mp3`
- `frontend/app/src/main/res/raw/song15.mp3`
- `frontend/app/src/main/res/raw/song16.mp3`
- `frontend/app/src/main/res/raw/song17.mp3`

## New Feature 2

**Name:** Allows users to enter the name of an artist and discover open-source, downloadable music similar to the artist's genres. Implements Spotify and Audius APIs.

**Short description:** 
The user enters the name of an artist (or any search term is also OK) in the text input in the "Find Music Similar to Your Favourite Artist" section, and clicking the search button will make a GET request to the Spotify search API, which searches for an artist matching the input. Once the artist is found, it will be displayed below the button, along with the artist's genres. If genre information is available, then it will be used as parameters to the API requests for Audius, a seperate, decentralized music streaming service that supports downloadable tracks. We first search Audius for a list of downloadable tracks matching the parameter. Then, we try every track in the list (in a randomized order), first checking that the track meets some basic requirements (length less than 15 min, track is downloadable), before attempting to download it by calling the `/download` endpoint. 

If the download is successful, the URL will be reflected below the artist and genres, and the music player component will be updated such that the newly downloaded song will start playing. If unsuccessful, then we will try the next song in the list, until either we are successful, or we have tried every song in the list, or we time out (currently set to 45 seconds). We make attempts to try multiple tracks before we "give up" to maximize the chance of finding a song to play, within reasonable resource and time limits.

### Error handling
Since API requests from these third party services are prone to returning errors, there needs to be robust error handling in both the frontend and backend. In the backend, we handle errors that occur at each step with specific messages (eg. No Audius servers found from which to download files, no artists found from Spotify API call, no suitable tracks found within a server, error with downloading or searching for a particular track), and handling logic. For errors that result from trying to download or search for a specific track (for example, it is possible due to limitations of the Audius API, even when we filter only for downloadable songs, certain tracks cannot be downloaded), the error handling logic results in trying the next track, but if it is an error with the initial Spotify API call, or with an Audius server, the error is caught and returned.

Frontend error handling is in the `MainViewModel` component's `findSimilarMusic()` function, and errors are displayed to the user in banners, with the message dependent on the cause of the error (whether it came from initially fetching the artist, not finding any suitable tracks to download and play, or specific issues with downloading a track).

**Usage Notes**
Note that due to the nature of the data returned from third party APIs, some artists will result in no tracks similar to the artist's genre being downloaded and therefore played. As explained above, this may be due to lack of genre information on certain artists from Spotify API, lack of downloadable tracks matching the artist's genres, or taking too long to find a track that is downloadable. If this is the case, please try another artist.

For testing, here are examples of some artists that seem to work well (resulting in a track being found and played):
- BTS
- Kenshi Yonezu
- Ed Sheeran
- BLACKPINK
- Queen

Here are examples of some artists that did not work for me (due to Spotify not showing the genre information for these artists):
- Taylor Swift
- Justin Bieber
- Imagine Dragons
- Twenty One Pilots
- Keshi

**Location and Code** Spread across both backend and frontend

### Backend
- `backend/src/controllers/music.controller.ts` - contains majority of the logic related to making API requests for searching for artists and tracks similar to the artist's genres, and for downloading tracks.
- `backend/src/routes/music.routes.ts` - defines the routes that when called, will trigger the API requests above.
- `backend/src/types/music.types.ts` - defines types for API requests and responses

### Frontend
- `frontend/app/src/main/java/com/cpen321/usermanagement/data/remote/dto/MusicModels.kt` - API request types
- `frontend/app/src/main/java/com/cpen321/usermanagement/data/remote/api/MusicInterface.kt` - Defines the routes that the app needs to call for this feature. Will call the relevant route in `backend/src/routes/music.routes.ts`.
- `frontend/app/src/main/java/com/cpen321/usermanagement/data/repository/MusicApiRepository.kt` - Defines interface for API calls related to this feature
- `frontend/app/src/main/java/com/cpen321/usermanagement/ui/screens/MainScreen.kt` - Modified to include the UI for this feature
- `frontend/app/src/main/java/com/cpen321/usermanagement/ui/viewmodels/MainViewModel.kt` - Modified to include the functionality required to make the UI above work (including code that calls the API through `MusicApiRepository`, and the error handling for different cases)
- `frontend/app/src/main/java/com/cpen321/usermanagement/ui/viewmodels/MusicPlayerViewModel.kt` - modified to support streaming songs from URL in addition to the predefined songs from local files
- `frontend/app/src/main/java/com/cpen321/usermanagement/service/MusicPlayerService.kt` - modified to support streaming songs from URL in addition to the predefined songs from local files



