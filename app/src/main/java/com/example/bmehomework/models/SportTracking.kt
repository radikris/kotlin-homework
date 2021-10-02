package com.example.bmehomework.models

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ServerValue

@IgnoreExtraProperties
data class SportTracking( val name: String="",
                       val money:String="Free",
                       val timestamp: Long = 0
) {

}