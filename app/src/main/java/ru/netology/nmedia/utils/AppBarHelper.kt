package ru.netology.nmedia.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import ru.netology.nmedia.R

fun navHostController(activity: AppCompatActivity) : NavController{
        return (activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
}

fun AppBarConfiguration(activity: AppCompatActivity): AppBarConfiguration {
    return AppBarConfiguration(navHostController(activity).graph)
}

fun AppCompatActivity.setupActionBarWithNavControllerDefault() {
    setupActionBarWithNavController(navHostController(this), AppBarConfiguration(this))
}