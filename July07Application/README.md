# July07Application

Android 화면 전환 학습용 예제 프로젝트입니다.

## 오늘 진행한 내용

- Android 프로젝트 기본 구조를 생성했습니다.
- `MainActivity`, `SecondActivity`, `ThirdActivity`, `FourthActivity` 총 4개 화면을 구성했습니다.
- ViewBinding을 활성화하고 각 Activity에서 바인딩 클래스를 사용하도록 구현했습니다.
- 메인 화면에서 두 번째, 세 번째, 네 번째 화면으로 이동하는 버튼을 추가했습니다.
- 각 서브 화면에서 `finish()`를 통한 뒤로가기와 다른 화면으로 이동하는 버튼을 구현했습니다.
- `AndroidManifest.xml`에 4개 Activity를 등록하고 앱 실행 진입점을 `MainActivity`로 설정했습니다.
- 화면별 XML 레이아웃과 앱 아이콘, 테마, 테스트 기본 파일을 포함했습니다.

## 화면 구성

- `MainActivity`: 화면 전환 학습 시작 화면
- `SecondActivity`: 두 번째 화면, 뒤로가기 및 세 번째/네 번째 화면 이동
- `ThirdActivity`: 세 번째 화면, 뒤로가기 및 네 번째/메인 화면 이동
- `FourthActivity`: 네 번째 화면, 뒤로가기 및 메인/두 번째 화면 이동

## 실행 방법

```bash
./gradlew assembleDebug
```

Android Studio에서 프로젝트를 열고 `app` 구성을 실행해도 됩니다.
