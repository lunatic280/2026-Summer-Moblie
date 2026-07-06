# Codex 설정 및 에이전트 구성 가이드

## 목표

이 프로젝트는 Kotlin + Jetpack Compose 기반 Android 음악 플레이어 앱이다. Spotify 스타일의 사용자 경험을 참고하지만, Spotify API, Spotify 로그인, 실제 Spotify 음원 스트리밍, 결제 기능은 구현하지 않는다. 앱은 샘플 MP3 또는 로컬 음악 파일을 재생하는 방식으로 만든다.

## Codex 기본 설정

Codex에는 아래 내용을 프로젝트 지시로 넣는 것을 추천한다.

```md
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
```

## 추천 구현 순서

1. `Music` 데이터 모델과 샘플 음악 목록 만들기
2. 홈, 검색, 재생, 보관함 Compose 화면 구성
3. Navigation Compose 추가
4. ViewModel + StateFlow로 재생 상태 관리
5. Media3 ExoPlayer 추가
6. `MusicService`로 백그라운드 재생 분리
7. 알림창 재생 제어 추가
8. 홈 화면 위젯 추가
9. 즐겨찾기와 최근 재생 기록 저장 추가

## 만들면 좋은 에이전트

생성된 에이전트 정의 파일:

- `.agents/android-music-player-builder.md`
- `.agents/compose-ui-reviewer.md`
- `.agents/playback-service-reviewer.md`

### 1. Android Music Player Builder

가장 먼저 만들 에이전트는 이 하나면 충분하다. 역할은 기획서를 기준으로 Android 앱 기능을 단계별로 구현하는 것이다.

권장 에이전트 지시문:

```md
너는 Kotlin, Jetpack Compose, Android Media3에 익숙한 Android 개발 에이전트다.
이 프로젝트의 목표는 Spotify 스타일 로컬 음악 플레이어 앱을 단계별로 구현하는 것이다.

반드시 지킬 것:
- spotify_style_music_player_plan.md와 AGENTS.md를 먼저 읽고 작업한다.
- Spotify API, 로그인, 실제 Spotify 음원 스트리밍은 구현하지 않는다.
- 샘플 데이터와 로컬 음원 재생 구조를 우선 사용한다.
- UI는 Compose + Material3로 작성한다.
- 큰 기능은 작은 단계로 나누어 구현한다.
- 서비스, 알림, 위젯은 UI와 기본 상태 관리가 안정된 뒤 추가한다.
- 변경 후 가능한 경우 ./gradlew :app:compileDebugKotlin을 실행한다.
```

### 2. Compose UI Reviewer

선택 사항이다. 홈, 검색, 재생 화면을 만든 뒤 UI 품질을 점검할 때 사용한다. 역할은 Compose 구조, 화면 밀도, 다크 테마, 미니 플레이어, 버튼 상태, Preview 누락 여부를 리뷰하는 것이다.

### 3. Playback Service Reviewer

선택 사항이다. ExoPlayer, `MediaSessionService`, foreground service, 알림 제어, 권한, AndroidManifest 설정을 추가한 뒤 검토할 때 사용한다. 백그라운드 재생은 오류가 나기 쉬우므로 별도 리뷰 에이전트가 있으면 좋다.

## 실제 요청 예시

처음에는 아래처럼 작게 요청하는 것이 좋다.

```md
spotify_style_music_player_plan.md를 기준으로 1단계만 구현해줘.
Music 데이터 모델, 샘플 음악 목록, 홈 화면의 음악 리스트 UI를 만들어줘.
아직 ExoPlayer, Service, 알림, 위젯은 구현하지 마.
변경 후 ./gradlew :app:compileDebugKotlin을 실행해줘.
```

그 다음 단계는 검색 화면, 재생 화면, 상태 관리, ExoPlayer 순서로 요청한다. 전체 앱을 한 번에 만들라고 요청하면 서비스, 권한, 위젯, DB 작업이 섞여 오류가 커질 수 있다.

## 멀티에이전트 스킬

생성된 스킬:

- 저장소 소스: `codex_skills/android-music-player-multiagent/`
- 개인 Codex 설치 위치: `~/.codex/skills/android-music-player-multiagent/`

사용 예시:

```md
Use $android-music-player-multiagent to implement 1단계.
Builder는 Music 데이터 모델과 홈 목록 UI를 구현하고,
Compose UI Reviewer는 변경된 Compose 화면을 리뷰하게 해줘.
아직 ExoPlayer, Service, 알림, 위젯은 구현하지 마.
```

## 주의 사항

- 실제 Spotify와 연결하는 기능은 만들지 않는다.
- 인터넷에서 음원을 다운로드하는 기능은 넣지 않는다.
- 샘플 음원은 직접 준비한 파일만 사용한다.
- 먼저 화면과 상태 구조를 안정화한 뒤 백그라운드 재생을 붙인다.
- Android 위젯은 Compose가 아니라 `RemoteViews` 기반으로 구현해야 한다.
