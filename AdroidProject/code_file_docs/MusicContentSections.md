# MusicContentSections.kt

원본 파일: `app/src/main/java/com/job/androidprojet/ui/home/MusicContentSections.kt`

## 한 줄 역할

홈, 검색, 보관함 화면의 목록 섹션을 구성한다.

## 쉽게 말하면

화면 안에 들어갈 음악 목록 블록들을 만드는 파일이다. HomeScreen이 큰 화면 틀이라면, 이 파일은 그 안의 섹션 내용이다.

## 주요 함수

- `homeContent()`: 홈 화면의 빠른 접근, 최근 선택, 추천, 전체 목록을 만든다.
- `searchContent()`: 검색 결과 목록을 만든다.
- `libraryContent()`: 보관함 통계, 즐겨찾기, 최근 선택, 앨범 목록을 만든다.

## 동작 예시

```text
홈 화면
-> Jump back in
-> Recent selections
-> Made for focus
-> All local tracks
```

## 이 파일이 중요한 이유

화면별로 어떤 음악 목록을 보여줄지 정하는 파일이다. 검색 결과가 없거나 즐겨찾기가 없을 때의 빈 상태 메시지도 여기에서 처리한다.
