package com.example.bmehomework.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SportPlace( val name: String="",
                       val lat: Double=0.0,
                       val lng:Double=0.0,
                       val money:String="Free",
                       val address: String = ""
) {

}