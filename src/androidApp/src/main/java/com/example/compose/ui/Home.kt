package com.example.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose.R
import com.example.compose.decodePostString
import com.example.compose.model.MainViewModel

@Composable
fun Home(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())){
            Spacer(modifier = Modifier.size(56.dp))
            PostList(navController = navController)
        }
        Column {
            Header(navController)
            Divider()
        }
    }
}

@Composable
fun PostList(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), navController: NavController) {
    val stackOfPost by viewModel.stackOfPost.observeAsState(initial = listOf())
    stackOfPost.forEach {
        val decode = decodePostString(it)
        Post(ID = decode.ID, uid = decode.uid, navController = navController)
    }
}

@Composable
fun Header(navController: NavController) {
    val icon = Modifier
        .padding(start = 26.dp)
        .size(24.dp)
    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(R.drawable.instagram_logo), contentDescription = null,
            modifier = Modifier.width(122.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.instagram_add), contentDescription = null,
                modifier = icon.clickable { navController.navigate(com.example.compose.CreateNewPost) }
            )
            Icon(
                painter = painterResource(R.drawable.instagram_heart_outline),
                contentDescription = null,
                modifier = icon
            )
            Icon(
                painter = painterResource(R.drawable.instagram_dm), contentDescription = null,
                modifier = icon
            )
        }
    }
}