# July09Application 작업 기록

## 작업 날짜

2026년 7월 9일

## 오늘 진행한 내용

### 1. 기본 앱 / Fragment 화면 전환

- `activity_main.xml`의 `TextView`가 앱바 메뉴와 검색 입력 내용에 따라 변경되도록 수정했습니다.
- 본문 화면을 Fragment 구조로 분리했습니다.
- 첫 번째 Fragment와 두 번째 Fragment를 추가하고, 버튼으로 화면을 이동할 수 있게 했습니다.
- 화면 전환을 쉽게 확인할 수 있도록 Fragment별 배경색을 다르게 설정했습니다.
- 앱바 메뉴와 검색 입력이 현재 표시 중인 Fragment의 `TextView`에 반영되도록 정리했습니다.

주요 파일:

- `app/src/main/java/com/job/july09application/MainActivity.kt`
- `app/src/main/java/com/job/july09application/MainFragment.kt`
- `app/src/main/java/com/job/july09application/SecondFragment.kt`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout/fragment_main.xml`
- `app/src/main/res/layout/fragment_second.xml`

### 2. RecyclerView 예제

- `데이터 추가` 버튼이 새 아이템을 추가하도록 구현했습니다.
- `데이터 삭제` 버튼이 마지막 아이템을 삭제하도록 구현했습니다.
- 삭제할 데이터가 없을 때 Toast 메시지를 표시하도록 처리했습니다.
- 새 아이템 추가 후 해당 위치로 자동 스크롤되도록 수정했습니다.
- 아이템 레이아웃 높이를 `wrap_content`로 바꿔 전체 아이템이 스크롤로 정상 표시되게 했습니다.
- `layout-port`에만 있던 아이템 레이아웃을 기본 `layout`에도 추가했습니다.

주요 파일:

- `newrecycleview/src/main/java/com/job/newrecycleview/MainActivity.kt`
- `newrecycleview/src/main/java/com/job/newrecycleview/MyAdapter.kt`
- `newrecycleview/src/main/res/layout/activity_main.xml`
- `newrecycleview/src/main/res/layout/item_main.xml`
- `newrecycleview/src/main/res/layout-port/item_main.xml`

### 3. ViewPager2 예제

- `activity_main.xml`에 `TabLayout`과 `ViewPager2`를 실제로 배치했습니다.
- `ViewPagerAdapter`를 추가해 첫 번째/두 번째 Fragment를 ViewPager에 연결했습니다.
- 탭 선택과 좌우 스와이프로 페이지가 전환되도록 했습니다.
- Fragment 내부 버튼이 Navigation이 아니라 ViewPager 페이지 이동을 호출하도록 수정했습니다.
- `content_main.xml`도 같은 ViewPager 구조로 정리했습니다.

주요 파일:

- `viewpager/src/main/java/com/job/viewpager/MainActivity.kt`
- `viewpager/src/main/java/com/job/viewpager/FirstFragment.kt`
- `viewpager/src/main/java/com/job/viewpager/SecondFragment.kt`
- `viewpager/src/main/res/layout/activity_main.xml`
- `viewpager/src/main/res/layout/content_main.xml`

### 4. DrawerLayout 예제

- `activity_main.xml`의 루트를 `DrawerLayout`으로 변경했습니다.
- 메인 영역과 드로어 영역을 올바른 Drawer 구조로 배치했습니다.
- 메인 화면 텍스트를 누르면 드로어가 열리도록 구현했습니다.
- 드로어 화면 텍스트를 누르면 드로어가 닫히도록 구현했습니다.
- 기존 코드에서 실제 레이아웃에 없는 `toolbar`, `fab`, `nav_host_fragment_content_main` 참조를 제거했습니다.
- 뒤로가기 버튼을 눌렀을 때 드로어가 열려 있으면 먼저 닫히도록 처리했습니다.

주요 파일:

- `mydrawer/src/main/java/com/job/mydrawer/MainActivity.kt`
- `mydrawer/src/main/res/layout/activity_main.xml`

## 검증한 내용

아래 Gradle 컴파일을 실행해 수정한 모듈의 Kotlin 코드와 리소스 바인딩이 정상 생성되는지 확인했습니다.

```bash
./gradlew :app:compileDebugKotlin
./gradlew :newrecycleview:compileDebugKotlin
./gradlew :viewpager:compileDebugKotlin
./gradlew :mydrawer:compileDebugKotlin
```

모든 명령은 `BUILD SUCCESSFUL`로 완료되었습니다.

## 정리

오늘은 Android 기본 UI 예제들을 중심으로 앱바, Fragment 전환, RecyclerView 데이터 추가/삭제, ViewPager2 페이지 전환, DrawerLayout 열기/닫기 기능을 실제로 동작하도록 수정했습니다. 각 예제는 버튼 클릭, 화면 전환, 스크롤, 뒤로가기 처리처럼 실행 중 확인 가능한 동작 위주로 정리했습니다.
