# Script Timestamp Sync

Complete native Android Kotlin + Jetpack Compose project.

## What it does
Select a numbered PDF script and an MP3/WAV/M4A file. The app extracts the script lines, sends audio to OpenAI Whisper for speech-to-text with word-level timestamps, aligns each script line using normalization + fuzzy matching + an offline bundled lexicon, and displays timestamps such as [00:00:03.120].

## Included
- Kotlin + Jetpack Compose + MVVM
- Coroutines
- Media3/ExoPlayer dependency
- PdfBox-Android PDF extraction
- OpenAI Whisper word timestamps
- Hindi Devanagari + English + Hinglish normalization
- 100,000+ dictionary entries generated at build time and bundled into the APK assets
- Hidden/internal dictionary: no dictionary screen, button, menu or toggle
- Confidence and Needs Review status
- TXT, SRT and CSV export
- INTERNET, READ_MEDIA_AUDIO and Android 12-and-lower READ_EXTERNAL_STORAGE permissions
- Min SDK 24 / Target SDK 34

## Build
The GitHub Actions workflow generates the bundled dictionary, compiles the debug APK and release APK, verifies the package ID, and uploads both APKs as the Script-Timestamp-Sync-APKs artifact.

APK build karne ke liye Android Studio me ye steps follow karo: project open karo, JDK 17 select karo, Gradle sync hone do, phir Build > Build Bundle(s) / APK(s) > Build APK(s) select karo.

## Runtime
Open Settings and enter an OpenAI API key. Then choose the PDF and audio and press Process. The dictionary remains entirely local after installation; only Whisper transcription requires internet access.