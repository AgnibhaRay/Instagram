package com.example.compose.model

import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.entity.Strings.collection
import com.example.compose.entity.Strings.collectionPost
import com.example.compose.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@OptIn(ExperimentalMaterialApi::class)
class MainViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _currentUserData = MutableLiveData<User>()
    val currentUserData: LiveData<User> = _currentUserData
    private val _differentUserData = MutableLiveData<User>()
    val differentUserData: LiveData<User> = _differentUserData
    private val _stackOfPost = MutableLiveData<List<String>>()
    val stackOfPost: LiveData<List<String>> = _stackOfPost
    private val _listOfUsers = MutableLiveData<List<String>>()
    val listOfUsers: LiveData<List<String>> = _listOfUsers
    private val _string = MutableLiveData<String>()
    val string: LiveData<String> = _string

    init {
        getUserData(_currentUserData, firebaseAuth.uid.toString())
        stack(_listOfUsers, collection)
        stack(_stackOfPost, collectionPost)
        getString()
    }

    fun getDifferentUserData(uid: String) {
        getUserData(_differentUserData, uid)
    }

    fun xyz() {
        _string.value = ""
    }

    private fun getString() {
        firebaseFirestore.collection("Strings").document("announcement")
            .get().addOnSuccessListener {
                _string.value = it.getString("String").toString()
            }
    }

    private fun getUserData(returnValue: MutableLiveData<User>, uid: String) {
        // TODO: Update [Strings.collection] String
        val firestoreInstance = firebaseFirestore.collection(collection).document(uid)
        firestoreInstance.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                firestoreInstance.get().addOnSuccessListener {
                    returnValue.value = it.toObject(User::class.java)
                }
            }
        }
    }

    private fun stack(returnValue: MutableLiveData<List<String>>, collection: String) {
        val firestoreInstance = firebaseFirestore.collection(collection)
            .orderBy("TimeStamp", Query.Direction.DESCENDING)
        firestoreInstance.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                firestoreInstance.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list: MutableList<String> = ArrayList()
                        for (document in task.result!!) {
                            list.add(document.id)
                        }
                        val listOf = list.filter { it != firebaseAuth.uid.toString() }.toList()
                        returnValue.value =
                            if (collection != collectionPost) listOf else list
                    }
                }
            }
        }
    }
}