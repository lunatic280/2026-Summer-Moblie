# MusicRouteContent.kt

원본 파일: `app/src/main/java/com/job/androidprojet/ui/home/MusicRouteContent.kt`

## 한 줄 역할

홈, 검색, 보관함 화면이 공통으로 쓰는 상단 영역을 만든다.

## 쉽게 말하면

검색창, 필터 버튼, 결과 개수 표시처럼 여러 화면에서 반복되는 UI를 한곳에 모아둔 파일이다.

## 주요 구성

- `HomeHeader`: 앱 제목과 전체 샘플 트랙 수 표시
- `LocalSearchField`: 검색 입력창
- `ContentFilterRow`: Music, Favorites, Albums 필터 칩
- `SearchSummary`: 현재 검색 결과 개수 표시

## 동작 흐름

```text
사용자 검색어 입력
-> onQueryChange 호출
-> ViewModel 검색어 갱신
-> 결과 개수 표시 변경
```

## 이 파일이 중요한 이유

검색과 필터 UI는 여러 화면에서 공통으로 필요하다. 이 파일로 분리해서 중복을 줄였다.
