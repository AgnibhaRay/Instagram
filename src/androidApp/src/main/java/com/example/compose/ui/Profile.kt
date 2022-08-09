package com.example.compose.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.compose.*
import com.example.compose.R
import com.example.compose.entity.Strings
import com.example.compose.entity.User
import com.example.compose.model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Profile(
    navController: NavController,
    viewModel: MainViewModel = viewModel(),
    uid: String = FirebaseAuth.getInstance().uid.toString()
) {
    val condition = uid == FirebaseAuth.getInstance().uid.toString()
    val data by if (condition) viewModel.currentUserData.observeAsState(initial = User())
    else viewModel.differentUserData.observeAsState(initial = User())
    val listOfUsers by viewModel.listOfUsers.observeAsState(initial = listOf())
    val context = LocalContext.current
    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp
    var edit by remember { mutableStateOf(false) }
    var discover by remember { mutableStateOf(true) }
    var follow by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        if (!condition) viewModel.getDifferentUserData(uid)
    }
    // TODO
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        // TODO
        if (data.displayName != "") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .offset(y = 46.dp)
            ) {
                // TODO: Profile pic and follow, post metrics
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 17.dp, vertical = 17.dp)
                ) {
                    Image(rememberImagePainter(cacheString("$uid/image", data.image, context)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(90.dp)
                            .border(width = 1.dp, color = Color(0xFfC7C7CC), shape = CircleShape)
                            .padding(5.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.FillWidth
                    )
                    // TODO: follow and post metrics
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val strings = listOf("Posts", "Followers", "Following")
                        strings.forEach {
                            Column(
                                modifier = Modifier.clickable {
                                    when (it) {
                                        "Posts" -> { navController.navigate("$CurrentPostView/$uid") }
                                        else -> { follow = !follow }
                                    }
                                },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = when (it) {
                                        "Posts" -> data.edge_posts.size.toString()
                                        "Followers" -> data.edge_followers.size.toString()
                                        "Following" -> data.edge_following.size.toString()
                                        else -> {
                                            "lol"
                                        }
                                    }, fontSize = 17.sp, fontWeight = FontWeight.SemiBold
                                )
                                Text(text = it, fontSize = 14.sp)
                            }
                        }
                    }
                }
                // TODO: DisplayName and biography
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = cacheString("$uid/displayName", data.displayName, context),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = cacheString("$uid/biography", data.biography, context))
                    Spacer(modifier = Modifier.size(8.dp))
                    // TODO: follow, unfollow and edit Button
                    if (!condition){
                        val differentFollower = viewModel.differentUserData.value?.edge_followers
                        if (differentFollower != null) {
                            UserLikeAndFollowView(Type = 1, list = differentFollower)
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Row(Modifier.padding(bottom = 16.dp)) {
                        val isFollower =
                            viewModel.currentUserData.value?.edge_following?.contains(uid)
                        when {
                            condition -> {
                                OutlinedButton(
                                    onClick = { edit = !edit }, text = "Edit Profile",
                                    modifier = Modifier
                                        .weight(0.9f, true)
                                        .padding(end = 4.dp)
                                )
                            }
                            isFollower == true -> {
                                OutlinedButton(
                                    onClick = { unfollow(uid) }, text = "Unfollow",
                                    modifier = Modifier
                                        .weight(0.9f, true)
                                        .padding(end = 4.dp)
                                )
                            }
                            else -> {
                                FilledButton(
                                    onClick = { follow(uid) }, text = "Follow",
                                    modifier = Modifier
                                        .weight(0.9f, true)
                                        .padding(end = 4.dp)
                                )
                            }
                        }
                        OutlinedIconButton(
                            onClick = { discover = !discover },
                            Modifier.size(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.instagram_arrow_down),
                                contentDescription = null,
                                Modifier
                                    .size(22.dp)
                                    .rotate(if (discover) 180F else 0F)
                            )
                        }
                    }
                }
                // TODO: List of latest 16 users
                if (discover) {
                    Text(
                        text = "Discover People",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 17.dp, top = 8.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        // TODO
                        listOfUsers.take(16).forEach {
                            val isFollower =
                                viewModel.currentUserData.value?.edge_following?.contains(it)
                            if (isFollower != null) {
                                if (it != uid) UserCard(
                                    uid = it,
                                    isFollower = isFollower,
                                    navController = navController
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
                // TODO: Post List
                when{
                    condition -> PostSection(data = data, navController = navController, uid = uid, condition = condition)
                    !condition -> {
                        if (viewModel.differentUserData.value?.edge_posts?.size != 0){
                            PostSection(data = data, navController = navController, uid = uid, condition = condition)
                        }
                    }
                }
            }
        } else DefaultView()
        // TODO
        Header(data = data, uid = uid, navController = navController, condition = condition)
    }
    AnimatedVisibility(
        visible = follow,
        enter = slideInHorizontally(initialOffsetX = { width }),
        exit = slideOutHorizontally(targetOffsetX = { width * 3 })
    ) {
        Follow(data = data, back = { follow = !follow }, navController = navController, staticUid = uid)
    }
    AnimatedVisibility(
        visible = edit,
        enter = slideInVertically(initialOffsetY = { height * 3 }),
        exit = slideOutVertically(targetOffsetY = { height * 3 })
    ) {
        edit(onClick = { edit = !edit }, image = data.image)
    }
}

@Composable
fun PostSection(data: User, navController: NavController, uid: String, condition: Boolean) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White
    ) {
        listOf(
            R.drawable.instagram_grid,
            R.drawable.instagram_person_square
        ).forEachIndexed { i, icon ->
            Tab(
                selected = selectedTabIndex == i,
                onClick = { selectedTabIndex = i },
                modifier = Modifier.height(50.dp),
                icon = {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
            )
        }
    }
    //TODO
    when (selectedTabIndex) {
        0 -> Zero(data, navController, uid, condition)
        1 -> One()
    }
}

@Composable
fun One() {
    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp
    Column(
        Modifier
            .size(width.dp, (height - 46 - 48).dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Not Supported")
    }
}

@Composable
fun Zero(data: User, navController: NavController,uid: String, condition: Boolean) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp
    Column(
        Modifier
            .fillMaxWidth()
            .height((if (condition) (height - 46 - 48) else (height - 46)).dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (data.edge_posts.isNotEmpty()) {
            FlexRow(
                crossAxisSpacing = 2.dp,
                mainAxisSpacing = 2.dp
            ) {
                data.edge_posts.forEach { ID ->
                    Image(ID = ID, modifier = Modifier
                        .size((width * 0.33).dp)
                        .clickable { navController.navigate("$CurrentPostView/$uid") }, uid = uid)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Profile", fontWeight = FontWeight.SemiBold, fontSize = 19.sp)
                Spacer(modifier = Modifier.size(20.dp))
                Text(text = "When you share photos, \n they'll appear on your profile.", fontSize = 15.sp, color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.size(10.dp))
                Text(text = "Share your first photo", fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0095f6), modifier = Modifier.clickable {
                    navController.navigate(CreateNewPost)
                })
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Header(data: User, uid: String, navController: NavController, condition: Boolean) {
    val context = LocalContext.current
    Column {
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
                if (condition) {
                    AnimatedVisibility(data.is_private) {
                        Icon(
                            painter = painterResource(R.drawable.instagram_lock),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(17.dp)
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(R.drawable.instagram_arrow_left_large),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp)
                            .clickable { navController.popBackStack() }
                    )
                }
                Text(
                    text = cacheString("$uid@username", data.username, context),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (data.is_verified) {
                    Spacer(modifier = Modifier.size(4.dp))
                    VerifiedBadge()
                }
            }
            AnimatedVisibility(condition) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(R.drawable.instagram_add),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 30.dp)
                            .size(22.dp)
                            .clickable { navController.navigate(CreateNewPost) }
                    )
                    Icon(
                        painter = painterResource(R.drawable.instagram_menu),
                        contentDescription = null,
                        modifier = Modifier
                            .size(19.dp)
                    )
                }
            }
        }
        Divider()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun edit(
    onClick: () -> Unit,
    image: String = "",
) {
    var displayName by rememberSaveable { mutableStateOf("") }
    var biography by rememberSaveable { mutableStateOf("") }
    var imageUriState by rememberSaveable { mutableStateOf<Uri?>(null) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUriState = uri
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(painter = painterResource(R.drawable.instagram_crossmark),
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onClick.invoke() })
            Text(text = "Edit Profile", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            Icon(
                painter = painterResource(R.drawable.instagram_checkmark),
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        updateString(field = "displayName", value = displayName)
                        updateString(field = "biography", value = biography)
                        onClick.invoke()
                        val firestore: FirebaseStorage = FirebaseStorage.getInstance()
                        val location = "$uid/image.png"
                        firestore
                            .getReference(location)
                            .putFile(if (imageUriState != null) imageUriState!! else return@clickable)
                        firestore.getReference(location).downloadUrl.addOnCompleteListener {
                            if (it.isSuccessful) updateString(
                                field = "image",
                                value = it.result?.toString()!!
                            ) else return@addOnCompleteListener
                        }
                    },
                tint = Color(0xFF0095f6)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (image == "") {
                Box(
                    modifier = Modifier
                        .size(90.dp, 90.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.7f))
                )
            } else {
                Image(
                    painter = rememberImagePainter(image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillWidth
                )
            }
            Image(
                painter = rememberImagePainter(imageUriState),
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillWidth
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "Change Profile Photo",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = Color(0xFF3897F0),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { launcher.launch("image/*") })
        Spacer(modifier = Modifier.size(10.dp))
        TextField(
            value = displayName,
            onValueChange = { displayName = it },
            placeholder = "Username",
            text = "Username"
        )
        Spacer(modifier = Modifier.size(10.dp))
        TextField(
            value = biography,
            onValueChange = { biography = it },
            placeholder = "Bio",
            text = "Bio"
        )
    }
}