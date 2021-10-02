package com.example.bmehomework.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseUtil {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser= firebaseAuth.currentUser
    val firebaseDatabase = Firebase.database.reference
    val firebaseDatabaseRef = Firebase.database


}