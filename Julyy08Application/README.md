# Julyy08Application

2026년 7월 8일 Android 실습 내용을 정리한 프로젝트입니다.

## 오늘 작업 요약

### app 모듈

- `activity_main.xml`에 4개의 버튼을 배치했습니다.
- `button1`은 날짜 선택(DatePicker), `button2`는 시간 선택(TimePicker), `button3`은 일반 Dialog, `button4`는 알림창(AlertDialog)을 실행하도록 구현했습니다.
- 날짜와 시간 선택 결과는 화면의 TextView에 표시됩니다.

### calc 모듈

- `activity_main.xml`을 사칙연산 계산기 화면으로 변경했습니다.
- 두 숫자를 입력한 뒤 더하기, 빼기, 곱하기, 나누기를 수행할 수 있습니다.
- 초기화 버튼을 추가했습니다.
- 숫자 미입력과 0으로 나누기 상황을 처리하도록 `MainActivity.kt`에 계산 로직을 구현했습니다.

### calcasd 모듈

- `activity_main.xml`에 버튼 2개를 좌우로 배치했습니다.
- 왼쪽 버튼은 날짜 선택 기능을 실행합니다.
- 오른쪽 버튼은 시간 선택 기능을 실행합니다.
- 선택한 날짜 또는 시간은 결과 TextView에 표시됩니다.

## 주요 변경 파일

- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/java/com/job/julyy08application/MainActivity.kt`
- `calc/src/main/res/layout/activity_main.xml`
- `calc/src/main/java/com/job/calc/MainActivity.kt`
- `calcasd/src/main/res/layout/activity_main.xml`
- `calcasd/src/main/java/com/job/calcasd/MainActivity.kt`

## 검증

다음 명령으로 각 모듈의 Kotlin 컴파일을 확인했습니다.

```bash
./gradlew :app:compileDebugKotlin
./gradlew :calc:compileDebugKotlin
./gradlew :calcasd:compileDebugKotlin
```
