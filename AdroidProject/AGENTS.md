# Repository Guidelines

## Music Player Project Defaults
이 저장소는 Kotlin + Jetpack Compose 기반 Android 음악 플레이어 앱이다.
목표는 Spotify 스타일 UI를 참고하되 Spotify API, 로그인, 실제 Spotify 음원, 결제 기능은 구현하지 않는다.
샘플 MP3 또는 로컬 음악 파일만 재생한다.

기술 방향:
- UI: Jetpack Compose + Material3
- 상태 관리: ViewModel + StateFlow
- 화면 이동: Navigation Compose
- 재생: Media3 ExoPlayer
- 백그라운드 재생: MediaSessionService 또는 Foreground Service
- 알림 제어: MediaStyle notification
- 홈 위젯: AppWidgetProvider + RemoteViews + PendingIntent
- 저장소: 초기에는 in-memory 데이터, 이후 DataStore 또는 Room으로 확장

작업 원칙:
- 한 번에 모든 기능을 만들지 말고 단계별로 구현한다.
- 먼저 음악 목록, 검색, 미니 플레이어 UI를 완성한다.
- 이후 ExoPlayer, 서비스, 알림, 위젯, 즐겨찾기 순서로 확장한다.
- Spotify API나 외부 스트리밍 연동은 추가하지 않는다.
- 저작권 문제가 있는 음원 파일은 생성하거나 다운로드하지 않는다.
- 변경 후 ./gradlew :app:compileDebugKotlin을 실행해 컴파일을 확인한다.

## Project Structure & Module Organization
This repository is a single Android application named `AndroidProjet` with one Gradle module, `:app`.

- `app/src/main/java/com/job/androidprojet/` contains Kotlin source code, including `MainActivity.kt`.
- `app/src/main/java/com/job/androidprojet/ui/theme/` contains Jetpack Compose theme files (`Color.kt`, `Theme.kt`, `Type.kt`).
- `app/src/main/res/` contains Android resources such as launcher drawables, XML rules, strings, colors, and themes.
- `app/src/test/java/` contains local JVM unit tests.
- `app/src/androidTest/java/` contains instrumented Android tests that require an emulator or device.
- `gradle/libs.versions.toml` centralizes dependency and plugin versions.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repository root.

- `./gradlew :app:assembleDebug` builds a debug APK.
- `./gradlew :app:testDebugUnitTest` runs local JUnit tests.
- `./gradlew :app:connectedDebugAndroidTest` runs instrumented tests on a connected emulator or device.
- `./gradlew :app:lintDebug` runs Android lint for the debug variant.
- `./gradlew :app:compileDebugKotlin` checks Kotlin compilation quickly without producing a full APK.

## Coding Style & Naming Conventions
Write Kotlin using standard Android style: 4-space indentation, trailing commas only when they improve diffs, and clear imports without wildcards. Use `PascalCase` for classes and composable functions, `camelCase` for variables and functions, and `UPPER_SNAKE_CASE` only for constants. Keep composables small and previewable; add `@Preview` functions beside the UI they exercise. Resource names should be lowercase with underscores, for example `ic_launcher_background.xml`.

## Testing Guidelines
Use JUnit for local tests and AndroidX/JUnit4 for instrumented tests. Place fast logic tests under `app/src/test/java` and UI/device-dependent tests under `app/src/androidTest/java`. Name tests after expected behavior, such as `addition_isCorrect` or `useAppContext`. Run unit tests before committing; run connected tests when changing Android framework integration or Compose UI behavior.

## Commit & Pull Request Guidelines
The current history uses short, informal messages, including Korean summaries and date-like entries. Going forward, prefer concise imperative commits with a clear scope, such as `Update keypad layout` or `Add player theme colors`. Pull requests should describe the change, list test commands run, link any related issue, and include screenshots or screen recordings for visible UI changes.

## Security & Configuration Tips
Do not commit local SDK paths, generated APKs, signing keys, or secrets. Keep dependency versions in `gradle/libs.versions.toml` and avoid adding project repositories outside `settings.gradle.kts`, which currently enforces centralized repositories.
