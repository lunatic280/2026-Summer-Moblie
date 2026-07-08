# SampleMusicCatalog.kt

원본 파일: `app/src/main/java/com/job/androidprojet/data/SampleMusicCatalog.kt`

## 한 줄 역할

앱에서 보여줄 샘플 음악 목록을 정의한다.

## 쉽게 말하면

임시 데이터베이스처럼 동작하는 파일이다. 서버나 Room DB 없이 앱 안에서 사용할 음악 정보를 직접 적어둔다.

## 주요 기능

- `songs` 리스트에 12개의 샘플 음악을 저장한다.
- 각 음악은 제목, 가수, 앨범, 파일 이름, 길이, 즐겨찾기 여부를 가진다.
- `fileName`은 `res/raw` 안의 실제 WAV 파일 이름과 연결된다.

## 현재 포함된 곡 예시

- Night Drive
- Study Beats
- Rainy Day
- Morning Coffee
- City Pop
- Deep Focus

## 이 파일이 중요한 이유

현재 앱은 외부 API나 실제 음악 DB를 쓰지 않는다. 그래서 이 파일이 홈 화면, 검색 화면, 플레이어 화면의 데이터 출처가 된다.
