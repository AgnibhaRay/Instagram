package com.example.compose.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.compose.*
import com.example.compose.R
import com.example.compose.entity.Post
import com.example.compose.entity.Strings.collection
import com.example.compose.entity.Strings.collectionPost
import com.example.compose.entity.User
import com.example.compose.model.MainViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.time.ExperimentalTime

@Composable
fun UserCardRect(
    uid: String,
    staticUid: String,
    navController: NavController,
    type: Int,
    viewModel: MainViewModel = viewModel()
) {
    var image by rememberSaveable { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var isVerified by rememberSaveable { mutableStateOf(false) }
    val isFollower = viewModel.currentUserData.value?.edge_following?.contains(uid)
    val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val firestoreInstance = firebaseFirestore.collection(collection).document(uid)
    firestoreInstance.addSnapshotListener { snapshot, _ ->
        if (snapshot != null && snapshot.exists()) {
            firestoreInstance.get().addOnSuccessListener {
                image = it?.getString("image").toString()
                displayName = it?.getString("displayName").toString()
                username = it?.getString("username").toString()
                isVerified = it?.getBoolean("is_verified")!!
            }
        }
    }
    val condition = staticUid == FirebaseAuth.getInstance().uid.toString()
    Row(
        modifier = Modifier
            .padding(6.dp, 10.dp, 22.dp, 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = MutableInteractionSource(), indication = null
            ) { navController.navigate("Profile/$uid") }) {
            Image(
                rememberImagePainter(image), contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 17.dp)
                    .size(56.dp)
                    .clip(CircleShape), contentScale = ContentScale.FillWidth
            )
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = username, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    if (isVerified) {
                        Spacer(modifier = Modifier.size(4.dp))
                        VerifiedBadge()
                    }
                }
                Text(text = displayName, fontSize = 14.sp, color = Color.Gray)
            }
        }
        val text = if (type != 1) "Remove" else "Following"
        if (condition) {
            if (isFollower == true) {
                OutlinedButton(
                    onClick = { unfollow(uid) },
                    text = text,
                    modifier = Modifier.height(36.dp)
                )
            } else {
                FilledButton(
                    onClick = { follow(uid) },
                    text = "Follow",
                    modifier = Modifier.height(36.dp)
                )
            }
        }
    }
}

@Composable
fun UserCard(
    uid: String,
    isFollower: Boolean,
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val currentData by viewModel.currentUserData.observeAsState(initial = User())
    val subTitle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    var image by rememberSaveable { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    var followingState by rememberSaveable { mutableStateOf(isFollower) }
    var isVerified by rememberSaveable { mutableStateOf(false) }
    val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val firestoreInstance = firebaseFirestore.collection(collection).document(uid)
    firestoreInstance.addSnapshotListener { snapshot, _ ->
        if (snapshot != null && snapshot.exists()) {
            firestoreInstance.get().addOnSuccessListener {
                image = it?.getString("image").toString()
                displayName = it?.getString("displayName").toString()
                isVerified = it?.getBoolean("is_verified")!!
            }
        }
    }
    LaunchedEffect(key1 = true) {
        followingState = currentData.edge_following.contains(uid)
    }
    Box(
        modifier = Modifier
            .padding(start = 13.dp)
            .size(160.dp, 213.dp)
            .border(
                color = Color.LightGray.copy(alpha = 0.4f),
                width = 0.5.dp,
                shape = RoundedCornerShape(5.dp)
            )
            .clickable { navController.navigate("Profile/$uid") },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Image(
                    rememberImagePainter(image),
                    contentDescription = null, modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillWidth
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = displayName, style = subTitle)
                if (isVerified) {
                    Spacer(modifier = Modifier.size(4.dp))
                    VerifiedBadge()
                }
            }
            Text(
                text = "Suggested for you",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.size(22.dp))
            if (!followingState) {
                FilledButton(
                    onClick = {
                        follow(uid)
                        followingState = !followingState
                    },
                    text = "Follow",
                    modifier = Modifier.size(130.dp, 35.dp)
                )
            } else {
                OutlinedButton(
                    onClick = {
                        unfollow(uid)
                        followingState = !followingState
                    },
                    text = "Unfollow",
                    modifier = Modifier.size(130.dp, 35.dp)
                )
            }
        }
    }
}

@Composable
fun VerifiedBadge() {
    Icon(
        painter = painterResource(R.drawable.instagram_verified),
        contentDescription = null,
        modifier = Modifier
            .size(13.dp),
        tint = Color(0xFF0095f6)
    )
}

@Composable
fun OutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: String,
) {
    Box(
        modifier = modifier
            .width(110.dp)
            .border(width = 1.dp, color = Color(0x1A3C3C43), shape = RoundedCornerShape(4.dp))
            .background(Color.White)
            .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                if (enabled) onClick.invoke() else return@clickable
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun FilledButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: String,
) {
    Box(
        modifier = modifier
            .width(110.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF0095f6))
            .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                if (enabled) onClick.invoke() else return@clickable
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun OutlinedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .border(width = 1.dp, color = Color(0x1A3C3C43), shape = RoundedCornerShape(4.dp))
            .background(Color.White)
            .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                if (enabled) onClick.invoke() else return@clickable
            },
        contentAlignment = Alignment.Center
    ) {
        val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha, content = content)
    }
}

@Composable
fun Divider() {
    Divider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.13f))
}

@Composable
fun DefaultView() {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = LinearEasing)
        )
    )
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(R.drawable.instagram_spinner),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .rotate(angle),
            tint = Color.Gray
        )
    }
}

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    text: String = "",
    isSecure: Boolean = false,
    enabled: Boolean = true
) {
    val textFieldModifier = Modifier
        .fillMaxWidth(0.9f)
    val colors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = Color.Black.copy(alpha = 0.7f),
        placeholderColor = Color.Black.copy(alpha = 0.4f)
    )
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text)
        Spacer(modifier = Modifier.size(13.dp))
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
            Box(
                modifier = textFieldModifier,
                contentAlignment = Alignment.CenterStart
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    colors = colors,
                    placeholder = ({
                        Text(
                            text = placeholder,
                            fontSize = 14.sp
                        )
                    }),
                    enabled = enabled,
                    textStyle = TextStyle(fontSize = 14.sp),
                    visualTransformation = if (isSecure) PasswordVisualTransformation() else VisualTransformation.None
                )
            }
            Divider(color = Color(0x2B3C3C43))
        }
    }
}

@Composable
fun FlexRow(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start,
    crossAxisSpacing: Dp = 0.dp,
    mainAxisSpacing: Dp = 0.dp,
    content: @Composable () -> Unit,
) = Layout(content, modifier) { measurables, constraints ->
    val hGapPx = mainAxisSpacing.roundToPx()
    val vGapPx = crossAxisSpacing.roundToPx()
    val rows = mutableListOf<MeasuredRow>()
    val itemConstraints = constraints.copy(minWidth = 0)

    for (measurable in measurables) {
        val lastRow = rows.lastOrNull()
        val placeable = measurable.measure(itemConstraints)
        if (lastRow != null && lastRow.width + hGapPx + placeable.width <= constraints.maxWidth) {
            lastRow.items.add(placeable)
            lastRow.width += hGapPx + placeable.width
            lastRow.height = max(lastRow.height, placeable.height)
        } else {
            val nextRow = MeasuredRow(
                items = mutableListOf(placeable),
                width = placeable.width,
                height = placeable.height
            )
            rows.add(nextRow)
        }
    }

    val width = rows.maxOfOrNull { row -> row.width } ?: 0
    val height = rows.sumOf { row -> row.height } + max(vGapPx.times(rows.size - 1), 0)
    val coercedWidth = width.coerceIn(constraints.minWidth, constraints.maxWidth)
    val coercedHeight = height.coerceIn(constraints.minHeight, constraints.maxHeight)
    layout(coercedWidth, coercedHeight) {
        var y = 0
        for (row in rows) {
            var x = when (alignment) {
                Alignment.Start -> 0
                Alignment.CenterHorizontally -> (coercedWidth - row.width) / 2
                Alignment.End -> coercedWidth - row.width
                else -> throw Exception("unsupported alignment")
            }
            for (item in row.items) {
                item.place(x, y)
                x += item.width + hGapPx
            }
            y += row.height + vGapPx
        }
    }
}

private data class MeasuredRow(
    val items: MutableList<Placeable>,
    var width: Int,
    var height: Int,
)

@Composable
fun UserLikeAndFollowView(
    Type: Int,
    list: List<String?>,
    viewModel: MainViewModel = viewModel()
) {
    val data by viewModel.currentUserData.observeAsState(initial = User())
    val followingList = data.edge_following
    val followedBy = list.filter { followingList.contains(it) }
    val filteredList = followedBy.take(3)
    var username by rememberSaveable { mutableStateOf("") }
    val size = if (Type != 0) 28.dp else 24.dp
    if (filteredList.isNotEmpty()){
        FirebaseFirestore.getInstance().collection(collection).document(filteredList[0].toString()).get().addOnSuccessListener {
            username = it.getString("username").toString()
        }
    }
    if (followedBy.isNotEmpty()){
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(30.dp), verticalAlignment = Alignment.CenterVertically) {
            Box {
                if (filteredList.size >= 3){
                    filteredList[2]?.let { FirebaseImage(uid = it, modifier = Modifier
                        .padding(start = (if (Type != 0) 44 else 36).dp)
                        .size(size)
                        .border(width = 2.dp, color = Color.White, shape = CircleShape)
                        .clip(CircleShape)
                        , type = 0, field = "image") }
                }
                if (filteredList.size >= 2){
                    filteredList[1]?.let { FirebaseImage(uid = it, modifier = Modifier
                        .padding(start = (if (Type != 0) 22 else 16).dp)
                        .size(size)
                        .border(width = 2.dp, color = Color.White, shape = CircleShape)
                        .clip(CircleShape), type = 0, field = "image") }
                }
                if (filteredList.isNotEmpty()){
                    filteredList[0]?.let { FirebaseImage(uid = it, modifier = Modifier
                        .size(size)
                        .border(width = 2.dp, color = Color.White, shape = CircleShape)
                        .clip(CircleShape), type = 0, field = "image") }
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            val text = if (Type != 0) "Followed by" else "Liked by"
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 14.sp)) {
                    append("$text ")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp)
                ) {
                    append(username)
                }
                withStyle(style = SpanStyle(fontSize = 14.sp)) {
                    append(" and ")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp)
                ) {
                    append((list.size-1).toString())
                }
                withStyle(style = SpanStyle(fontSize = 14.sp)) {
                    append(" others")
                }
            })
        }
    }
}

@Composable
fun Image(modifier: Modifier = Modifier, ID: String, uid: String) {
    var image by remember { mutableStateOf("") }
    FirebaseFirestore.getInstance().collection(collection).document(uid)
        .collection(collectionPost).document(ID).get()
        .addOnCompleteListener {
            if (it.isSuccessful) {
                image = it.result?.getString("Media_URl").toString()
            }
        }
    Box(modifier = modifier.size(110.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = modifier
                .size(110.dp)
                .background(Color.LightGray)
        )
        Image(
            rememberImagePainter(image), contentDescription = null,
            modifier = modifier.size(110.dp), contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun FirebaseImage(
    modifier: Modifier = Modifier,
    ID: String = "",
    uid: String,
    field: String,
    type: Int,
    contentScale: ContentScale = ContentScale.FillWidth
) {
    var image by rememberSaveable { mutableStateOf("") }
    Image(
        rememberImagePainter(image), contentDescription = null,
        modifier = modifier, contentScale = contentScale
    )
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        if (image != "") cacheString("$uid@$field", image, context)
    }
    val firebaseInstance = FirebaseFirestore.getInstance()
    when (type) {
        0 -> {
            firebaseInstance.collection(collection).document(uid)
                .get().addOnSuccessListener { image = it.getString(field).toString() }
        }
        1 -> {
            firebaseInstance.collection(collection).document(uid)
                .collection(collectionPost).document(ID).get()
                .addOnSuccessListener { image = it.getString(field).toString() }
        }
    }
}

@OptIn(ExperimentalTime::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun Post(
    ID: String,
    uid: String,
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val width = LocalConfiguration.current.screenWidthDp
    val data by viewModel.currentUserData.observeAsState(initial = User())
    val icon = Modifier
        .padding(end = 22.dp)
        .size(24.dp)
    var isLiked by remember { mutableStateOf(false) }
    var initialLiked by remember { mutableStateOf(false) }
    val size = animateDpAsState(
        targetValue = if (initialLiked) 156.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
    )
    if (initialLiked) {
        LaunchedEffect(key1 = true) {
            isLiked = true
            like(ID, uid)
            delay(900)
            initialLiked = !initialLiked
        }
    }
    // TODO
    var username by rememberSaveable { mutableStateOf("") }
    var image by rememberSaveable { mutableStateOf("") }
    var caption by rememberSaveable { mutableStateOf("") }
    var mediaUrl by rememberSaveable { mutableStateOf("") }
    var likeList by rememberSaveable { mutableStateOf("") }
    val livePost = MutableLiveData<Post>()
    val post: LiveData<Post> = livePost
    val datax by post.observeAsState(initial = Post())
    val firebaseInstance = FirebaseFirestore.getInstance()
        .collection(collection).document(uid)
        .collection(collectionPost).document(ID)
    firebaseInstance.addSnapshotListener { snapshot, _ ->
        if (snapshot != null && snapshot.exists()) {
            firebaseInstance.get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        livePost.value = document.toObject(Post::class.java)!!
                    }
                }
            }
        }
    }
    var time by rememberSaveable { mutableStateOf(Timestamp.now()) }
    val firestoreInstance = FirebaseFirestore.getInstance().collection(collection).document(uid)
    firestoreInstance.addSnapshotListener { snapshot, _ ->
        if (snapshot != null && snapshot.exists()) {
            firestoreInstance.get().addOnSuccessListener {
                username = it.getString("username").toString()
                image = it.getString("image").toString()
            }
        }
    }
    firestoreInstance.collection(collectionPost).document(ID).addSnapshotListener { snapshot, _ ->
        if (snapshot != null && snapshot.exists()) {
            firestoreInstance.collection(collectionPost).document(ID).get().addOnSuccessListener {
                mediaUrl = it.getString("Media_URl").toString()
                caption = it.getString("Caption").toString()
                likeList = it.get("UsersLiked").toString()
                time = it.getTimestamp("Time")!!
            }
        }
    }
    // TODO
    val likeArray = decodeStringToList(likeList)
    isLiked = likeArray.contains(FirebaseAuth.getInstance().uid.toString())
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    navController.navigate("$Profile/$uid")
                }) {
                Image(
                    rememberImagePainter(image), contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = username.lowercase(),
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                painter = painterResource(R.drawable.instagram_menu_dot),
                contentDescription = null, modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(width.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = { initialLiked = !initialLiked })
                }, contentAlignment = Alignment.Center
        ) {
            Image(
                rememberImagePainter(mediaUrl), contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(width.dp)
                    .background(Color.LightGray), contentScale = ContentScale.FillWidth
            )
            Image(
                painter = painterResource(R.drawable.feed_like_big),
                contentDescription = null,
                modifier = Modifier.size(size.value)
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth()
                .height(52.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(if (isLiked) R.drawable.instagram_heart_filled else R.drawable.instagram_heart_outline),
                contentDescription = null,
                modifier = icon.clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    isLiked = !isLiked
                    if (isLiked) like(ID, uid) else unlike(ID, uid)
                },
                tint = if (isLiked) Color(0xFFED4956) else Color.Black
            )
            Icon(
                painter = painterResource(R.drawable.instagram_comment),
                contentDescription = null, modifier = Modifier.padding(end = 22.dp).size(24.dp)
                    .clickable { navController.navigate("comment/$uid/$ID") }
            )
            Icon(
                painter = painterResource(R.drawable.instagram_dm),
                contentDescription = null, modifier = Modifier.size(24.dp)
            )
        }
        val likeString = if (likeArray.size != 1) "likes" else "like"
        val xyz = data.edge_following.toSet().intersect(likeArray.toSet())
        if (xyz.isNotEmpty()){
            Row(modifier = Modifier.padding(start = 10.dp)) {
                UserLikeAndFollowView(Type = 0, list = likeArray)
            }
        } else {
            AnimatedVisibility(likeList != "[]") {
                Text(
                    text = "${likeArray.size} $likeString",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)) {
                    append(username.lowercase())
                }
                withStyle(style = SpanStyle(fontSize = 14.sp, letterSpacing = (-0.2).sp)) {
                    append(" $caption")
                }
            },
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        if (datax.comments.isNotEmpty()){
            Text(text = "View all ${datax.comments.size} comments", color = Color.DarkGray.copy(alpha = 0.7F), fontSize = 14.sp,
                modifier = Modifier.padding(start = 10.dp).clickable { navController.navigate("comment/$uid/$ID") })
        }
        Spacer(modifier = Modifier.size(2.dp))
        if (time != Timestamp.now()) {
            Text(
                text = calculateTime(Timestamp.now().seconds - time.seconds),
                fontSize = 10.sp,
                color = Color.DarkGray.copy(alpha = 0.7F),
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
    }
}