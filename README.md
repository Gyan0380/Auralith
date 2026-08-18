# Native Android Media Player & File Manager

100% Native Kotlin + Jetpack Compose + Media3

## Features
- Video Player Surface: Media3 ExoPlayer with custom Material 3 controls
- Storage Scanner: MediaStore query for files >100MB with RecoverableSecurityException secure delete
- App Manager: PackageManager lists user apps with size via StorageStatsManager + uninstall intent
- Material 3 UI + fluid sliding animations (left/right tabs, slide up for full player)

## Build
./gradlew assembleDebug

## GitHub CI
Push to main branch -> .github/workflows/android.yml builds Debug and Release APK as artifacts automatically.

Artifacts: Actions tab -> Download app-debug-apk and app-release-apk

## Permissions
READ_MEDIA_VIDEO, READ_MEDIA_AUDIO, QUERY_ALL_PACKAGES
