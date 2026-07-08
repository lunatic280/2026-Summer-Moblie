# MainActivity.kt

원본 파일: `app/src/main/java/com/job/androidprojet/MainActivity.kt`

## 한 줄 역할

앱이 처음 실행될 때 Compose 화면과 음악 재생 컨트롤러를 연결하는 시작점이다.

## 쉽게 말하면

Android 앱의 입구다. 화면을 띄우고, 음악 재생 상태를 감시하고, 사용자가 재생 버튼을 눌렀을 때 실제 재생 로직으로 넘겨준다.

## 주요 기능

- `LocalPlaybackController`를 생성한다.
- Activity lifecycle에 playback controller를 등록한다.
- `playbackController.playbackState`를 구독한다.
- `AndroidProjetTheme`을 적용한다.
- `HomeScreen`에 샘플 음악 목록과 재생 관련 콜백을 전달한다.

## 동작 흐름

```text
앱 실행
-> MainActivity.onCreate()
-> LocalPlaybackController 생성
-> Compose UI 시작
-> HomeScreen 표시
-> 사용자가 곡 선택
-> playbackController.play(music) 호출
```

## 이 파일이 중요한 이유

UI와 실제 재생 기능이 만나는 지점이다. `HomeScreen`은 화면만 담당하고, `LocalPlaybackController`는 실제 재생 명령을 담당하는 구조로 분리되어 있다.
