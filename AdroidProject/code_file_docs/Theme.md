# Theme.kt

원본 파일: `app/src/main/java/com/job/androidprojet/ui/theme/Theme.kt`

## 한 줄 역할

앱 전체에 Material3 테마를 적용한다.

## 쉽게 말하면

Compose UI의 기본 색상, 글자 스타일을 감싸주는 테마 함수다. `MainActivity`에서 `AndroidProjetTheme`으로 화면 전체를 감싼다.

## 주요 기능

- 다크 테마와 라이트 테마를 고른다.
- Android 12 이상에서는 dynamic color를 사용할 수 있게 한다.
- `MaterialTheme`에 colorScheme과 typography를 전달한다.

## 현재 사용 방식

`MainActivity`에서는 다음처럼 사용한다.

```text
AndroidProjetTheme(
    darkTheme = true,
    dynamicColor = false
)
```

즉 현재 앱은 강제로 다크 테마를 쓰고, 기기 동적 색상은 사용하지 않는다.
