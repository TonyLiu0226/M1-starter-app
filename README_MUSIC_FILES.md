# MP3 Player - Audio Files Setup

## Adding Music Files

To use the MP3 player feature, you need to add MP3 files to the following location:
`frontend/app/src/main/res/raw/`

### Required File Names:
- `song1.mp3`
- `song2.mp3`
- `song3.mp3`
- `song4.mp3`
- `song5.mp3`

### Instructions:
1. Place your MP3 files in the `frontend/app/src/main/res/raw/` directory
2. Rename them to match the exact filenames listed above
3. The app will automatically detect and play these files in shuffle mode

### Features:
- **Shuffle Play**: Songs are played in random order
- **Play/Pause**: Toggle playback
- **Next Track**: Skip to the next random song
- **Previous Track**: Go back to the previous song
- **Progress Bar**: Shows current playback position and allows seeking
- **Auto-play**: Automatically plays the next song when current song ends

### Notes:
- Currently, only MP3 format is supported
- The player will cycle through all 5 songs in shuffle mode
- Songs are loaded locally from the app resources
- The empty placeholder files created are for development - replace them with actual MP3 files for testing 