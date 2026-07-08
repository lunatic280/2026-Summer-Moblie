# LocalPlaybackController.kt

원본 파일: `app/src/main/java/com/job/androidprojet/playback/LocalPlaybackController.kt`

## 한 줄 역할

Compose UI와 Media3 재생 서비스 사이에서 재생 명령과 재생 상태를 중계한다.

## 쉽게 말하면

화면에서 "재생", "일시정지", "다음 곡" 같은 요청이 들어오면 실제 플레이어에게 전달하고, 플레이어의 현재 상태를 다시 화면에 알려주는 컨트롤러다.

## 주요 기능

- `MusicPlaybackService`에 연결할 `MediaController`를 만든다.
- 곡을 선택하면 `Music`을 `MediaItem`으로 변환한다.
- 전체 샘플 곡을 Media3 재생 목록으로 설정한다.
- 재생, 일시정지, 탐색 기능을 제공한다.
- 500ms마다 현재 재생 위치를 업데이트한다.
- 실제 플레이어 상태를 `LocalPlaybackState`로 노출한다.

## 주요 함수

- `play(music)`: 선택한 곡을 재생한다.
- `setPlaying(music, shouldPlay)`: 재생 또는 일시정지를 처리한다.
- `seekToProgress(progress)`: 슬라이더 위치에 맞춰 재생 위치를 이동한다.
- `release()`: controller와 progress job을 정리한다.
- `emitPlaybackState()`: 현재 player 상태를 StateFlow에 반영한다.

## 동작 흐름

```text
사용자가 곡 선택
-> play(music)
-> Music을 MediaItem으로 변환
-> 전체 샘플 곡을 재생 목록으로 설정
-> 선택한 곡 index부터 재생
-> playbackState 갱신
-> MainActivity와 HomeScreen이 UI를 갱신
```

## 주의할 점

이 파일은 현재 로컬에서 수정된 상태다. 전체 재생 목록 설정과 MediaItem 전환 시 현재 곡 동기화 기능이 추가되어 있다.
