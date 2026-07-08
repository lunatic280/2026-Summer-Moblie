# 프로젝트 기능 현황 및 개발 로드맵

작성일: 2026-07-07
최근 업데이트: 2026-07-08
프로젝트: Kotlin + Jetpack Compose 기반 Spotify 스타일 음악 플레이어

## 1. 현재 진행 상태 요약

현재 앱은 **로컬 샘플 음원 재생, 검색, 플레이어 UI, 백그라운드 재생 서비스, 알림 구조, 홈 화면 위젯, 즐겨찾기/최근 기록 저장, API 30초 미리듣기 통합, API preview artwork 표시 개선**까지 구현된 상태다.

Spotify API, Spotify 로그인, 실제 Spotify 음원 스트리밍, 다운로드, 결제 기능은 구현하지 않는다. API 미리듣기는 iTunes/MusicBrainz 기반 검색 결과의 30초 preview URL을 재생하는 기능이며, Spotify 연동이나 음원 다운로드가 아니다.

최근 검증 결과:

- `./gradlew :app:compileDebugKotlin` 성공
- `./gradlew :app:testDebugUnitTest` 성공
- `./gradlew :app:lintDebug` 성공
- `./gradlew :app:connectedDebugAndroidTest` 성공
- Pixel_8 AVD에서 API 30초 preview 재생 후 `dumpsys media_session` / `dumpsys notification --noredact`로 MediaSession과 알림 메타데이터 표시를 확인했다.
- API preview artwork를 플레이어 상세 화면, 미니 플레이어, 큐 목록에 표시하도록 수정한 뒤 `compileDebugKotlin`, `testDebugUnitTest`, `lintDebug`를 다시 통과했다.
- Pixel_8 AVD에 debug APK를 설치하고 package dump에서 홈 화면 위젯 receiver와 `MusicPlaybackService` 등록을 확인했다.

## 2. 구현 완료된 기능

### 2.1 기본 앱 구조

- Kotlin + Jetpack Compose + Material3 기반 Android 앱 구조가 구성되어 있다.
- 단일 Gradle 모듈 `:app`으로 구성되어 있다.
- `MainActivity`에서 Compose UI, ViewModel 상태, Media3 재생 컨트롤러를 연결한다.
- Navigation Compose 기반으로 홈, 검색, 플레이어, 보관함 화면을 전환한다.

### 2.2 로컬 샘플 음악 데이터

- `SampleMusicCatalog`에 앱 내장 샘플 음악 목록이 정의되어 있다.
- 샘플 음원은 `res/raw`의 WAV 파일을 사용한다.
- `Music` 모델은 로컬 음원과 API 30초 preview 항목을 함께 표현할 수 있도록 확장되었다.
- `PlaybackMediaItems`가 로컬 raw resource와 HTTPS preview URL을 Media3 `MediaItem`으로 변환한다.

### 2.3 홈 화면

- 전체 트랙 수, 검색 입력, `Music` / `Favorites` / `Albums` 필터를 제공한다.
- 빠른 접근 그리드, 최근 선택 음악, 즐겨찾기, 추천 섹션, 전체 로컬 트랙 목록을 표시한다.
- API 30초 미리듣기 추천 섹션을 표시한다.
- 곡 또는 API preview를 선택하면 같은 플레이어 화면으로 이동하고 재생을 시작한다.

### 2.4 검색 화면

- 로컬 음악을 제목, 아티스트, 앨범 기준으로 검색한다.
- 검색어 입력 시 홈에서 검색 화면으로 이동한다.
- iTunes Search API와 MusicBrainz API 기반 온라인 검색 결과를 함께 표시한다.
- preview URL이 있는 온라인 결과는 30초 API 미리듣기로 재생할 수 있다.
- 온라인 검색은 다운로드나 Spotify 스트리밍이 아니라 preview URL 재생과 메타데이터 표시 용도다.

### 2.5 플레이어 상세 화면

- 현재 재생 중인 항목의 제목, 아티스트, 앨범, 진행률을 표시한다.
- 재생 / 일시정지, 이전, 다음, seek 제어를 제공한다.
- 로컬 곡은 즐겨찾기 토글을 제공한다.
- API preview는 즐겨찾기 대신 `API preview` 출처 배지를 표시한다.
- API preview artwork URL이 있는 경우 플레이어 상세 화면의 큰 앨범 영역에 실제 이미지를 표시한다.
- 다음 재생 큐 미리보기를 표시한다.

### 2.6 미니 플레이어와 하단 내비게이션

- 현재 재생 중인 로컬 곡 또는 API preview를 미니 플레이어에 표시한다.
- API preview artwork URL이 있는 경우 미니 플레이어에도 실제 이미지를 표시한다.
- 미니 플레이어에서 이전, 재생 / 일시정지, 다음 제어가 가능하다.
- 곡을 선택하기 전에는 미니 플레이어가 불필요하게 먼저 표시되지 않는다.

### 2.7 상태 관리

- `MusicPlayerViewModel`이 UI 상태를 `StateFlow`로 관리한다.
- 검색어, 필터, 현재 재생 항목, 재생 상태, 진행률, 최근 선택, 즐겨찾기, 큐 미리보기를 관리한다.
- API preview 선택 시 preview 결과를 30초짜리 `Music` 항목으로 변환해 로컬 곡과 같은 UI 상태 흐름에 넣는다.
- 실제 Media3 재생 상태는 `LocalPlaybackController`에서 받아 UI에 동기화한다.

### 2.8 실제 재생 및 백그라운드 서비스

- Media3 `ExoPlayer` 기반 재생이 구현되어 있다.
- `MusicPlaybackService`는 `MediaSessionService`로 동작한다.
- `LocalPlaybackController`가 `MediaController`로 서비스에 연결한다.
- 로컬 곡과 API 30초 preview가 같은 MediaSessionService 경로를 사용한다.
- 재생, 일시정지, 이전, 다음, seek가 동작한다.
- API preview는 서비스 레벨에서 30초 제한을 적용하고, 무한 반복되지 않도록 처리했다.
- 오디오 포커스와 이어폰 분리 처리 기본 설정이 들어가 있다.

### 2.9 알림 재생 구조

- `MusicPlaybackService`에 Media3 기반 알림 provider 설정이 들어가 있다.
- 알림 제목에는 현재 곡 제목, 알림 본문에는 아티스트와 앨범이 함께 표시되도록 provider를 보강했다.
- 로컬 곡과 API preview의 `MediaItem` metadata에 title, artist, album, subtitle, description, duration, media type을 명시한다.
- MediaSession notification controller가 이전 / 재생 또는 일시정지 / 다음 슬롯을 안정적으로 받을 수 있도록 이전/다음 media button preference를 지정했다.
- 알림 클릭 시 앱으로 돌아오는 `PendingIntent`가 연결되어 있다.
- MediaSession 기반 알림 제어 구조를 사용한다.
- foreground service 및 media playback service 권한/선언이 AndroidManifest에 들어가 있다.

### 2.10 홈 화면 위젯

- `AppWidgetProvider` + `RemoteViews` 기반 홈 화면 위젯이 구현되어 있다.
- 위젯은 현재 재생 항목 제목, 아티스트, 재생 상태를 표시한다.
- 이전, 재생 / 일시정지, 다음, 검색 열기 버튼을 제공한다.
- 위젯 버튼은 `PendingIntent`로 `MusicPlaybackService` 또는 앱 진입점에 명령을 전달한다.
- 앱 상단의 `Add widget` 버튼으로 런처 위젯 핀 요청을 보낼 수 있다.
- 위젯 재생 / 일시정지 버튼은 저장된 표시 상태에 의존한 단순 토글이 아니라 목표 재생 상태를 명시해 서비스에 전달한다.
- 위젯 상태는 SharedPreferences 기반 저장소로 관리한다.
- API preview 상태도 위젯에 표시할 수 있다.
- 단, 앱 프로세스 재시작 후 오래된 preview URL을 위젯에서 다시 재생하지 않도록 막아 두었다.
- 위젯 명령이 재생 가능한 로컬 큐를 복원하지 못하면 foreground service를 즉시 종료해 알림 없는 서비스가 남지 않도록 처리했다.

### 2.11 저장 기능

- DataStore 기반으로 즐겨찾기와 최근 선택 기록을 저장한다.
- 앱 재실행 후에도 즐겨찾기와 최근 기록이 유지된다.
- 현재는 단순한 ID 목록 저장 방식이며, 데이터 구조가 커지면 Room으로 확장할 수 있다.

## 3. 남은 작업

### 3.1 실제 기기 검증

우선순위: 높음

- [ ] 에뮬레이터 또는 실제 기기에서 로컬 곡 재생을 확인한다.
- [ ] 앱을 백그라운드로 내려도 재생이 유지되는지 확인한다.
- [ ] 알림에서 이전 / 재생 / 일시정지 / 다음 제어가 정상 동작하는지 확인한다.
- [ ] 홈 화면 위젯 추가, 위젯 버튼 제어, 검색 화면 열기를 확인한다.
- [ ] 네트워크가 없을 때 API preview 검색/재생 실패 상태를 확인한다.
- [ ] API preview가 30초를 넘지 않고 멈추거나 다음 preview로 넘어가는지 확인한다.

### 3.2 알림 제어 고도화

우선순위: 높음

- [x] 로컬 곡과 API preview의 Media3 `MediaItem` metadata에 제목, 아티스트, 앨범, 설명, 길이, media type을 명시했다.
- [x] Media3 notification provider를 보강해 알림 제목은 곡 제목, 알림 본문은 `아티스트 - 앨범` 형식으로 표시되게 했다.
- [x] Pixel_8 AVD에서 API preview 재생 중 `dumpsys media_session`으로 `POP!`, `나연`, `IM NAYEON` metadata가 MediaSession에 들어오는 것을 확인했다.
- [x] Pixel_8 AVD에서 `dumpsys notification --noredact`로 media notification channel, transport category, title/text metadata, media session token이 게시되는 것을 확인했다.
- [x] MediaSession 알림 controller용 이전/다음 media button preference를 지정했다. 중앙 재생/일시정지는 Media3 기본 provider가 재생 상태에 따라 동적으로 처리한다.
- [x] 위젯에서 재생 가능한 항목이 없는 상태로 foreground service가 시작되면 즉시 종료해 알림 없는 서비스가 남지 않도록 했다.
- [x] 현재 외부 미디어 클라이언트 정책은 앱 내부 제어 전용으로 `MediaSessionService android:exported="false"`를 유지한다. Android Auto, Assistant, 외부 media browser가 필요해지면 export 전환과 `MediaSession.Callback.onConnect` 기반 controller gating을 먼저 설계한다.
- [x] 현재는 MediaSession media notification만 사용하므로 `POST_NOTIFICATIONS` 런타임 권한은 추가하지 않는다. 일반 알림을 별도로 추가할 때 manifest permission과 런타임 요청 흐름을 추가한다.
- [ ] 잠금 화면에서 제목, 아티스트, 앨범, 이전 / 재생 / 일시정지 / 다음 제어가 보이는지 직접 확인한다.
- [ ] 블루투스/이어폰 미디어 버튼으로 재생 / 일시정지 / 이전 / 다음 동작을 직접 확인한다.
- [ ] 현재 `minSdk` / `targetSdk`가 37이라 API 37 AVD 기준만 확인했다. Android 13~16 호환을 목표로 낮출 경우 버전별 알림 권한과 System UI 동작을 다시 검증한다.

### 3.3 API preview UX 개선

우선순위: 높음

- [x] 온라인 API 결과에서 30초 preview를 실제 재생한다.
- [x] API preview를 로컬 곡과 같은 미니 플레이어 / 상세 플레이어 / 서비스 경로에 통합했다.
- [x] API preview는 30초 제한을 적용했다.
- [x] API preview artwork를 플레이어 상세 화면의 큰 앨범 영역, 미니 플레이어, 큐 목록에 표시한다.
- [x] API preview 로딩 실패, 만료 URL, 네트워크 오류 메시지를 더 명확히 표시한다.
- [x] API preview와 로컬 곡이 섞이지 않도록 UI 문구를 계속 명확히 유지한다.

### 3.4 홈 화면 위젯 개선

우선순위: 중간

- [x] `AppWidgetProvider` 기반 위젯을 추가했다.
- [x] 현재 항목 표시와 기본 재생 제어를 연결했다.
- [x] 앱에서 런처 위젯 핀 요청을 보낼 수 있게 했다.
- [ ] 작은 위젯 크기에서 텍스트와 버튼이 잘리지 않는지 확인한다.
- [x] 작은 위젯 크기 대응을 위해 버튼 간격, 검색 문구, 텍스트 크기, 상태 문구를 줄였다.
- [x] API preview 표시 문구와 로컬 곡 표시 문구를 더 다듬는다.
- [x] 위젯 재생 / 일시정지를 명시적 목표 상태 기반 명령으로 바꿔 오래된 위젯 상태의 오동작을 줄였다.
- [ ] 위젯에서 재생 중인 항목 artwork를 표시할지 결정한다.

### 3.5 테스트 보강

우선순위: 중간

- [x] ViewModel의 초기 상태, 로컬 선택, 즐겨찾기, 최근 기록, API preview 큐 테스트가 있다.
- [x] `OnlineMusicSearchController` 상태 전이 테스트를 추가한다.
- [x] 온라인 repository는 실제 네트워크 대신 fake repository로 테스트한다.
- [ ] Compose UI 테스트로 검색, 탭 이동, 미니 플레이어 표시, 플레이어 상세 이동을 검증한다.
- [ ] 위젯과 알림은 가능하면 기기/에뮬레이터 기반 수동 또는 instrumented 검증을 추가한다.

### 3.6 플레이어 안정성 개선

우선순위: 중간

- [ ] MediaController 연결 전 버튼 입력에 대한 UI 상태를 더 명확히 표시한다.
- [ ] 서비스 연결 실패를 사용자에게 보여준다.
- [x] 재생 리소스 누락과 네트워크/API preview 재생 실패를 사용자에게 보여준다.
- [ ] 로컬 곡 큐와 API preview 큐의 반복/셔플 정책을 명확히 정한다.
- [ ] API preview 재생 중 앱 종료, 서비스 재시작, 위젯 제어의 경계 상황을 추가 검증한다.

### 3.7 실제 로컬 파일 확장

우선순위: 중간

- [ ] 현재는 앱에 포함된 샘플 WAV 리소스만 로컬 음원으로 재생한다.
- [ ] 사용자의 기기 로컬 음악 파일을 읽을지 결정한다.
- [ ] 로컬 파일 접근을 추가한다면 Android 권한, MediaStore, 파일 선택 UI를 함께 설계한다.

### 3.8 접근성 및 UI polish

우선순위: 중간

- [x] 주요 버튼의 content description을 점검하고 재생/위젯/즐겨찾기 동작 설명을 보강했다.
- [ ] 작은 화면에서 긴 제목, 긴 아티스트명, API 결과가 잘리지 않는지 확인한다.
- [x] API preview 미니 플레이어와 오류 배너가 있는 플레이어 상세 화면의 compact Preview를 추가했다.
- [ ] 텍스트 기반 재생 버튼을 아이콘 기반 컨트롤로 개선할지 검토한다.
- [ ] TalkBack 기준으로 곡 선택, preview 선택, 필터 선택, 재생 제어 흐름을 점검한다.

### 3.9 보관함 기능 확장

우선순위: 낮음

- [ ] 앨범별 상세 목록 화면을 추가할 수 있다.
- [ ] 즐겨찾기만 모아 듣기 기능을 추가할 수 있다.
- [ ] 최근 재생 기록 삭제 기능을 추가할 수 있다.
- [ ] DataStore 저장 구조가 복잡해지면 Room으로 전환한다.

## 4. 추천 개발 순서

1. 실제 기기/에뮬레이터에서 로컬 재생, API preview, 알림, 위젯 흐름을 수동 검증한다.
2. 알림/위젯의 경계 상황을 보완한다.
3. API preview 실패 상태와 네트워크 오류 UX를 개선한다.
4. Compose UI 테스트와 온라인 검색 fake repository 테스트를 추가한다.
5. 플레이어 아이콘, 접근성, 작은 화면 레이아웃을 polish한다.
6. 로컬 파일 선택 또는 MediaStore 연동 여부를 결정한다.
7. 보관함 상세 기능을 확장한다.

## 5. 현재 주의할 점

- Spotify API, Spotify 로그인, 실제 Spotify 음원 스트리밍은 구현하지 않는다.
- 인터넷에서 음원을 다운로드하는 기능은 추가하지 않는다.
- API preview는 30초 미리듣기 URL 재생만 허용한다.
- API preview는 로컬 곡과 같은 플레이어 UI를 사용하지만, 로컬 파일로 저장하거나 즐겨찾기 저장 대상으로 취급하지 않는다.
- 기능 변경 후에는 최소한 `./gradlew :app:compileDebugKotlin`을 실행한다.
- 재생 서비스, 알림, 위젯 변경 후에는 가능하면 `./gradlew :app:testDebugUnitTest`와 `./gradlew :app:lintDebug`도 실행한다.
