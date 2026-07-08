# Music.kt

원본 파일: `app/src/main/java/com/job/androidprojet/model/Music.kt`

## 한 줄 역할

음악 한 곡의 정보를 담는 데이터 모델이다.

## 쉽게 말하면

앱에서 노래 한 곡을 표현하기 위한 상자다. 제목, 가수, 앨범, 파일 이름 같은 정보를 하나로 묶는다.

## 필드 설명

- `id`: 곡을 구분하는 고유 번호
- `title`: 곡 제목
- `artist`: 가수 이름
- `album`: 앨범 이름
- `albumImage`: 앨범 이미지 이름으로 쓰려고 만든 값
- `fileName`: 실제 raw 음원 파일 이름
- `durationMillis`: 곡 길이, 밀리초 단위
- `isFavorite`: 즐겨찾기 여부

## 이 파일이 중요한 이유

앱의 거의 모든 화면은 `Music` 객체를 기준으로 동작한다. 목록, 검색, 플레이어, 즐겨찾기, 최근 선택 기능이 모두 이 모델을 사용한다.
