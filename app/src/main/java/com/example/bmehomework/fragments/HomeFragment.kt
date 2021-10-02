package com.example.bmehomework.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.bmehomework.adapters.CardRecyclerAdapter

import com.example.bmehomework.R
import com.example.bmehomework.models.SportPlace
import com.example.bmehomework.viewmodels.SportPlaceViewModel
import com.google.firebase.database.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: CardRecyclerAdapter
    private lateinit var dbref : DatabaseReference
    private lateinit var loading: ProgressBar

    private var sportList = ArrayList<SportPlace>()

    private var model: SportPlaceViewModel?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        recyclerAdapter= CardRecyclerAdapter(sportList)
        val v: View= inflater.inflate(R.layout.fragment_home, container, false)
        loading=v.findViewById(R.id.sportcard_loading)

        model= ViewModelProviders.of(activity!!).get(SportPlaceViewModel::class.java)

        getUserData()

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView=view.findViewById(R.id.sportcard_recycler_view)
        recyclerView.adapter=recyclerAdapter

    }

    private fun getUserData() {
        loading.visibility=View.VISIBLE

        sportList.clear()

        dbref = FirebaseDatabase.getInstance().getReference("sportplaces")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (sportSnapshot in snapshot.children){
                        val sportplace = sportSnapshot.getValue(SportPlace::class.java)
                        sportList.add(sportplace!!)
                    }
                    recyclerView.adapter = CardRecyclerAdapter(sportList)
                    //insert animationdrwable running avatar https://www.youtube.com/watch?v=EgYZu4v5wvg
                    loading.visibility=View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //TODO add to viewmodel
        model!!.setListCommunicator(sportList)

    }

}
