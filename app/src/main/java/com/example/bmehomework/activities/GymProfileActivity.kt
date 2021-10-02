package com.example.bmehomework.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bmehomework.R
import com.example.bmehomework.adapters.CardRecyclerAdapter
import com.example.bmehomework.firebase.FirebaseUtil
import com.example.bmehomework.firebase.FirebaseUtil.firebaseAuth
import com.example.bmehomework.firebase.FirebaseUtil.firebaseDatabase
import com.example.bmehomework.firebase.FirebaseUtil.firebaseDatabaseRef
import com.example.bmehomework.models.SportPlace
import com.example.bmehomework.models.User
import com.example.bmehomework.utils.AppUtil
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_gym_profile.*
import kotlinx.android.synthetic.main.loading_overlay.*
import java.util.*


class GymProfileActivity : AppCompatActivity() {

    lateinit var selectedPlace : Place
    lateinit var selectedChipText : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym_profile)

        firebaseDatabaseRef.getReference("sportplaces").child(firebaseAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                        val sportplace = snapshot.getValue(SportPlace::class.java)
                        Log.d("FIREBASE", sportplace.toString())
                        setProfileViewFromData(sportplace!!)
                    //insert animationdrwable running avatar https://www.youtube.com/watch?v=EgYZu4v5wvg
                    //loading.visibility=View.INVISIBLE
                }else{
                    Log.d("FIREBASE", "NINCS ILYEN")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        gymChipGroup.setOnCheckedChangeListener { chipGroup, id ->
            run{
                selectedChipText = gymChipGroup.findViewById<Chip>(gymChipGroup.checkedChipId).text.toString()

                gymLogo.setImageResource(AppUtil.getCategoryByPrice(selectedChipText))

            }
        }

        AppUtil.animateView(loading_circle_overlay, View.VISIBLE, 0.4f, 200);


        gymSignout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)

        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyD9p44rqYJ3gSiV37cJXl2AGs8pox6wi4I")    //TODO MAKE IT SECURE
        }

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?

        autocompleteFragment!!.setPlaceFields(
            Arrays.asList(
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )

        autocompleteFragment!!.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("yesman", "Place: " + place.address + ", " + place.latLng)
                selectedPlace=place
                gymLocation.setText(place.address)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("yesman", "An error occurred: $status")
            }
        })

        gymSave.setOnClickListener {

            writeNewSportPlace(gymName.text.toString(), selectedPlace.latLng!!.latitude, selectedPlace.latLng!!.longitude, selectedChipText, selectedPlace.address.toString())
        }


    }

    private fun setProfileViewFromData(profil: SportPlace){
        gymLogo.setImageResource(AppUtil.getCategoryByPrice(profil.money));
        gymLocation.setText(profil.address)
        gymName.setText(profil.name)

        for (i in 0 until gymChipGroup.getChildCount()) {
            val chip = gymChipGroup.getChildAt(i) as Chip
            Log.i("outside if ", i.toString() + " chip = " + chip.text.toString())
            if (chip.text==profil.money) {
                chip.isChecked=true
                selectedChipText=profil.money
            }
        }
    }

    fun writeNewSportPlace(name: String, lat: Double, lng: Double, money: String, address: String) {
        val sportPlace = SportPlace(name, lat, lng, money, address)

        firebaseDatabase.child("sportplaces").child(firebaseAuth.currentUser!!.uid).setValue(sportPlace) //addOnCompleteListener with loading overlay
    }

}
