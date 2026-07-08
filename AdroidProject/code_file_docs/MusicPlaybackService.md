# MusicPlaybackService.kt

원본 파일: `app/src/main/java/com/job/androidprojet/playback/MusicPlaybackService.kt`

## 한 줄 역할

앱 밖에서도 재생을 유지할 수 있게 Media3 `MediaSessionService`를 제공한다.

## 쉽게 말하면

실제 음악을 재생하는 서비스다. 화면은 꺼지거나 바뀔 수 있지만, 재생 서비스는 ExoPlayer를 들고 음악을 계속 재생할 수 있게 설계되어 있다.

## 주요 기능

- `ExoPlayer`를 생성한다.
- `MediaSession`을 생성한다.
- 외부 `MediaController`가 접속하면 현재 session을 반환한다.
- 서비스 종료 시 player와 session을 해제한다.
- Media3 기본 알림 provider를 설정한다.
- 알림을 눌렀을 때 `MainActivity`로 돌아오는 `PendingIntent`를 만든다.

## 주요 함수

- `onCreate()`: player, notification provider, media session을 준비한다.
- `onGetSession()`: 컨트롤러 요청에 대해 media session을 반환한다.
- `onDestroy()`: player와 session을 정리한다.
- `createSessionActivityPendingIntent()`: 알림에서 앱으로 돌아오는 Intent를 만든다.

## 주의할 점

이 파일도 현재 로컬에서 수정된 상태다. 알림 채널 이름과 기본 Media3 알림 설정이 추가되어 있다.
