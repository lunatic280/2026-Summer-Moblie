# HomeDestination.kt

원본 파일: `app/src/main/java/com/job/androidprojet/ui/home/HomeDestination.kt`

## 한 줄 역할

앱에서 이동할 수 있는 화면 목록을 정의한다.

## 쉽게 말하면

네비게이션 메뉴판이다. Home, Search, Player, Library 화면의 route와 label을 한곳에 모아둔다.

## 주요 값

- `Home`: 홈 화면
- `Search`: 검색 화면
- `Player`: 상세 플레이어 화면
- `Library`: 보관함 화면

## 주요 함수

- `fromName(name)`: 문자열로 목적지를 찾는다.
- `fromRoute(route)`: Navigation route로 목적지를 찾는다.

## 이 파일이 중요한 이유

화면 이름을 여러 곳에 직접 쓰면 실수하기 쉽다. enum으로 관리하면 하단 탭과 NavHost가 같은 목적지 정의를 공유할 수 있다.
