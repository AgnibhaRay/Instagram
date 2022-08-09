package com.example.compose.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.compose.R
import com.example.compose.entity.Post
import com.example.compose.entity.Strings
import com.example.compose.entity.User
import com.example.compose.model.MainViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CommentView(
    ID: String,
    uid: String,
    navController: NavController,
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val livePost = MutableLiveData<Post>()
    val post: LiveData<Post> = livePost
    val data by post.observeAsState(initial = Post())
    val currentUserData by viewModel.currentUserData.observeAsState(initial = User())
    val firebaseInstance = FirebaseFirestore.getInstance()
        .collection(Strings.collection).document(uid)
        .collection(Strings.collectionPost).document(ID)
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
    var comment by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize()) {
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
                text = "Comments",
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column {
            Spacer(modifier = Modifier.size(46.dp))
            data.comments.forEach {
                comment(uid = it.uid, msg = it.comment, time = it.time, navController)
            }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.Image(
                rememberImagePainter(currentUserData.image),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillWidth
            )
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth(0.9F),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.Black.copy(alpha = 0.7f),
                    placeholderColor = Color.Black.copy(alpha = 0.4f)
                ),
                placeholder = ({
                    Text(
                        text = "Comment as ${currentUserData.username}...",
                        fontSize = 14.sp
                    )
                })
            )
            Text(
                text = "Post",
                fontSize = 15.sp,
                color = Color(0xFF0095f6),
                modifier = Modifier.clickable { doComment(firebaseInstance, comment) })
        }
    }
}

fun doComment(firebaseInstance: DocumentReference, text: String) {
    firebaseInstance.update(
        "comments", FieldValue.arrayUnion(
            hashMapOf(
                "uid" to FirebaseAuth.getInstance().uid.toString(),
                "comment" to text,
                "time" to Timestamp.now().seconds
            )
        )
    )
}

@SuppressLint("ComposableNaming")
@Composable
fun comment(uid: String, msg: String, time: Int, navController: NavController) {
    var image by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    FirebaseFirestore.getInstance().collection(Strings.collection).document(uid).get()
        .addOnSuccessListener {
            image = it.getString("image").toString()
            username = it.getString("username").toString()
        }
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("${com.example.compose.Profile}/$uid") }
        , verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.Image(
            rememberImagePainter(image),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp, 10.dp, 0.dp, 10.dp)
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.FillWidth
        )
        Column {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    ) {
                        append(username.lowercase())
                    }
                    withStyle(style = SpanStyle(fontSize = 14.sp, letterSpacing = (-0.2).sp)) {
                        append(" $msg")
                    }
                },
                modifier = Modifier.padding(start = 10.dp)
            )
            Text(
                text = time(Timestamp.now().seconds - time.toLong()),
                fontSize = 12.sp,
                color = Color.DarkGray.copy(alpha = 0.7F),
                modifier = Modifier.padding(top = 4.dp, start = 10.dp)
            )
        }
    }
}

fun time(timeInSeconds: Long): String {
    var time = timeInSeconds
    val hours = time / 3600
    time %= 3600
    val minutes = time / 60
    time %= 60
    val seconds = time
    val x =
        if (hours.toInt() == 0) "" else if (hours.toInt() >= 24) "${hours.toInt() / 24}D" else "${hours.toInt()}h"
    val y =
        if (hours.toInt() >= 1) "" else if (minutes.toInt() == 0) "" else "${minutes.toInt()}m"
    val z = if (minutes.toInt() >= 1) "" else "${seconds.toInt()}s"
    return "$x$y$z"
}
