package com.example.compose.login

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.compose.addNewUser
import com.example.compose.app
import com.google.firebase.auth.FirebaseAuth

val defaultNavy = Color(0xFF0095F6)

@SuppressLint("ComposableNaming")
@Composable
fun default(navController: NavController) {
    val defaultPadding = 17.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        locale(modifier = Modifier.align(Alignment.TopCenter))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(defaultPadding)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            appPlaceholder()
            Spacer(modifier = Modifier.size(56.dp))
            Button(onClick = { navController.navigate("create") }, text = "Create new account")
            Spacer(modifier = Modifier.size(13.dp))
            TextButton(onClick = { navController.navigate("login") }, text = "Log In")
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun login(navController: NavController) {
    val defaultPadding = 22.dp
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isWorking by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        locale(modifier = Modifier.align(Alignment.TopCenter))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(defaultPadding)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            appPlaceholder()
            Spacer(modifier = Modifier.size(26.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Phone number, email or username"
            )
            Spacer(modifier = Modifier.size(10.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isSecure = true
            )
            Spacer(modifier = Modifier.size(10.dp))
            Button(onClick = {
                isWorking = !isWorking
                val Instance = FirebaseAuth.getInstance()
                Instance.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { login ->
                        val exception = login.exception?.localizedMessage.toString()
                        if (!login.isSuccessful) error = exception
                        else if (Instance.currentUser?.isEmailVerified == false) error = exception
                        else navController.navigate(app) { popUpTo(0) }
                        isWorking = !isWorking
                    }
            },
                text = "Log In",
                enabled = email(email.trimEnd()) && password(password.trimEnd()),
                isWorking = isWorking)
            Spacer(modifier = Modifier.size(13.dp))
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Black.copy(alpha = 0.56f))) {
                        append("Forgot your login details? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF00376B)
                        )
                    ) {
                        append("Get help logging in.")
                    }
                }, textAlign = TextAlign.Center, fontSize = 13.sp,
                modifier = Modifier.clickable { navController.navigate("forgot") }
            )
        }
        footer(
            onClick = { navController.navigate("create") },
            body = "Don't have an account?",
            action = "Sign up",
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
        if (error != "")
            error(onClick = { error = "" }, text = error)
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun create(navController: NavController) {
    val defaultPadding = 22.dp
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isWorking by remember { mutableStateOf(false) }
    var viewState by rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current
    val string = "We sent an email to $email with a link to verify your account."
    when (viewState) {
        1 -> BackHandler(enabled = true) {
            viewState = 0
        }
        2 -> BackHandler(enabled = true) {
            viewState = 1
        }
        3 -> BackHandler(enabled = true) {
            viewState = 2
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        locale(modifier = Modifier.align(Alignment.TopCenter))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(defaultPadding)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (viewState) {
                0 -> {
                    appPlaceholder()
                    Spacer(modifier = Modifier.size(30.dp))
                    TextField(value = email, onValueChange = { email = it }, placeholder = "Email")
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(
                        onClick = { viewState = 1 },
                        text = "Next",
                        enabled = email(email),
                        isWorking = isWorking
                    )
                }
                1 -> {
                    Text(
                        text = "NAME AND PASSWORD",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.size(22.dp))
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "Full name"
                    )
                    Spacer(modifier = Modifier.size(13.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Password",
                        isSecure = true
                    )
                    Spacer(modifier = Modifier.size(22.dp))
                    Button(
                        onClick = { viewState = 2 },
                        text = "Continue",
                        enabled = string(username.trimEnd()) && password(password.trimEnd())
                    )
                }
                2 -> {
                    Text(
                        text = "CREATE USERNAME",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.size(17.dp))
                    Text(
                        text = "Add a username or use our suggestion. You can\n change this at any time.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(17.dp))
                    TextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        placeholder = "Username"
                    )
                    Spacer(modifier = Modifier.size(22.dp))
                    Button(
                        onClick = { viewState = 3 },
                        text = "Continue",
                        enabled = string(displayName)
                    )
                }
                3 -> {
                    Text(
                        text = "Sign up as\n$displayName?",
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(
                        text = "You can always change your username later.",
                        textAlign = TextAlign.Center, color = Color.Gray
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(onClick = {
                        isWorking = !isWorking
                        val instance = FirebaseAuth.getInstance()
                        instance.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { login ->
                                val exception = login.exception?.localizedMessage.toString()
                                if (!login.isSuccessful) {
                                    error = exception
                                } else {
                                    addNewUser(username, displayName)
                                    instance.currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
                                                navController.navigate("login") { popUpTo(0) }
                                            } else
                                                error = it.exception?.localizedMessage.toString()
                                        }
                                }
                                isWorking = !isWorking
                            }
                    }, text = "Sign Up", isWorking = isWorking)
                }
            }
        }
        footer(
            onClick = { navController.navigate("login") },
            body = "Already have an account?",
            action = "Log in",
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
        if (error != "")
            error(onClick = { error = "" }, text = error)
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun forgot() {
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isWorking by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val defaultPadding = 17.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(defaultPadding)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.size(13.dp))
            Text(
                text = "Login Help",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(46.dp))
            Text(text = "Find Your Account", fontSize = 22.sp)
            Spacer(modifier = Modifier.size(13.dp))
            Text(
                text = "Enter your username or the email address linked to your account",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(22.dp)
            )
            Spacer(modifier = Modifier.size(13.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Username or email address"
            )
            Spacer(modifier = Modifier.size(13.dp))
            Button(onClick = {
                isWorking = !isWorking
                val instance = FirebaseAuth.getInstance()
                instance.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful)
                    Toast.makeText(context,
                        "We sent an email to $email with a link to get back into your account.",
                        Toast.LENGTH_SHORT).show()
                    else {
                        error = it.exception?.localizedMessage.toString()
                    }
                    isWorking = !isWorking
                }
            }, text = "Next", enabled = email(email.trimEnd()), isWorking = isWorking)
        }
        if (error != "")
            error(onClick = { error = "" }, text = error)
    }
}