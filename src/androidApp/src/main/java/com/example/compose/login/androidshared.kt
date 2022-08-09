package com.example.compose.login

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.R
import com.example.compose.login.defaultNavy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun email(email: String): Boolean {
    return email.split("@")[0].trim().length > 5 && android.util.Patterns.EMAIL_ADDRESS.matcher(
        email)
        .matches()
}

fun password(password: String): Boolean {
    return password.trim().length > 5
}

fun string(string: String): Boolean {
    return string.trim().length > 4
}

fun addUserData(username: String, displayName: String) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = "eEg6OPEjSNM7L6GSqCPoT6pfTLC3"
    val zzz = displayName.toList()[0].uppercaseChar()
    val array = arrayOf<String>()
    val data = hashMapOf(
        "username" to username,
        "displayName" to displayName,
        "image" to "https://via.placeholder.com/480x480.png/262626/ffffff?text=$zzz",
        "email" to "test",
        "biography" to "Add your Bio",
        "edge_followers" to listOf(*array),
        "edge_following" to listOf(*array),
        "edge_posts" to listOf(*array),
        "is_private" to false,
        "is_verified" to false
    )
    FirebaseFirestore.getInstance().collection("Users")
        .document(uid).set(data)
}

@SuppressLint("ComposableNaming")
@Composable
fun locale(modifier: Modifier = Modifier) {
    Row(modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "English (United States)",
            style = TextStyle(color = Color(0xFF737373), fontSize = 14.sp),
            modifier = modifier.padding(top = 2.dp)
        )
        Icon(painterResource(R.drawable.instagram_arrow_down),
            contentDescription = null,
            modifier = Modifier.size(17.dp),
            tint = Color(0xFF737373))
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun appPlaceholder() {
    Image(
        painter = painterResource(R.drawable.instagram_logo),
        contentDescription = null,
        modifier = Modifier.width(170.dp),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    isWorking: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (enabled) defaultNavy else Color(0xFFB2DFFC))
            .clickable(indication = null, interactionSource = MutableInteractionSource()) {
                if (enabled) onClick.invoke() else return@clickable
            },
        contentAlignment = Alignment.Center
    ) {
        if (isWorking)
            CircularProgressIndicator(
                strokeWidth = 2.5.dp,
                color = Color.White,
                modifier = Modifier.size(20.dp)
            )
        else
            Text(
                text = text,
                color = if (enabled) Color.White else Color(0xFFC8E9FD),
                fontSize = 14.sp,
                fontWeight = FontWeight(700)
            )
    }
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(indication = null, interactionSource = MutableInteractionSource()) {
                if (enabled) onClick.invoke() else return@clickable
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = defaultNavy,
            fontSize = 14.sp,
            fontWeight = FontWeight(700),
        )
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isSecure: Boolean = false,
) {
    val textFieldModifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(Color(0xFFF5F5F5))
        .border(0.5.dp, color = Color.Black.copy(alpha = 0.1f), shape = RoundedCornerShape(5.dp))
    val colors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = Color.Black.copy(alpha = 0.7f),
        placeholderColor = Color.Black.copy(alpha = 0.2f)
    )
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
            maxLines = 1,
            visualTransformation = if (isSecure) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun footer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    body: String,
    action: String,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(color = Color.Gray.copy(alpha = 0.04f), width = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
        )
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black.copy(alpha = 0.56f))) {
                    append(body)
                }
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF00376B)
                    )
                ) {
                    append(" $action.")
                }
            }, textAlign = TextAlign.Center, fontSize = 13.sp,
            modifier = Modifier.clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { onClick.invoke() }
        )
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun error(
    onClick: () -> Unit,
    text: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.56f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(22.dp))
            Text(
                text = "Oops?",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = text, color = Color.Gray, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 30.dp), fontSize = 14.sp
            )
            Spacer(modifier = Modifier.size(22.dp))
            Divider(color = Color(0x4DDBDBDB))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clickable{ onClick.invoke() }, contentAlignment = Alignment.Center
            ) {
                Text(text = "OK", textAlign = TextAlign.Center)
            }
        }
    }
}