# PlayerDetailScreen.kt

원본 파일: `app/src/main/java/com/job/androidprojet/ui/home/PlayerDetailScreen.kt`

## 한 줄 역할

현재 재생 중인 곡을 자세히 보여주는 플레이어 화면을 만든다.

## 쉽게 말하면

노래를 눌렀을 때 크게 보이는 상세 재생 화면이다. 곡 정보, 진행률, 재생 버튼, 즐겨찾기, 다음 곡 목록을 보여준다.

## 주요 컴포넌트

- `PlayerDetailScreen`: 상세 플레이어 전체 화면
- `PlayerHeader`: 현재 앨범 정보
- `PlayerAlbumArt`: 앨범 placeholder
- `PlayerMetadata`: 곡 제목, 가수, 즐겨찾기 버튼
- `PlayerTimeline`: 진행률 슬라이더와 시간 표시
- `PlayerControls`: 이전/재생/다음 버튼
- `QueuePreview`: 다음 곡 미리보기

## 동작 흐름

```text
현재 곡 있음
-> 곡 정보 표시
-> Slider로 위치 변경
-> 이전/재생/다음 버튼으로 제어
-> 즐겨찾기 버튼으로 상태 변경
```

## 이 파일이 중요한 이유

음악 플레이어 앱의 핵심 화면이다. 사용자가 재생 상태를 가장 자세히 보고 직접 조작하는 곳이다.
