# rules.keep

원본 파일: `app/src/main/keepRules/rules.keep`

## 한 줄 역할

R8 코드 축소/난독화 과정에서 보존할 코드를 지정하는 규칙 파일이다.

## 쉽게 말하면

릴리즈 빌드에서 사용하지 않는 코드가 제거되거나 이름이 바뀔 때, 반드시 남겨야 하는 코드를 적는 파일이다.

## 현재 상태

현재는 템플릿 안내 주석만 있고 실제 keep 규칙은 없다.

## 이 파일이 중요한 이유

나중에 reflection, WebView JavaScript interface, 외부 라이브러리 요구사항 등이 생기면 이 파일에 보존 규칙을 추가해야 한다.
