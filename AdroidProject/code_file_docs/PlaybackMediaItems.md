# PlaybackMediaItems.kt

원본 파일: `app/src/main/java/com/job/androidprojet/playback/PlaybackMediaItems.kt`

## 한 줄 역할

`Music` 데이터를 Media3가 재생할 수 있는 `MediaItem`으로 바꾼다.

## 쉽게 말하면

앱의 음악 정보는 그냥 데이터이고, ExoPlayer는 `MediaItem`이라는 형식이 필요하다. 이 파일은 둘 사이를 번역한다.

## 주요 기능

- `Music.toLocalMediaItem(context)` 확장 함수를 제공한다.
- `fileName`에서 확장자를 제거해 raw resource 이름을 만든다.
- Android resource URI를 만든다.
- Media3 `MediaMetadata`에 제목, 가수, 앨범 정보를 넣는다.

## 동작 흐름

```text
Music(fileName = "sample_night_drive.wav")
-> sample_night_drive 리소스 ID 찾기
-> android.resource://패키지명/리소스ID URI 생성
-> MediaItem 생성
```

## 이 파일이 중요한 이유

이 변환이 실패하면 ExoPlayer가 어떤 파일을 재생해야 하는지 알 수 없다. 그래서 로컬 음원 재생의 핵심 연결 파일이다.
