# HomeBottomBar.kt

원본 파일: `app/src/main/java/com/job/androidprojet/ui/home/HomeBottomBar.kt`

## 한 줄 역할

하단 미니 플레이어와 하단 네비게이션 바를 만든다.

## 쉽게 말하면

앱 아래쪽에 항상 보이는 영역이다. 현재 곡을 보여주고, 이전/재생/다음 버튼과 화면 이동 탭을 제공한다.

## 주요 컴포넌트

- `HomeBottomBar`: 전체 하단 영역
- `MiniPlayerBar`: 현재 재생 중인 곡과 재생 컨트롤
- `MiniControl`: 작은 원형 버튼

## 동작 흐름

```text
현재 곡이 있음
-> MiniPlayerBar 표시
-> 이전/재생/다음 버튼 사용 가능
-> 하단 NavigationBar로 Home/Search/Player/Library 이동
```

## 이 파일이 중요한 이유

사용자는 어떤 화면에 있어도 현재 곡을 확인하고 재생을 제어할 수 있다. 음악 앱다운 사용성을 만드는 핵심 UI다.
