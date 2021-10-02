package com.example.bmehomework.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bmehomework.R
import com.example.bmehomework.firebase.FirebaseUtil
import com.example.bmehomework.models.SportPlace
import com.example.bmehomework.models.SportTracking
import com.google.android.material.bottomsheet.BottomSheetDialog


object Extensions {

    lateinit var currentLocation : Location

    fun Toast.showCustomToast(activity: Activity, message: String, isError: Boolean?=false)
    {

        val layout = activity.layoutInflater.inflate(
            R.layout.custom_toast_layout,
            activity.findViewById(R.id.toast_container)
        )

        // set the text of the TextView of the message
        val textView =  layout.findViewById<TextView>(R.id.toast_text)
        textView.text = message

        if(isError!!){
            val toastType =  layout.findViewById<FrameLayout>(R.id.button_accent_border)
            toastType.setBackgroundResource(R.color.errorColor)
        }

        // use the application extension function
        this.apply {
            setGravity(Gravity.BOTTOM, 0, 40)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    fun getCurrentLocation(activity: Activity, context: Context): Location {
        val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)!!
        }
        return Location("")
    }

    fun Activity.checkAndRequestPermission(
        title: String, message: String,
        manifestPermission: String, requestCode: Int,
        action: () -> Unit
    ) {
        val permissionStatus = ContextCompat.checkSelfPermission(applicationContext, manifestPermission)

        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, manifestPermission)) {
                    requestPermission(manifestPermission, requestCode)
            } else {
                // No explanation needed -> request the permission
                requestPermission(manifestPermission, requestCode)
            }
        } else {
            action()
        }
    }

    fun Activity.requestPermission(manifestPermission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(manifestPermission), requestCode)
    }

    fun showTrackingBottomSheet(context: Context, p0: SportPlace){
        val bottomSheetDialog = BottomSheetDialog(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.detail_bottom_layout, null)
        var alreadySportTracking: SportTracking?=null

        val sportName = dialogView.findViewById<TextView>(R.id.sportName)
        val sportImage = dialogView.findViewById<ImageView>(R.id.sportImage)
        val sportAddress = dialogView.findViewById<TextView>(R.id.sportAddress)
        val sportDistance = dialogView.findViewById<TextView>(R.id.sportDistanceAndMoney)
        val trackButton = dialogView.findViewById<Button>(R.id.idBtnDismiss)


        FirebaseUtil.firebaseDatabase.child("trackings").child(FirebaseUtil.firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    alreadySportTracking = it.getValue(SportTracking::class.java)!!
                    trackButton.setText("STOP PREVIOUS TRACKING")
                    trackButton.setBackgroundColor(Color.parseColor("#B00020"))


                } else {
                    trackButton.setText("START TRACKING")
                    trackButton.setBackgroundColor(Color.parseColor("#13715B")) //PRIMAR

                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        sportName.text = p0.name

        sportAddress.text = p0.address
        var drawableIcon = AppUtil.getCategoryByPrice(p0.money)
        sportImage.setImageResource(drawableIcon)

        val targetLocation = Location("") //provider name is unnecessary

        targetLocation.latitude = p0.lat //your coords of course
        targetLocation.longitude = p0.lng

        sportDistance.text = AppUtil.getDistanceBetweenTwoLocation(Extensions.currentLocation, targetLocation)

        trackButton.setOnClickListener {
            if (alreadySportTracking != null) {

                writeNewSportTrackingToHistory(alreadySportTracking!!)

                trackButton.setText("START TRACKING")
                trackButton.setBackgroundColor(Color.parseColor("#13715B")) //PRIMAR
                alreadySportTracking = null
            } else {
                val sportTracking = SportTracking(
                    name = p0.name,
                    money = p0.money,
                    timestamp = System.currentTimeMillis()
                )
                FirebaseUtil.firebaseDatabase.child("trackings").child(FirebaseUtil.firebaseAuth.currentUser!!.uid)
                    .setValue(sportTracking)
                trackButton.setText("STOP PREVIOUS TRACKING")
                trackButton.setBackgroundColor(Color.parseColor("#B00020"))

                alreadySportTracking = sportTracking
            }
        }

        bottomSheetDialog.setContentView(dialogView)

        // on below line we are calling
        // a show method to display a dialog.
        bottomSheetDialog.show()
    }

    fun writeNewSportTrackingToHistory(alreadySportTracking: SportTracking){
        val key = FirebaseUtil.firebaseDatabase.child("history").push().key
        FirebaseUtil.firebaseDatabase.child("history").child(FirebaseUtil.firebaseAuth.currentUser!!.uid)
            .child(key.toString()).setValue(
                SportTracking(
                    name = alreadySportTracking!!.name,
                    money = alreadySportTracking!!.money,
                    timestamp = System.currentTimeMillis() - alreadySportTracking!!.timestamp
                )
            )
        FirebaseUtil.firebaseDatabase.child("trackings").child(FirebaseUtil.firebaseAuth.currentUser!!.uid)
            .removeValue();
    }

}