package com.example.bmehomework.adapters

import android.graphics.Color
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bmehomework.R
import com.example.bmehomework.firebase.FirebaseUtil
import com.example.bmehomework.models.SportPlace
import com.example.bmehomework.models.SportTracking
import com.example.bmehomework.utils.AppUtil
import com.example.bmehomework.utils.Extensions
import com.example.bmehomework.utils.Extensions.getCurrentLocation
import com.example.bmehomework.utils.Extensions.showTrackingBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialog

class CardRecyclerAdapter(sportList: ArrayList<SportPlace>) :
    RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder>() {

    private var sportPlaces: MutableList<SportPlace> = sportList

    private lateinit var dialogView : View
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var myLocation : Location
    private var alreadyTracking : Boolean=false
    private var alreadySportTracking : SportTracking? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
val v=LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
    return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return sportPlaces.size }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var currentSportPlace = sportPlaces[position]

        holder.itemTitle.text = currentSportPlace.name
        holder.itemMoney.text=currentSportPlace.money
        holder.itemCategoryLogo.setImageResource(AppUtil.getCategoryByPrice(currentSportPlace.money))
        val targetLocation=Location("")
        targetLocation.latitude=currentSportPlace.lat
        targetLocation.longitude=currentSportPlace.lng

        //TODO currentlocation should be outside extension
        holder.itemDistance.text=AppUtil.getDistanceBetweenTwoLocation(Extensions.currentLocation, targetLocation)

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
         var itemCategoryLogo: ImageView
         var itemTitle: TextView
         var itemMoney: TextView
         var itemDistance: TextView
        //card other elements like logo, text, distance, crowded

        init {
            itemCategoryLogo=itemView.findViewById(R.id.sportcard_logo)
            itemTitle = itemView.findViewById(R.id.sportcard_title)
            itemMoney = itemView.findViewById(R.id.sportcard_money)
            itemDistance = itemView.findViewById(R.id.sportcard_distance)

            itemView.setOnClickListener{
                //bottomsheet comes up
                val position=bindingAdapterPosition
                val p0: SportPlace
                p0=sportPlaces[position]

                showTrackingBottomSheet(itemView.context, p0)

            }
        }
    }

}