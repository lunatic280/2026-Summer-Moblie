# Type-safe Navigation 정리

이 앱은 Jetpack Compose의 Navigation Compose를 사용해서 `Home`, `Search`, `Player`, `Library` 화면을 전환한다.
기존에는 `"home"`, `"search"` 같은 문자열 route를 사용했지만, 지금은 `HomeDestination.Home` 같은 타입 객체를 route로 사용하는 Type-safe Navigation 방식이다.

## 핵심 개념

문자열 route 방식은 다음처럼 동작한다.

```kotlin
navController.navigate("player")
composable("player") { ... }
```

Type-safe Navigation 방식은 다음처럼 동작한다.

```kotlin
navController.navigate(HomeDestination.Player)
composable<HomeDestination.Player> { ... }
```

이 방식의 장점은 route 오타를 줄이고, 화면 이동 대상을 Kotlin 타입으로 관리할 수 있다는 점이다.

## 1. 화면 Route 정의

파일: `app/src/main/java/com/job/androidprojet/ui/home/HomeDestination.kt`

```kotlin
internal sealed interface HomeDestination {
    val label: String
    val indicator: String

    @Serializable
    data object Home : HomeDestination {
        override val label = "Home"
        override val indicator = "H"
    }

    @Serializable
    data object Search : HomeDestination {
        override val label = "Search"
        override val indicator = "S"
    }

    @Serializable
    data object Player : HomeDestination {
        override val label = "Player"
        override val indicator = "P"
    }

    @Serializable
    data object Library : HomeDestination {
        override val label = "Library"
        override val indicator = "L"
    }
}
```

각 화면은 `@Serializable data object`로 정의되어 있다.
Navigation Compose는 이 객체를 route로 직렬화해서 내부 이동 경로로 사용한다.

## 2. NavController 생성

파일: `app/src/main/java/com/job/androidprojet/ui/home/HomeScreen.kt`

```kotlin
val navController = rememberNavController()
```

`navController`는 화면 이동을 실행하는 객체다.
하단바 클릭, 노래 클릭, 미니 플레이어 클릭 같은 이벤트가 발생하면 이 객체를 통해 다른 화면으로 이동한다.

## 3. NavHost에 화면 등록

```kotlin
NavHost(
    navController = navController,
    startDestination = initialDestination,
) {
    composable<HomeDestination.Home> {
        // 홈 화면
    }

    composable<HomeDestination.Search> {
        // 검색 화면
    }

    composable<HomeDestination.Player> {
        // 플레이어 화면
    }

    composable<HomeDestination.Library> {
        // 보관함 화면
    }
}
```

`NavHost`는 어떤 route가 어떤 화면을 보여줄지 등록하는 곳이다.
여기서 중요한 부분은 `composable("home")`이 아니라 `composable<HomeDestination.Home>` 형태로 등록한다는 점이다.

## 4. 화면 이동 함수

```kotlin
fun navigateTo(destination: HomeDestination) {
    when (destination) {
        HomeDestination.Home -> navController.navigate(HomeDestination.Home) {
            configureTopLevelNavigation()
        }

        HomeDestination.Search -> navController.navigate(HomeDestination.Search) {
            configureTopLevelNavigation()
        }

        HomeDestination.Player -> navController.navigate(HomeDestination.Player) {
            configureTopLevelNavigation()
        }

        HomeDestination.Library -> navController.navigate(HomeDestination.Library) {
            configureTopLevelNavigation()
        }
    }
}
```

이 함수는 `HomeDestination` 타입을 받아서 해당 화면으로 이동한다.
예를 들어 플레이어 화면으로 이동하려면 다음처럼 호출한다.

```kotlin
navigateTo(HomeDestination.Player)
```

## 5. 하단바 클릭 흐름

파일: `app/src/main/java/com/job/androidprojet/ui/home/HomeBottomBar.kt`

```kotlin
HomeDestination.entries.forEach { destination ->
    NavigationBarItem(
        selected = selectedDestination == destination,
        onClick = { onDestinationSelected(destination) },
        label = {
            Text(text = destination.label)
        },
    )
}
```

하단바는 `HomeDestination.entries`를 순회하면서 탭을 만든다.
사용자가 탭을 누르면 `onDestinationSelected(destination)`이 호출된다.

`HomeScreen`에서는 이 콜백을 `navigateTo`와 연결한다.

```kotlin
HomeBottomBar(
    selectedDestination = selectedDestination,
    onDestinationSelected = ::navigateTo,
)
```

전체 흐름은 다음과 같다.

```text
하단바 Search 클릭
-> onDestinationSelected(HomeDestination.Search)
-> navigateTo(HomeDestination.Search)
-> navController.navigate(HomeDestination.Search)
-> composable<HomeDestination.Search> 화면 표시
```

## 6. 노래 클릭 시 Player 화면 이동

노래를 클릭하면 현재 곡을 ViewModel에 저장한 뒤 Player 화면으로 이동한다.

```kotlin
fun selectMusic(music: Music) {
    playerViewModel.selectMusic(music)
    navigateTo(HomeDestination.Player)
    onMusicClick?.invoke(music)
}
```

동작 흐름은 다음과 같다.

```text
노래 클릭
-> playerViewModel.selectMusic(music)
-> navigateTo(HomeDestination.Player)
-> PlayerDetailScreen 표시
-> playbackController.play(music) 호출
```

## 7. 현재 선택된 탭 확인

현재 화면이 어떤 탭인지 확인할 때도 문자열 route를 비교하지 않는다.

```kotlin
val navBackStackEntry by navController.currentBackStackEntryAsState()
val selectedDestination = HomeDestination.fromNavDestination(navBackStackEntry?.destination)
    ?: initialDestination
```

`HomeDestination.fromNavDestination()` 내부에서는 `hasRoute<T>()`로 현재 destination 타입을 확인한다.

```kotlin
private fun NavDestination?.hasRoute(destination: HomeDestination): Boolean {
    if (this == null) return false

    return when (destination) {
        HomeDestination.Home -> hasRoute<HomeDestination.Home>()
        HomeDestination.Search -> hasRoute<HomeDestination.Search>()
        HomeDestination.Player -> hasRoute<HomeDestination.Player>()
        HomeDestination.Library -> hasRoute<HomeDestination.Library>()
    }
}
```

즉 현재 route 문자열이 `"search"`인지 비교하는 것이 아니라, 현재 destination이 `HomeDestination.Search` 타입인지 확인한다.

## 8. Top-level Navigation 옵션

하단 탭 이동에는 다음 옵션을 사용한다.

```kotlin
fun NavOptionsBuilder.configureTopLevelNavigation() {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
```

각 옵션의 의미는 다음과 같다.

- `popUpTo`: 시작 화면까지 back stack을 정리한다.
- `saveState = true`: 이전 탭의 상태를 저장한다.
- `launchSingleTop = true`: 같은 화면을 여러 번 중복으로 쌓지 않는다.
- `restoreState = true`: 다시 돌아온 탭의 상태를 복원한다.

하단 탭 앱에서 흔히 쓰는 설정이다.

## 전체 구조 요약

```text
HomeDestination.kt
-> 화면 route를 @Serializable 타입 객체로 정의

HomeScreen.kt
-> rememberNavController() 생성
-> NavHost에 composable<HomeDestination.X> 등록
-> navigateTo(HomeDestination.X)로 화면 이동
-> hasRoute<HomeDestination.X>()로 현재 탭 확인

HomeBottomBar.kt
-> HomeDestination.entries로 하단 탭 생성
-> 탭 클릭 시 navigateTo 호출
```

## 한 줄 요약

이 앱의 네비게이션은 문자열 route가 아니라 `HomeDestination.Home`, `HomeDestination.Search`, `HomeDestination.Player`, `HomeDestination.Library` 같은 타입 객체를 기준으로 화면을 등록하고 이동하는 구조다.
