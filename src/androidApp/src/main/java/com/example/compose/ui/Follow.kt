package com.example.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.compose.R
import com.example.compose.cacheString
import com.example.compose.entity.User
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Follow(data: User, back:() -> Unit, navController: NavController, staticUid: String) {
    val uid = FirebaseAuth.getInstance().uid.toString()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(Color.White)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.instagram_arrow_left_large),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(24.dp)
                        .clickable { back.invoke() }
                )
                Text(
                    text = cacheString("$uid@username", data.username, context),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        var selectedTabIndex by remember { mutableStateOf(0) }
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White,
        ) {
            listOf(
                "${data.edge_followers.size} Followers",
                "${data.edge_following.size} Following"
            ).forEachIndexed { i, text ->
                Tab(
                    selected = selectedTabIndex == i,
                    onClick = { selectedTabIndex = i },
                    text = {
                        Text(text,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            letterSpacing = 0.sp)
                    }
                )
            }
        }
        if (selectedTabIndex == 0) {
            when{
                data.edge_followers.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "People follow you", fontWeight = FontWeight.SemiBold, fontSize = 19.sp)
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(text = "Once people follow you, they'll appear here.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
                else -> {
                    data.edge_followers.forEach {
                        UserCardRect(uid = it, navController = navController, type = 0, staticUid = staticUid)
                    }
                }
            }
        } else {
            when{
                data.edge_following.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "People you follow", fontWeight = FontWeight.SemiBold, fontSize = 19.sp)
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(text = "Once you follow people, you'll see them here.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
                else -> {
                    data.edge_following.forEach {
                        UserCardRect(uid = it, navController = navController, type = 1, staticUid = staticUid)
                    }
                }
            }
        }
    }
}