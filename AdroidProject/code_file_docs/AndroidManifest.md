# AndroidManifest.xml

원본 파일: `app/src/main/AndroidManifest.xml`

## 한 줄 역할

Android 시스템에 앱 구성 요소와 권한을 알려준다.

## 쉽게 말하면

앱의 등록 서류다. 어떤 Activity가 시작 화면인지, 어떤 Service가 있는지, 어떤 권한이 필요한지 Android에게 알려준다.

## 주요 내용

- 포그라운드 서비스 권한 선언
- 미디어 재생용 포그라운드 서비스 권한 선언
- 알림 권한 선언
- `MainActivity`를 런처 Activity로 등록
- `MusicPlaybackService`를 Media3 `MediaSessionService`로 등록

## 이 파일이 중요한 이유

서비스를 Manifest에 등록하지 않으면 `MediaController`가 `MusicPlaybackService`에 연결할 수 없다.
