package com.example.bmehomework.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bmehomework.R
import com.example.bmehomework.models.SportPlace
import com.example.bmehomework.utils.AppUtil
import com.example.bmehomework.utils.Extensions
import com.example.bmehomework.utils.Extensions.showTrackingBottomSheet
import com.example.bmehomework.viewmodels.SportPlaceViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.gms.maps.model.LatLng




class SearchFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,LocationListener {

    private lateinit var gMap : GoogleMap
    private lateinit var dialogView : View
    private lateinit var bottomSheetDialog :BottomSheetDialog
    private lateinit var myLocation : Location


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetDialog = BottomSheetDialog(context!!)
        dialogView = layoutInflater.inflate(R.layout.detail_bottom_layout, null)

        val mapFrag= childFragmentManager.findFragmentById(R.id.googleMapGym) as SupportMapFragment
        mapFrag.getMapAsync(this)

        getLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        gMap=googleMap
        val model= ViewModelProviders.of(activity!!).get(SportPlaceViewModel::class.java)
        model.sportPlacesList.observe(this, object : Observer<Any> {
            override fun onChanged(o: Any?) {
                val mySportList = o as ArrayList<SportPlace>

                for (sportPlace in mySportList) {

                    var drawableIcon = AppUtil.getCategoryByPrice(sportPlace.money)

                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(sportPlace.lat, sportPlace.lng))
                            .title(sportPlace.name)
                            .icon(AppUtil.BitmapFromVector(view!!.context, drawableIcon))
                            .snippet(sportPlace.address + "#" + sportPlace.money)
                    )
                }
            }
        })

        gMap.setOnMarkerClickListener(this)

            myLocation =
                Extensions.currentLocation

            moveCameraToCurrentPosition(myLocation!!.latitude, myLocation!!.longitude)


    }

    private fun moveCameraToCurrentPosition(currentLat: Double, currentLng: Double){

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(47.49, 19.04), 14.0f))


        gMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentLat,
                    currentLng
                ), 15f
            )
        )

        gMap.addMarker(
            MarkerOptions()
                .position(LatLng(currentLat, currentLng))
                .title("You")
        )
    }

    override fun onLocationChanged(location: Location) {

        Extensions.currentLocation=location

    }

    override fun onMarkerClick(p0: Marker): Boolean {

        if (p0.title.toString() != "You") {

            val snipList = p0.snippet?.toString()?.split("#")
            val sportPlace=SportPlace(name=p0.title, money=snipList?.get(1) ?: "", address = snipList?.get(0) ?: "", lat = p0.position.latitude, lng = p0.position.longitude)

            showTrackingBottomSheet(context!!, sportPlace)

            return true
        }
        return false
    }

}
