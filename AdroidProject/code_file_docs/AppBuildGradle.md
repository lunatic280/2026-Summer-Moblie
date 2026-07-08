# app/build.gradle.kts

원본 파일: `app/build.gradle.kts`

## 한 줄 역할

`:app` Android 모듈의 빌드 설정과 의존성을 정의한다.

## 쉽게 말하면

앱을 어떤 SDK로 빌드할지, 어떤 라이브러리를 쓸지, Compose를 켤지 정하는 파일이다.

## 주요 설정

- applicationId: `com.job.androidprojet`
- minSdk / targetSdk / compileSdk 설정
- Java 11 호환성 설정
- Compose 사용 설정
- 테스트 runner 설정

## 주요 의존성

- Jetpack Compose
- Material3
- Navigation Compose
- Lifecycle + ViewModel
- Media3 ExoPlayer
- Media3 Session
- JUnit / AndroidX Test

## 이 파일이 중요한 이유

Media3, Navigation Compose, Material3 같은 기능은 이 파일에 의존성이 있어야 코드에서 사용할 수 있다.
