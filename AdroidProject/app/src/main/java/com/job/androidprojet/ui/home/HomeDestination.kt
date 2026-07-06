package com.job.androidprojet.ui.home

internal enum class HomeDestination(
    val route: String,
    val label: String,
    val indicator: String,
) {
    Home("home", "Home", "H"),
    Search("search", "Search", "S"),
    Player("player", "Player", "P"),
    Library("library", "Library", "L");

    companion object {
        fun fromName(name: String): HomeDestination {
            return entries.firstOrNull { destination ->
                destination.name.equals(name, ignoreCase = true) ||
                    destination.route.equals(name, ignoreCase = true) ||
                    destination.label.equals(name, ignoreCase = true)
            } ?: Home
        }

        fun fromRoute(route: String?): HomeDestination? {
            return entries.firstOrNull { destination -> destination.route == route }
        }
    }
}
