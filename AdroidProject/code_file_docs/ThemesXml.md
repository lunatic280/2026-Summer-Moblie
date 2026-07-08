# themes.xml

원본 파일: `app/src/main/res/values/themes.xml`

## 한 줄 역할

Android 시스템이 Activity에 적용할 기본 XML 테마를 정의한다.

## 쉽게 말하면

Compose 화면이 뜨기 전 Android Activity 자체에 적용되는 기본 테마 설정이다.

## 현재 설정

```text
Theme.AndroidProjet
-> android:Theme.Material.Light.NoActionBar
```

즉, 기본 ActionBar가 없는 Material Light 계열 테마를 사용한다.

## 이 파일이 중요한 이유

`AndroidManifest.xml`에서 `MainActivity`와 application theme으로 `Theme.AndroidProjet`를 사용한다. Compose 안쪽 테마는 `Theme.kt`에서 따로 적용한다.
