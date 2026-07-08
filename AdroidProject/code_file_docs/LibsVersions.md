# libs.versions.toml

원본 파일: `gradle/libs.versions.toml`

## 한 줄 역할

라이브러리와 플러그인 버전을 한곳에서 관리한다.

## 쉽게 말하면

의존성 버전표다. `app/build.gradle.kts`에서는 긴 라이브러리 이름 대신 `libs.androidx.media3.exoplayer` 같은 별칭을 사용한다.

## 주요 버전

- Android Gradle Plugin
- Kotlin
- Compose BOM
- Navigation Compose
- Lifecycle
- Media3
- JUnit / Espresso

## 이 파일이 중요한 이유

버전을 여러 파일에 흩어놓지 않고 한곳에서 관리할 수 있다. 라이브러리 업데이트도 이 파일 중심으로 하면 된다.
