# July6Application

Android Kotlin으로 만든 스톱워치 예제 앱입니다.

## 주요 기능

- `start` 버튼을 누르면 스톱워치가 시작됩니다.
- `stop` 버튼을 누르면 현재 시간이 멈추고, 다시 시작하면 이어서 측정됩니다.
- `reset` 버튼을 누르면 측정 시간이 초기화됩니다.
- 시작, 정지, 초기화 시 `Toast` 알림을 표시합니다.
- 시작 전에는 `stop` 버튼을 누를 수 없도록 비활성화합니다.
- 뒤로가기 버튼을 한 번 누르면 종료 안내 Toast를 표시하고, 3초 안에 한 번 더 누르면 앱이 종료됩니다.
- 화면 회전 같은 Activity 재생성 상황에서도 스톱워치 상태를 유지합니다.

## 화면 구성

- 세로 화면 레이아웃: `app/src/main/res/layout/activity_main.xml`
- 가로 화면 레이아웃: `app/src/main/res/layout-land/activity_main.xml`

가로 화면에서는 Android 리소스 규칙에 따라 `layout-land/activity_main.xml`이 자동으로 적용됩니다.
왼쪽에는 측정 시간과 스톱워치가 표시되고, 오른쪽에는 `start`, `reset`, `stop` 버튼이 세로로 배치됩니다.

## 수정된 주요 파일

- `app/src/main/java/com/job/july6application/MainActivity.kt`
  - `SystemClock.elapsedRealtime()` 기반으로 스톱워치 시간 계산
  - 시작/정지/초기화 버튼 동작 구현
  - Toast 알림 추가
  - 시작 전 `stop` 버튼 비활성화 처리
  - 뒤로가기 두 번 입력 시 종료 기능 추가
  - `onSaveInstanceState()`로 상태 저장

- `app/src/main/res/layout-land/activity_main.xml`
  - 가로 화면 전용 레이아웃 추가
  - 세로 화면과 같은 View ID를 유지해 ViewBinding과 호환되도록 구성

## 빌드 확인

다음 명령으로 Kotlin 컴파일을 확인했습니다.

```bash
./gradlew :app:compileDebugKotlin
```

빌드 결과는 성공입니다.
