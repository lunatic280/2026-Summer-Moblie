# HomeScreen.kt

원본 파일: `app/src/main/java/com/job/androidprojet/ui/home/HomeScreen.kt`

## 한 줄 역할

홈, 검색, 플레이어, 보관함 화면을 하나로 묶고 화면 이동과 사용자 이벤트를 관리한다.

## 쉽게 말하면

앱의 메인 화면 관리자다. 사용자가 어디 화면에 있는지, 어떤 곡을 눌렀는지, 재생 버튼을 눌렀는지를 처리한다.

## 주요 기능

- `MusicPlayerViewModel`을 만든다.
- `NavHost`로 4개 화면을 전환한다.
- 하단 `HomeBottomBar`를 항상 보여준다.
- 검색어 입력 시 검색 화면으로 이동한다.
- 곡 선택 시 플레이어 화면으로 이동하고 재생을 요청한다.
- 이전/다음 곡을 계산한다.
- 실제 playback 상태를 ViewModel 상태와 동기화한다.

## 화면 목적지

- `HomeDestination.Home`
- `HomeDestination.Search`
- `HomeDestination.Player`
- `HomeDestination.Library`

## 이 파일이 중요한 이유

화면 구성과 재생 이벤트가 만나는 중심 파일이다. UI의 큰 흐름을 이해하려면 이 파일을 먼저 보면 된다.
