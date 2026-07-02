# JulySecondApplication 오늘 작업 요약

날짜: 2026-07-02

## frameexam
- `FrameLayout`을 사용한 이미지 전환 예제.
- 강아지/고양이 버튼을 누르면 해당 이미지가 보이도록 구성.
- `ViewBinding`으로 버튼 클릭과 뷰 표시 상태를 제어.

## frompad
- 전화기 숫자패드 UI로 수정.
- 숫자 버튼은 `1~9`, `*`, `0`, `#` 형태로 배치.
- 마지막 줄은 아이콘 버튼으로 구성:
  - 영상통화
  - 다이얼링
  - 삭제
- `GridLayout`에서 `TableLayout` 기반으로 다시 정렬해 행과 열을 맞춤.
- 숫자 버튼은 원형 키패드 스타일, 하단 버튼은 아이콘 버튼 스타일로 분리.

## gravitiyexam
- `LinearLayout`과 `gravity` 속성 동작을 확인하는 예제.
- 화면 중앙 정렬과 상단/하단 배치 같은 기본 배치 방식 학습용.

## relativeexam
- `RelativeLayout`으로 뷰의 상대 위치를 배치하는 예제.
- 중앙 사각형을 기준으로 상/하/좌/우에 뷰를 배치하는 구조.

## 정리
- 이번 작업은 레이아웃 배치 방식(`FrameLayout`, `RelativeLayout`, `TableLayout`)을 확인하고,
- `frompad` 화면을 실제 전화기 숫자패드처럼 보이도록 다듬는 데 집중했다.
