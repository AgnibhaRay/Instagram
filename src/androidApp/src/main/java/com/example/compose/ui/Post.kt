package com.example.compose.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.compose.R
import com.example.compose.createNewPost
import com.example.compose.entity.User
import com.example.compose.model.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CreateNewPost(navController: NavController) {
    var caption by remember { mutableStateOf("") }
    var image by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        image = uri
    }
    LaunchedEffect(key1 = true) {
        launcher.launch("image/*")
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(17.dp, 10.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.instagram_arrow_left_large),
                    contentDescription = null,
                    Modifier
                        .padding(end = 16.dp)
                        .size(24.dp)
                        .clickable { navController.popBackStack() })
                Text(text = "New Post", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
            }
            Icon(painter = painterResource(R.drawable.instagram_checkmark),
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { image?.let { createNewPost(caption, it) } },
                tint = Color(0xFF0095F6)
            )
        }
        Row(Modifier.padding(horizontal = 10.dp)) {
            Image(
                rememberImagePainter(image), contentDescription = null,
                modifier = Modifier.size(90.dp), contentScale = ContentScale.Inside
            )
            TextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = "Write a caption..."
            )
        }
    }
}

@Composable
fun CurrentPost(
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController,
    uid: String
) {
    val condition = uid == FirebaseAuth.getInstance().uid.toString()
    val data by if (condition) viewModel.currentUserData.observeAsState(initial = User())
    else viewModel.differentUserData.observeAsState(initial = User())
    LaunchedEffect(true) {
        if (!condition) viewModel.getDifferentUserData(uid)
    }
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.instagram_arrow_left_large),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
            Text(
                text = "Posts",
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            data.edge_posts.reversed().forEach {
                Post(ID = it, uid = FirebaseAuth.getInstance().uid.toString(), navController = navController)
            }
        }
    }
}