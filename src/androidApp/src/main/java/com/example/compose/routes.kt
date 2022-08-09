package com.example.compose

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import com.example.compose.entity.User
import com.example.compose.model.MainViewModel
import com.example.compose.View.*
import com.example.compose.login.create
import com.example.compose.login.default
import com.example.compose.login.forgot
import com.example.compose.login.login
import com.example.compose.ui.*
import com.google.firebase.auth.FirebaseAuth

const val Profile: String = "Profile"
const val CreateNewPost: String = "CreateNewPost"
const val app: String = "route"
const val CurrentPostView: String = "CurrentPost"

@SuppressLint("ComposableNaming")
@Composable
fun routes() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = when {
        currentUser == null -> "default"
        currentUser.isEmailVerified -> app
        else -> "default"
    }
    NavHost(navController = navController, startDestination = startDestination){
        composable("default") { default(navController) }
        composable("login") { login(navController) }
        composable("forgot") { forgot() }
        composable("create") { create(navController = navController) }
        composable(app){ route(navController) }
        composable("$CurrentPostView/{uid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            CurrentPost(
                uid = backStackEntry.arguments?.getString("uid").toString(),
                navController = navController
            )
        }
        composable(Profile){ Profile(navController) }
        composable(CreateNewPost){ CreateNewPost(navController) }
        composable("$Profile/{uid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            Profile(
                uid = backStackEntry.arguments?.getString("uid").toString(),
                navController = navController
            )
        }
        composable("comment/{uid}/{ID}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType },
                navArgument("ID") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            CommentView(
                uid = backStackEntry.arguments?.getString("uid").toString(),
                ID = backStackEntry.arguments?.getString("ID").toString(),
                navController = navController
            )
        }
    }
}

private enum class View(
    val icon: Int,
    val selectedIcon: Int,
) {
    Home(R.drawable.instagram_filled_home, R.drawable.instagram_filled_home),
    Explore(R.drawable.instagram_search_outline, R.drawable.instagram_search_outline),
    Reels(R.drawable.instagram_reels_outline, R.drawable.instagram_reels_outline),
    News(R.drawable.instagram_shopping_bag_outline, R.drawable.instagram_shopping_bag_outline),
    Profile(R.drawable.instagram_user_circle_outline, R.drawable.instagram_user_circle_outline)
}

@SuppressLint("ComposableNaming")
@Composable
fun route(navController: NavController) {
    val sectionState = rememberSaveable { mutableStateOf(Home) }
    val navItems = listOf(Home, Explore, Reels, News, View.Profile)
    if (sectionState.value != Home) {
        BackHandler(true) {
            sectionState.value = Home
        }
    }
    Scaffold(
        bottomBar = {
            BottomBar(
                items = navItems,
                currentSection = sectionState.value,
                onSectionSelected = { sectionState.value = it }
            )
        }) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        Crossfade(
            modifier = modifier,
            targetState = sectionState.value)
        { Screens ->
            when (Screens) {
                Home -> Home(navController)
                Explore -> Text(text = "Explore")
                Reels -> Text(text = "Reels")
                News -> Text(text = "Activity")
                View.Profile -> Profile(navController = navController)
            }
        }
    }
}

@Composable
private fun BottomBar(
    items: List<View>,
    currentSection: View,
    onSectionSelected: (View) -> Unit,
) {
    CompositionLocalProvider(LocalRippleTheme provides Undefined) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.background,
            contentColor = contentColorFor(MaterialTheme.colors.background),
            elevation = 0.dp,
            modifier = Modifier
                .height(48.dp)
                .drawWithContent {
                    drawContent()
                    clipRect {
                        val strokeWidth = Stroke.DefaultMiter
                        drawLine(
                            brush = SolidColor(Color.Gray.copy(alpha = 0.13f)),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Square,
                            start = Offset.Zero.copy(y = 0f),
                            end = Offset(x = size.width, y = 0f)
                        )
                    }
                }
        ) {
            items.forEach { View ->
                val selected = View == currentSection
                val iconRes = if (selected) View.selectedIcon else View.icon
                BottomNavigationItem(
                    icon = {
                        if (View == com.example.compose.View.Profile) {
                            ProfileImage(selected)
                        } else {
                            Icon(
                                painterResource(id = iconRes),
                                modifier = Modifier.size(22.dp),
                                contentDescription = null
                            )
                        }

                    },
                    selected = selected,
                    onClick = { onSectionSelected(View) },
                    alwaysShowLabel = false,
                    selectedContentColor = LocalContentColor.current,
                    unselectedContentColor = LocalContentColor.current
                )
            }
        }
    }
}

@Composable
private fun ProfileImage(
    isSelected: Boolean,
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val data by viewModel.currentUserData.observeAsState(initial = User())
    Box(modifier = if (isSelected) {
        Modifier.border(
            color = Color.LightGray,
            width = 1.dp,
            shape = CircleShape
        )
    } else Modifier
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .padding(if (isSelected) 3.dp else 0.dp)
                .background(color = Color.LightGray, shape = CircleShape)
                .clip(CircleShape), contentAlignment = Alignment.Center
        ) {
            Image(rememberImagePainter(data.image), contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape), contentScale = ContentScale.FillWidth)
        }
    }
}

private object Undefined : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified
    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f, 0.0f, 0.0f, 0.0f)
}