package com.example.bmehomework.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

import com.example.bmehomework.R
import com.example.bmehomework.activities.AuthActivity
import com.example.bmehomework.adapters.CardRecyclerAdapter
import com.example.bmehomework.adapters.SportHistoryRecyclerAdapter
import com.example.bmehomework.firebase.FirebaseUtil
import com.example.bmehomework.firebase.FirebaseUtil.firebaseAuth
import com.example.bmehomework.firebase.FirebaseUtil.firebaseDatabase
import com.example.bmehomework.firebase.FirebaseUtil.firebaseDatabaseRef
import com.example.bmehomework.models.SportPlace
import com.example.bmehomework.models.SportTracking
import com.example.bmehomework.service.TrackingService
import com.example.bmehomework.utils.AppUtil
import com.example.bmehomework.utils.Extensions
import com.example.bmehomework.utils.Extensions.writeNewSportTrackingToHistory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class ProfileFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: SportHistoryRecyclerAdapter

    private var sportHistory = ArrayList<SportTracking>()


    object Constants {
        const val TIMER_INTERVAL = 1000
    }

    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private lateinit var alreadySportTracking: SportTracking

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recyclerAdapter = SportHistoryRecyclerAdapter(sportHistory)

        getUserData()


        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private fun getUserData() {
        sportHistory.clear()
        firebaseDatabaseRef.getReference("history").child(firebaseAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    sportHistory.clear()
                    for (sportSnapshot in snapshot.children){
                        val sportplace = sportSnapshot.getValue(SportTracking::class.java)
                        sportHistory.add(sportplace!!)
                    }
                    recyclerView.adapter = SportHistoryRecyclerAdapter(sportHistory)
                    Log.d("firebase", sportHistory.size.toString())
                    //insert animationdrwable running avatar https://www.youtube.com/watch?v=EgYZu4v5wvg
                    //loading.visibility=View.INVISIBLE
                    sporthistory_recyclerview_empty.visibility= GONE
                }else{
                    sporthistory_recyclerview_empty.visibility= VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileid.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent (activity, AuthActivity::class.java)
            activity!!.startActivity(intent)
        }

        recyclerView=view.findViewById(R.id.sporthistory_recyclerview)
        recyclerView.adapter=recyclerAdapter

        timerCard.visibility=GONE

        FirebaseUtil.firebaseDatabase.child("trackings").child(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    timerCard.visibility=View.VISIBLE

                    alreadySportTracking = it.getValue(SportTracking::class.java)!!

                    time= TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-alreadySportTracking.timestamp).toDouble()

                    serviceIntent = Intent(activity, TrackingService::class.java)
                    activity!!.registerReceiver(updateTime, IntentFilter(TrackingService.TIMER_UPDATED))

                    startTimer()

                } else {
                    timerCard.visibility=GONE
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        startStopButton.setOnClickListener{
            stopTimer()
            if(timerCard.visibility== VISIBLE) {
                timerCard.visibility=GONE
            }

        }
    }

    private fun startTimer()
    {
        serviceIntent.putExtra(TrackingService.TIME_EXTRA, time)
        activity!!.startService(serviceIntent)
        startStopButton.text = "Stop"
    }

    private fun stopTimer()
    {
        writeNewSportTrackingToHistory(alreadySportTracking)
        requireActivity().stopService(serviceIntent)
        startStopButton.text = "Start"
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(TrackingService.TIME_EXTRA, 0.0)
            if(timeTV != null && timeTV.visibility==View.VISIBLE)
                timeTV.text = AppUtil.getTimeStringFromDouble(time)
        }
    }

}
