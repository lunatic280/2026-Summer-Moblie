# ExampleInstrumentedTest.kt

원본 파일: `app/src/androidTest/java/com/job/androidprojet/ExampleInstrumentedTest.kt`

## 한 줄 역할

Android 기기나 에뮬레이터에서 실행되는 테스트 예시 파일이다.

## 쉽게 말하면

로컬 JVM 테스트가 아니라 실제 Android 환경에서 앱 context를 가져와 확인하는 테스트다.

## 주요 테스트

- `useAppContext()`: 앱 package name이 `com.job.androidprojet`인지 확인한다.

## 이 파일이 중요한 이유

서비스, Activity, 실제 Android framework와 관련된 테스트는 이런 instrumented test 구조에서 확장할 수 있다.
