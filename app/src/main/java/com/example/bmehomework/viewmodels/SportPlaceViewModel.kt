package com.example.bmehomework.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bmehomework.models.SportPlace

class SportPlaceViewModel : ViewModel(){

    val sportPlacesList = MutableLiveData<ArrayList<SportPlace>>()

    fun setListCommunicator(sportList:ArrayList<SportPlace>){
        sportPlacesList.setValue(sportList)
    }
}