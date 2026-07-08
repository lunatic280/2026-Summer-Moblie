package com.job.androidprojet.ui.home

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable

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

    companion object {
        val entries: List<HomeDestination> = listOf(
            Home,
            Search,
            Player,
            Library,
        )

        fun fromNavDestination(destination: NavDestination?): HomeDestination? {
            return entries.firstOrNull { homeDestination ->
                destination.hasRoute(homeDestination)
            }
        }
    }
}

private fun NavDestination?.hasRoute(destination: HomeDestination): Boolean {
    if (this == null) return false

    return when (destination) {
        HomeDestination.Home -> hasRoute<HomeDestination.Home>()
        HomeDestination.Search -> hasRoute<HomeDestination.Search>()
        HomeDestination.Player -> hasRoute<HomeDestination.Player>()
        HomeDestination.Library -> hasRoute<HomeDestination.Library>()
    }
}
