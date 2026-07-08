# build.gradle.kts

원본 파일: `build.gradle.kts`

## 한 줄 역할

프로젝트 전체에서 사용할 Gradle 플러그인을 선언한다.

## 쉽게 말하면

루트 빌드 설정 파일이다. 실제 앱 설정은 `app/build.gradle.kts`에 있고, 이 파일은 공통 플러그인을 준비해둔다.

## 주요 내용

- Android application plugin 선언
- Kotlin Compose plugin 선언
- `apply false`로 실제 적용은 각 모듈에서 하도록 설정

## 이 파일이 중요한 이유

여러 모듈이 있는 프로젝트로 커져도 같은 플러그인 버전을 중앙에서 관리할 수 있다.
