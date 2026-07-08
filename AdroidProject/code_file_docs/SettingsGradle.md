# settings.gradle.kts

원본 파일: `settings.gradle.kts`

## 한 줄 역할

Gradle 프로젝트 이름, 모듈, 저장소 설정을 정의한다.

## 쉽게 말하면

이 프로젝트가 어떤 이름이고, 어떤 모듈을 포함하며, 라이브러리는 어디서 받을지 정하는 파일이다.

## 주요 내용

- 프로젝트 이름: `AndroidProjet`
- 포함 모듈: `:app`
- 플러그인 저장소: Google, Maven Central, Gradle Plugin Portal
- 의존성 저장소: Google, Maven Central
- 프로젝트별 임의 저장소 추가를 막는 설정

## 이 파일이 중요한 이유

Gradle은 이 파일을 보고 프로젝트 구조를 이해한다. `include(":app")`가 없으면 app 모듈을 빌드 대상으로 인식하지 못한다.
