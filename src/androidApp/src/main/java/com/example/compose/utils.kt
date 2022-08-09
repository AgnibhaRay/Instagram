package com.example.compose

import android.content.Context
import android.net.Uri
import androidx.preference.PreferenceManager
import com.example.compose.entity.Strings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.roundToInt

data class Size(
    val statusBarHeight: Int,
    val navigationBarHeight: Int
)

internal fun getStatusAndNavigationBarsHeight(context: Context): Size {
    val deviceDensity: Float = context.resources.displayMetrics.density
    val idStatusBarHeight: Int =
        context.applicationContext.resources.getIdentifier("status_bar_height", "dimen", "android")
    val statusHeight =
        if (idStatusBarHeight > 0) context.resources.getDimensionPixelSize(idStatusBarHeight) / deviceDensity else 0F
    val idNavigationBarHeight: Int = context.applicationContext.resources.getIdentifier("navigation_bar_height", "dimen", "android")
    val navigationHeight = if (idNavigationBarHeight > 0) context.resources.getDimensionPixelSize(idNavigationBarHeight) / deviceDensity else 0F
    return Size(
        statusBarHeight = statusHeight.roundToInt(),
        navigationBarHeight = navigationHeight.roundToInt()
    )
}

internal fun addNewUser(username: String, displayName: String) {
    val uid = FirebaseAuth.getInstance().uid.toString()
    val array = arrayOf<String>()
    FirebaseFirestore.getInstance().collection(Strings.collection).document(uid).set(
        hashMapOf(
            "username" to username,
            "displayName" to displayName,
            "email" to FirebaseAuth.getInstance().currentUser?.email.toString(),
            "image" to "https://via.placeholder.com/480x480.png/262626/ffffff?text=${displayName.toList()[0].uppercase()}",
            "biography" to "Add your bio",
            "edge_followers" to listOf(*array),
            "edge_following" to listOf(*array),
            "edge_posts" to listOf(*array),
            "is_private" to false,
            "is_verified" to false,
            "TimeStamp" to FieldValue.serverTimestamp()
        )
    )
}

internal fun createNewPost(
    caption: String,
    image: Uri
): Boolean {
    val uid = FirebaseAuth.getInstance().uid.toString()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val array = arrayOf<String>()
    firestore.collection(Strings.collection).document(uid).collection(Strings.collectionPost).get()
        .addOnSuccessListener {
            val int = it.documents.size.toString()
            firestore.collection(Strings.collection).document(uid)
                .update("edge_posts", FieldValue.arrayUnion(int))
            storage.getReference("$uid/${Strings.collectionPost}/$int/0.png").putFile(image)
                .addOnSuccessListener {
                    storage.getReference("$uid/${Strings.collectionPost}/$int/0.png").downloadUrl.addOnSuccessListener { downloadUrl ->
                        firestore.collection(Strings.collection).document(uid)
                            .collection(Strings.collectionPost).document(int).set(
                            hashMapOf(
                                "ID" to int,
                                "Caption" to caption,
                                "Media_Type" to "image",
                                "Media_URl" to downloadUrl.toString(),
                                "UsersLiked" to listOf(*array),
                                "comments" to listOf(*array),
                                "Time" to FieldValue.serverTimestamp()
                            )
                        ).addOnSuccessListener {
                            firestore.collection(Strings.collectionPost).document("$uid@$int").set(
                                hashMapOf(
                                    "InitialValue" to "$uid@$int",
                                    "TimeStamp" to FieldValue.serverTimestamp()
                                )
                            )
                        }
                    }
                }
        }
    return true
}

internal fun cacheString(
    key: String,
    string: String,
    context: Context,
): String {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    if (string != "") sharedPreferences.edit().putString(key, string).apply()
    val zzz = if (string == "") sharedPreferences.getString(key, "") else string
    return zzz.toString()
}

internal fun follow(
    uid: String,
) {
    val current = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val firestore = FirebaseFirestore.getInstance()
    if (uid != current) {
        firestore.collection(Strings.collection).document(current)
            .update("edge_following", FieldValue.arrayUnion(uid))
        firestore.collection(Strings.collection).document(uid)
            .update("edge_followers", FieldValue.arrayUnion(current))
    }
}

internal fun unfollow(
    uid: String,
) {
    val current = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val firestore = FirebaseFirestore.getInstance()
    if (uid != current) {
        firestore.collection(Strings.collection).document(current)
            .update("edge_following", FieldValue.arrayRemove(uid))
        firestore.collection(Strings.collection).document(uid)
            .update("edge_followers", FieldValue.arrayRemove(current))
    }
}

internal fun updateString(
    field: String,
    value: String,
    documentId: String = FirebaseAuth.getInstance().currentUser?.uid.toString(),
) {
    if (value != "") FirebaseFirestore.getInstance().collection(Strings.collection)
        .document(documentId).update(field, value)
}

data class Post(
    val ID: String,
    val uid: String
)

internal fun decodePostString(String: String): Post {
    return Post(ID = String.split("@")[1], uid = String.split("@")[0])
}

internal fun decodeStringToList(String: String): List<String> {
    return String.removeSurrounding("[", "]").replace(" ", "").split(",").toList()
}

internal fun like(ID: String, uid: String) {
    FirebaseFirestore.getInstance().collection(Strings.collection).document(uid)
        .collection(Strings.collectionPost).document(ID).update(
            "UsersLiked",
            FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser?.uid.toString())
        )
}

internal fun unlike(ID: String, uid: String) {
    FirebaseFirestore.getInstance().collection(Strings.collection).document(uid)
        .collection(Strings.collectionPost).document(ID).update(
            "UsersLiked",
            FieldValue.arrayRemove(FirebaseAuth.getInstance().currentUser?.uid.toString())
        )
}

internal fun calculateTime(timeInSeconds: Long): String {
    var time = timeInSeconds
    val hours = time / 3600
    time %= 3600
    val minutes = time / 60
    time %= 60
    val seconds = time
    val x =
        if (hours.toInt() == 0) "" else if (hours.toInt() >= 24) "${hours.toInt() / 24} days ago" else "${hours.toInt()} hours ago"
    val y =
        if (hours.toInt() >= 1) "" else if (minutes.toInt() == 0) "" else "${minutes.toInt()} minutes ago"
    val z = if (minutes.toInt() >= 1) "" else "${seconds.toInt()} seconds ago"
    return "$x$y$z"
}