# 개인 발표 노트

이 문서는 발표 준비용 개인 메모입니다. GitHub Pages에는 배포하지 않습니다.

## 배포 대상

- 올바른 저장소: `lunatic280/2026-Summer-Moblie`
- 공개 발표 페이지: `https://lunatic280.github.io/2026-Summer-Moblie/`
- 배포 방식: `gh-pages` 브랜치에 정적 HTML만 배포
- 보조 설정: 대상 저장소 main에도 `docs/`와 `.github/workflows/pages.yml`를 추가함

## 현재 배포 상태 메모

- `gh-pages` 브랜치에는 공개용 정적 페이지가 올라가 있다.
- main 브랜치에는 공개용 `docs/`와 GitHub Actions Pages 워크플로가 올라가 있다.
- URL이 계속 404이면 GitHub 저장소 Settings > Pages에서 Source를 `Deploy from a branch`, Branch를 `gh-pages`, Folder를 `/ (root)`로 설정하면 된다.
- 또는 Source를 `GitHub Actions`로 설정한 뒤 `Deploy presentation page` 워크플로를 다시 실행한다.

## 공개 페이지에서 제외한 내용

- 이전에 잘못 배포했던 저장소와 URL 정보
- 로컬 파일 경로와 구체적인 소스 파일 목록
- 커밋 SHA, 브랜치 작업 상태, 테스트 명령 로그
- 미완성/보강 필요 항목의 세부 리스크
- 발표자가 참고할 구현 내부 구조와 디버깅 메모

## 프로젝트 분석 요약

- 앱 주제: Spotify 스타일 로컬 음악 플레이어
- 기술 스택: Kotlin, Jetpack Compose, Material3, Navigation Compose, ViewModel, StateFlow, Media3 ExoPlayer
- 데이터: 샘플 로컬 음원 12곡과 메타데이터 카탈로그
- 주요 화면: 홈, 검색, 상세 플레이어, 보관함
- 주요 UX: 검색/필터, 미니 플레이어, 상세 재생 화면, 즐겨찾기, 최근 선택 목록
- 재생 구조: UI 상태와 실제 재생 컨트롤러를 분리하고, MediaSessionService 기반 확장을 고려

## 발표 때 말할 핵심 포인트

1. 실제 Spotify API나 로그인 없이 로컬 음원 기반으로 음악 앱 UX를 구현했다.
2. Compose 화면은 사용자 경험을 담당하고, ViewModel은 검색/선택/즐겨찾기 상태를 담당한다.
3. 재생은 화면 내부 로직으로 끝내지 않고, 백그라운드 재생을 고려한 서비스 구조로 확장했다.
4. 발표 시연은 홈 화면 → 검색 → 곡 선택 → 플레이어 → 보관함 순서가 가장 자연스럽다.

## 구현 상태 메모

- 완료: 홈/검색/플레이어/보관함 UI, Navigation, 검색 필터, 즐겨찾기 토글, 최근 선택 목록, 로컬 샘플 음원 카탈로그
- 완료: ViewModel 중심 상태 관리와 단위 테스트
- 진행/보강: Media3 알림 제어, 알림 권한 요청, 실제 기기 장기 재생 검증
- 이후 확장: DataStore 또는 Room 저장, AppWidgetProvider 기반 홈 위젯

## 발표 시연 스크립트

1. "이 앱은 외부 스트리밍이 아니라 로컬 샘플 음원으로 동작하는 음악 플레이어입니다."
2. 홈 화면에서 추천 영역과 전체 목록을 보여준다.
3. 검색 화면에서 `focus` 또는 `night` 같은 키워드를 입력한다.
4. 곡을 선택해서 상세 플레이어로 이동한다.
5. 재생/일시정지, 다음 곡, 이전 곡, 진행률 조작을 보여준다.
6. 보관함에서 즐겨찾기와 최근 선택 흐름을 설명한다.
7. 마무리로 알림 제어, 위젯, 영구 저장소 확장 계획을 말한다.

## 확인 명령

```bash
./gradlew :app:compileDebugKotlin
./gradlew :app:testDebugUnitTest
```

## 주의할 점

- 공개 발표에서는 "아직 안 됨"보다 "다음 단계로 확장"이라는 표현이 좋다.
- 저장소가 비공개일 수 있으므로 공개 페이지에는 GitHub 저장소 링크를 노출하지 않는다.
- 저작권 문제가 있는 음원이나 Spotify 실제 서비스 연동을 사용하지 않았다는 점을 분명히 말한다.
