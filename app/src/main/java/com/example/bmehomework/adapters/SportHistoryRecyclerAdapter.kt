package com.example.bmehomework.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmehomework.R
import com.example.bmehomework.models.SportTracking
import com.example.bmehomework.utils.AppUtil

class SportHistoryRecyclerAdapter(sportList: MutableList<SportTracking>) :
    RecyclerView.Adapter<SportHistoryRecyclerAdapter.ViewHolder>() {

    private var sportHistory: MutableList<SportTracking> = sportList

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        Log.d("emptyview",sportHistory.size.toString())
        lateinit var v:View
        when(viewType){
            VIEW_TYPE_EMPTY -> v=LayoutInflater.from(parent.context).inflate(R.layout.empty_layout, parent, false)
            VIEW_TYPE_ITEM -> v=LayoutInflater.from(parent.context).inflate(R.layout.sporthistory_layout, parent, false)
        }
        return ViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return if (sportHistory.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return sportHistory.size    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {

            var currentSportTracking = sportHistory[position]

            holder.itemTitle.text = currentSportTracking.name
            holder.itemTime.text = AppUtil.getMinutesFromTimestamp(currentSportTracking.timestamp)
                .toString() + " minutes"
            holder.itemCategoryLogo.setImageResource(AppUtil.getCategoryByPrice(currentSportTracking.money))
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemCategoryLogo: ImageView
        var itemTitle: TextView
        var itemTime: TextView
        //card other elements like logo, text, distance, crowded

        init {
            itemCategoryLogo=itemView.findViewById(R.id.sporthistory_type)
            itemTitle = itemView.findViewById(R.id.sporthistory_name)
            itemTime = itemView.findViewById(R.id.sporthistory_time)

        }
    }

}