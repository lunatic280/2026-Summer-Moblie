# 현재 프로젝트 동작 정리

작성일: 2026-07-07
프로젝트: Kotlin + Jetpack Compose 기반 로컬 음악 플레이어 앱

## 1. 전체 개요

이 프로젝트는 Spotify 스타일 UI를 참고한 Android 로컬 음악 플레이어 앱이다. 실제 Spotify API, 로그인, 스트리밍, 결제 기능은 사용하지 않고, 앱 안에 포함된 샘플 WAV 파일을 재생한다.

현재 앱의 핵심 흐름은 다음과 같다.

1. 앱이 실행되면 `MainActivity`가 `LocalPlaybackController`를 생성한다.
2. `SampleMusicCatalog.songs`에 정의된 샘플 음악 목록을 `HomeScreen`에 전달한다.
3. `HomeScreen`은 Compose UI와 Navigation Compose로 홈, 검색, 플레이어, 보관함 화면을 전환한다.
4. `MusicPlayerViewModel`은 검색어, 필터, 현재 곡, 즐겨찾기, 최근 선택 목록, 재생 진행률을 `StateFlow`로 관리한다.
5. 사용자가 곡을 선택하면 UI 상태가 갱신되고, `LocalPlaybackController`가 Media3 `MediaController`를 통해 `MusicPlaybackService`에 재생 명령을 보낸다.
6. `MusicPlaybackService`는 `ExoPlayer`와 `MediaSession`을 생성해 실제 로컬 음원을 재생한다.

## 2. 앱 시작 흐름

앱의 진입점은 `MainActivity`다.

`MainActivity`에서 하는 일:

- `LocalPlaybackController(this)` 생성
- Activity lifecycle에 playback controller 등록
- `enableEdgeToEdge()` 적용
- Compose UI 렌더링
- `playbackController.playbackState`를 `collectAsStateWithLifecycle()`로 구독
- `HomeScreen`에 음악 목록과 재생 콜백 전달

핵심 연결 구조:

```text
MainActivity
-> LocalPlaybackController 생성
-> playbackState 구독
-> AndroidProjetTheme 적용
-> HomeScreen 렌더링
-> HomeScreen에서 발생한 재생 이벤트를 LocalPlaybackController로 전달
```

## 3. 데이터 구조

음악 데이터는 `SampleMusicCatalog`에 하드코딩되어 있다.

현재 샘플 곡은 12개다.

- Night Drive
- Study Beats
- Rainy Day
- Morning Coffee
- City Pop
- Slow Sunset
- Metro Lines
- Deep Focus
- Late Walk
- Soft Keys
- Window Light
- Arcade Evening

각 곡은 `Music` 모델로 표현된다.

```text
Music
- id
- title
- artist
- album
- albumImage
- fileName
- durationMillis
- isFavorite
```

현재 음원 파일은 `app/src/main/res/raw/` 아래의 WAV 리소스로 포함되어 있다. `PlaybackMediaItems.kt`는 `Music.fileName`을 raw resource 이름으로 변환하고, `android.resource://` URI를 가진 Media3 `MediaItem`을 만든다.

## 4. 화면 구조

`HomeScreen`은 Navigation Compose를 사용해 4개 목적지를 관리한다.

```text
Home
Search
Player
Library
```

하단에는 `HomeBottomBar`가 항상 표시된다. 이 하단 영역은 현재 곡을 보여주는 미니 플레이어와 화면 이동 버튼 역할을 한다.

### 4.1 홈 화면

홈 화면은 사용자가 앱을 처음 열었을 때 보는 화면이다.

주요 구성:

- 전체 트랙 수 표시
- 검색 입력 영역
- 필터 선택
- `Jump back in` 빠른 접근 그리드
- 최근 선택 음악
- 집중 음악 추천 영역
- 전체 로컬 트랙 목록
- 하단 미니 플레이어

사용자가 홈 화면에서 곡을 누르면 다음 일이 일어난다.

```text
곡 클릭
-> MusicPlayerViewModel.selectMusic(music)
-> 현재 곡, 재생 상태, 최근 선택 목록 갱신
-> Player 화면으로 이동
-> LocalPlaybackController.play(music)
-> 실제 음원 재생 시작
```

### 4.2 검색 화면

검색 화면은 제목, 가수, 앨범 기준으로 음악을 필터링한다.

검색 로직:

```text
검색어 입력
-> MusicPlayerViewModel.updateSearchQuery(query)
-> filterMusic() 실행
-> title / artist / album 중 하나라도 포함하면 결과에 표시
```

홈 화면에서 검색어를 입력하면 자동으로 검색 화면으로 이동한다.

### 4.3 플레이어 화면

플레이어 화면은 현재 선택된 곡을 상세하게 보여준다.

주요 구성:

- 현재 재생 중인 앨범/곡 정보
- 앨범 아트 placeholder
- 곡 제목
- 가수명
- 즐겨찾기 버튼
- 진행률 슬라이더
- 현재 위치 / 전체 길이
- 이전 곡 / 재생 또는 일시정지 / 다음 곡 버튼
- 다음 재생 큐 미리보기

진행률 슬라이더를 움직이면 다음 흐름으로 동작한다.

```text
Slider 값 변경
-> MusicPlayerViewModel.updateProgress(progress)
-> LocalPlaybackController.seekToProgress(progress)
-> MediaController.seekTo(...)
-> playbackState 갱신
-> UI 진행률 동기화
```

### 4.4 보관함 화면

보관함 화면은 저장된 음악과 최근 선택 흐름을 보여준다.

현재 구성:

- 전체 음악 통계
- 즐겨찾기 목록
- 최근 선택 목록
- 앨범 대표 목록

현재 즐겨찾기와 최근 재생은 앱 메모리 안에서만 유지된다. 앱을 완전히 종료했다가 다시 열면 저장 상태가 유지되지 않는다.

## 5. 상태 관리 방식

상태 관리는 `MusicPlayerViewModel`이 담당한다.

주요 상태:

```text
MusicPlayerUiState
- musicList
- searchQuery
- selectedFilter
- filteredMusic
- recentMusic
- currentMusic
- isPlaying
- playbackProgress
- currentPositionMillis
- queuePreview
```

`MusicPlayerViewModel`은 내부 값을 변경한 뒤 `emitState()`를 호출해 새로운 `MusicPlayerUiState`를 만든다. Compose 화면은 이 값을 `collectAsStateWithLifecycle()`로 구독하고 자동으로 다시 그려진다.

주요 상태 변경 함수:

- `updateSearchQuery()` : 검색어 변경
- `selectFilter()` : Music / Favorites / Albums 필터 변경
- `selectMusic()` : 현재 곡 선택 및 최근 선택 기록
- `togglePlay()` : UI상의 재생 상태 토글
- `previousTrack()` : 이전 곡 선택
- `nextTrack()` : 다음 곡 선택
- `updateProgress()` : UI 진행률 변경
- `syncPlaybackState()` : 실제 플레이어 상태를 UI 상태에 반영
- `toggleFavorite()` : 현재 곡 즐겨찾기 토글

## 6. 재생 동작 방식

실제 재생은 `LocalPlaybackController`와 `MusicPlaybackService`가 나누어 담당한다.

### 6.1 LocalPlaybackController

`LocalPlaybackController`의 역할:

- `MediaController` 비동기 연결
- `MusicPlaybackService`와 연결할 `SessionToken` 생성
- 곡 선택 시 Media3 `MediaItem` 생성
- ExoPlayer 재생 목록 설정
- 재생 / 일시정지 / 탐색 명령 전달
- 실제 재생 상태를 `LocalPlaybackState`로 변환
- 500ms마다 진행률 업데이트

현재 로컬 변경 기준으로는 단일 곡만 설정하지 않고, `SampleMusicCatalog.songs` 전체를 Media3 재생 목록으로 만들어 넣는다.

```text
곡 선택
-> 선택한 곡을 MediaItem으로 변환
-> 전체 샘플 곡을 MediaItem 목록으로 변환
-> 선택한 곡 index를 찾아 setMediaItems(mediaItems, startIndex, 0L)
-> prepare()
-> play()
```

이 구조 덕분에 Media3 내부의 current media item이 바뀔 때 현재 곡 ID를 다시 찾아 UI에 동기화할 수 있다.

### 6.2 MusicPlaybackService

`MusicPlaybackService`는 Media3 `MediaSessionService`다.

역할:

- `ExoPlayer` 생성
- `MediaSession` 생성
- `MediaController` 요청이 들어오면 현재 세션 반환
- 서비스 종료 시 player와 session release
- 기본 Media3 notification provider 설정
- 알림이나 외부 컨트롤에서 앱으로 돌아올 수 있는 `PendingIntent` 설정

현재 로컬 변경 기준으로는 다음 알림 설정이 들어가 있다.

```text
DefaultMediaNotificationProvider
- channelId: music_playback
- channelName: Music playback
- notificationId: 1001
```

알림을 눌렀을 때는 `MainActivity`로 돌아오도록 `PendingIntent`가 연결된다.

## 7. UI 상태와 실제 재생 상태 동기화

이 앱은 UI 상태와 실제 플레이어 상태를 분리한다.

```text
UI 상태
-> MusicPlayerViewModel

실제 재생 상태
-> LocalPlaybackController
-> MediaController
-> MusicPlaybackService
-> ExoPlayer
```

실제 재생 상태는 `LocalPlaybackController.playbackState`로 노출된다.

`MainActivity`는 이 값을 구독하고 `HomeScreen`에 넘긴다.

`HomeScreen`은 `LaunchedEffect(playbackMusicId, playbackIsPlaying, playbackProgress)` 안에서 `playerViewModel.syncPlaybackState()`를 호출해 실제 재생 상태를 UI 상태에 반영한다.

즉, 최종 흐름은 다음과 같다.

```text
ExoPlayer 상태 변경
-> Player.Listener 호출
-> LocalPlaybackController.emitPlaybackState()
-> playbackState StateFlow 갱신
-> MainActivity가 상태 수집
-> HomeScreen에 props 전달
-> MusicPlayerViewModel.syncPlaybackState()
-> Compose UI 재구성
```

## 8. 현재 테스트 상태

현재 `MusicPlayerViewModelTest`에서 ViewModel 중심 로직을 검증한다.

검증 중인 내용:

- 검색어가 title / artist / album 기준으로 필터링되는지
- 곡 선택 시 현재 곡과 재생 상태가 바뀌는지
- 즐겨찾기 토글이 Favorites 필터에 반영되는지
- 다음 곡이 마지막 곡에서 첫 곡으로 순환되는지
- 최근 선택 목록이 중복 없이 최신순으로 유지되는지
- 최근 선택 목록이 최대 8개로 제한되는지
- 이전/다음 곡 이동이 최근 선택 목록에 반영되는지
- 진행률이 0~1 범위로 보정되는지
- 빈 음악 목록에서는 재생 토글이 동작하지 않는지

최근 확인한 명령:

```bash
./gradlew :app:compileDebugKotlin
./gradlew :app:testDebugUnitTest
```

두 명령 모두 성공했다.

## 9. GitHub Pages 발표 자료 상태

발표 자료는 `2026-Summer-Moblie` 저장소에 PR로 올라갔고, PR은 머지된 상태다.

공개 발표 페이지:

```text
https://lunatic280.github.io/2026-Summer-Moblie/
```

현재 URL은 `HTTP/2 200`으로 정상 응답한다.

발표 자료 구성:

- 프로젝트 개요
- 프로젝트 목표
- 핵심 기능
- 앱 구조
- 시연 흐름
- 확장 방향
- 마무리

공개 페이지에는 외부에 보여줘도 되는 내용만 넣었다. 세부 구현 메모와 진행상황은 별도 Markdown 문서로 분리했다.

## 10. 현재 로컬 작업트리 상태

현재 로컬에는 아직 커밋되지 않은 변경이 남아 있다.

남아 있는 변경:

- `LocalPlaybackController.kt`
  - 전체 샘플 곡을 Media3 재생 목록으로 설정
  - media item transition 시 현재 곡을 ID로 다시 찾음
  - 재생 상태 계산 시 실제 player의 current media item을 우선 사용

- `MusicPlaybackService.kt`
  - 기본 Media3 notification provider 설정
  - 알림 채널 ID와 알림 ID 설정
  - 알림 또는 외부 컨트롤에서 앱으로 복귀하는 PendingIntent 설정

- `strings.xml`
  - `playback_notification_channel_name` 문자열 추가

- `docs/index.html`
  - 공개 발표 페이지 로컬 사본

- `presentation_private_notes.md`
  - 발표자 개인 노트 로컬 사본

주의할 점:

현재 로컬 저장소의 `origin`은 `2026-AndroidProgramming`을 가리킨다. 실제 발표 페이지 PR은 별도 임시 checkout을 통해 `2026-Summer-Moblie`에 올렸다.

## 11. 아직 남은 작업

앱 기능 기준으로 남은 작업은 다음과 같다.

1. 로컬에 남아 있는 Media3 재생 목록/알림 변경을 정식 PR로 올릴지 결정
2. Android 13 이상 알림 권한 런타임 요청 구현
3. 실제 기기에서 백그라운드 재생과 알림 제어 테스트
4. 즐겨찾기와 최근 선택 목록을 DataStore 또는 Room에 저장
5. 홈 화면 위젯 구현
6. 현재 로컬 `origin`을 실제 작업 대상 저장소인 `2026-Summer-Moblie`로 정리할지 결정

## 12. 한 줄 요약

현재 앱은 로컬 샘플 음원 기반으로 음악 목록, 검색, 상세 플레이어, 미니 플레이어, 즐겨찾기, 최근 선택, Media3 기반 재생 구조까지 동작한다. 발표용 GitHub Pages는 `2026-Summer-Moblie`에 배포되어 정상 공개 중이고, 다음 단계는 로컬에 남은 Media3 알림/재생 목록 개선 변경을 정식 PR로 올리는 것이다.
