# Script Timestamp Sync

Complete native Android Kotlin + Jetpack Compose project.

## What it does
Select a numbered PDF script and an MP3/WAV/M4A file. The app extracts script lines, removes leading line numbers, sends the audio to OpenAI Whisper for word-level timestamps, aligns each script line using normalization + fuzzy matching + an offline bundled lexicon, and displays timestamps such as [00:00:03.120].

## Included
- Kotlin + Jetpack Compose + MVVM
- Coroutines
- Media3/ExoPlayer dependency
- PdfBox-Android PDF extraction
- OpenAI Whisper word timestamps
- Hindi Devanagari + English + Hinglish normalization
- 100,000+ real dictionary entries generated at build time and bundled into APK assets
- Hidden/internal dictionary: no dictionary screen, button, menu or toggle
- Confidence and Needs Review status
- TXT, SRT and CSV export
- Settings for API key, timestamp milliseconds, and language mode
- INTERNET, READ_MEDIA_AUDIO and Android 12-and-lower READ_EXTERNAL_STORAGE permissions
- Min SDK 24 / Target SDK 34

## Build
GitHub Actions generates the real bundled dictionary, compiles debug and release APKs, signs the release APK with a CI-only key, verifies package identity/signature, and uploads the final APK artifact.

APK build karne ke liye Android Studio me ye steps follow karo: project open karo, JDK 17 select karo, Gradle sync hone do, phir Build > Build Bundle(s) / APK(s) > Build APK(s) select karo.

## Runtime
Open Settings and enter an OpenAI API key. Choose the PDF and audio, then press Process. The dictionary stays local after installation; Whisper transcription requires internet access.


<!-- CI final APK verification trigger -->
