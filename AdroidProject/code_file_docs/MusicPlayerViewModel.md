# MusicPlayerViewModel.kt

원본 파일: `app/src/main/java/com/job/androidprojet/ui/home/MusicPlayerViewModel.kt`

## 한 줄 역할

음악 앱 화면의 상태를 관리한다.

## 쉽게 말하면

검색어, 현재 곡, 즐겨찾기, 최근 선택, 진행률 같은 화면 상태를 기억하고 바꿔주는 파일이다.

## 주요 상태

- `musicList`: 전체 음악 목록
- `searchQuery`: 검색어
- `selectedFilter`: 선택된 필터
- `filteredMusic`: 화면에 보여줄 필터링된 목록
- `recentMusic`: 최근 선택한 음악
- `currentMusic`: 현재 선택된 음악
- `isPlaying`: 재생 중인지 여부
- `playbackProgress`: 재생 진행률
- `queuePreview`: 다음 곡 미리보기

## 주요 함수

- `updateSearchQuery()`: 검색어 변경
- `selectFilter()`: 필터 변경
- `selectMusic()`: 현재 곡 선택
- `togglePlay()`: 재생 상태 토글
- `previousTrack()`: 이전 곡 선택
- `nextTrack()`: 다음 곡 선택
- `toggleFavorite()`: 즐겨찾기 변경
- `syncPlaybackState()`: 실제 플레이어 상태를 UI 상태에 반영

## 이 파일이 중요한 이유

Compose 화면은 이 ViewModel의 `uiState`를 보고 다시 그려진다. 즉, 이 파일이 화면에 무엇이 보일지 결정하는 중심이다.
