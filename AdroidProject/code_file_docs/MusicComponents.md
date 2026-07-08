# MusicComponents.kt

원본 파일: `app/src/main/java/com/job/androidprojet/ui/home/MusicComponents.kt`

## 한 줄 역할

음악 앱에서 반복해서 쓰는 작은 UI 컴포넌트를 모아둔다.

## 쉽게 말하면

음악 카드, 음악 한 줄 목록, 앨범 이미지 placeholder, 빈 목록 메시지 같은 재사용 부품을 담은 파일이다.

## 주요 컴포넌트

- `SectionTitle`: 섹션 제목
- `QuickAccessGrid`: 빠른 접근 음악 그리드
- `HorizontalMusicSection`: 가로 스크롤 추천 섹션
- `MusicListRow`: 음악 한 줄 목록
- `LibraryStatsRow`: 보관함 통계 카드
- `EmptyMusicList`: 빈 목록 메시지
- `AlbumPlaceholder`: 실제 이미지 대신 보여주는 색상 앨범 박스

## 보조 함수

- `formatDuration()`: 밀리초를 `분:초` 형식으로 변환한다.
- `albumAccentColor()`: 음악 ID에 따라 앨범 박스 색상을 정한다.

## 이 파일이 중요한 이유

같은 UI를 여러 화면에서 반복해서 쓰기 때문에, 이 파일이 화면 코드의 중복을 줄이고 디자인을 통일한다.
