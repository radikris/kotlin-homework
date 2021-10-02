package com.example.bmehomework.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bmehomework.R
import com.example.bmehomework.firebase.FirebaseUtil.firebaseAuth
import com.example.bmehomework.firebase.FirebaseUtil.firebaseDatabase
import com.example.bmehomework.firebase.FirebaseUtil.firebaseUser
import com.example.bmehomework.models.User
import com.example.bmehomework.models.UserType
import com.example.bmehomework.utils.Extensions
import com.example.bmehomework.utils.Extensions.showCustomToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_auth.*

public val TAG : String = "FIREBASE_AUTH"

class AuthActivity : AppCompatActivity(), LocationListener {

    private var permissionDenied = true
    lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    private fun registerUser(){
        signin_signup_btn.isEnabled=false


        if(profileName.text.isNotEmpty() && email.text.isNotEmpty() && password.text.toString()==(passwordConfirm.text.toString())){
            Log.d(TAG, "register go")

            firebaseRegister()

        }
        signin_signup_btn.isEnabled=true

    }

    private fun firebaseRegister(){
        firebaseAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    writeNewUser(firebaseAuth.currentUser!!.uid, profileName.text.toString(), email.text.toString(), signup_gymornot.isChecked)

                    //updateUI(user)
                    if(signup_gymornot.isChecked.not())
                        goToMainPage()
                    else
                        goToGymPage()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)

                    Toast(this).showCustomToast(this, "Authentication failed", isError = true)
                    //updateUI(null)
                }
            }
    }

    private fun firebaseLogin(){
        firebaseAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("KRIS", firebaseAuth.toString())
                    loginUserByType()
                } else {
                    // If sign in fails, show  Toast to the user.
                    Toast(this).showCustomToast(this, "Authentication failed", isError = true)
                }
            }

    }

    private fun loginUser() {
        signin_signup_btn.isEnabled = false
        if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
            //firebaseLogin()
            checkPermissionBeforeAuth { firebaseLogin()}
        }
        signin_signup_btn.isEnabled=true

    }

    private fun getLocation() {
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )

            if(applicationContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                permissionDenied=false;
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    5f,
                    this
                )
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

            }

        }else{
            Extensions.currentLocation =
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)!!
            permissionDenied=false
            Log.d("PERMISSIONcheck", "PERMISSION GRANTED FALSEBAN")

        }

    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        getLocation()
        if(permissionDenied==false) {
            Extensions.currentLocation =
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)!!
            if( user != null) {
                loginUserByType()
            }else{

            }
        }

        if( user != null){
            loginUserByType()
        }else {
            withoutAutomataLogin()
        }

    }

    private fun withoutAutomataLogin(){
        setContentView(R.layout.activity_auth)
        layoutChangeForInit()

        signin.setOnClickListener {
            layoutChangeForSignIn()
        }
        signup.setOnClickListener {
            layoutChangeForSignUp()
        }

        signin_signup_btn.setOnClickListener {
            Log.d(TAG, signin_signup_btn.text.toString())
            if (signin_signup_btn.text.toString() == "Sign In") {
                loginUser()
            } else {
                registerUser()
            }
        }
    }

    private fun layoutChangeForInit(){
        passwordConfirmEditText.visibility=View.GONE
        profileNameEditText.visibility=View.GONE
        signup_gymornot.visibility=View.GONE
        signup_gymornot_text.visibility=View.GONE
    }

    private fun layoutChangeForSignIn(){
        signin.setTextColor(ContextCompat.getColor(this, R.color.appWhite))
        signin.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        signup.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        signup.setBackgroundColor(ContextCompat.getColor(this, R.color.appWhite))
        signin_signup_txt.text = "Sign In"
        signin_signup_btn.text = "Sign In"
        signup_gymornot.visibility = View.GONE
        passwordConfirmEditText.visibility=View.GONE
        profileNameEditText.visibility=View.GONE
        signup_gymornot_text.visibility=View.GONE
    }

    private fun layoutChangeForSignUp(){
        signup.setTextColor(ContextCompat.getColor(this, R.color.appWhite))
        signup.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        signin.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        signin.setBackgroundColor(ContextCompat.getColor(this, R.color.appWhite))
        signin_signup_txt.text = "Sign Up"
        signin_signup_btn.text = "Sign Up"
        signup_gymornot.visibility = View.VISIBLE
        passwordConfirmEditText.visibility=View.VISIBLE
        profileNameEditText.visibility=View.VISIBLE
        signup_gymornot_text.visibility=View.VISIBLE
    }

    private fun goToMainPage(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun goToGymPage(){
        val intent = Intent(this, GymProfileActivity::class.java)
        startActivity(intent)
    }

    fun writeNewUser(userId: String, name: String, email: String, isGym: Boolean) {
        val user = User(name, email, isGym)

        firebaseDatabase.child("users").child(userId).setValue(user)
    }

    fun checkPermissionBeforeAuth(authCallBack: ()->Unit) {
        if ((ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {

        } else {
            Extensions.currentLocation =
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)!!
            permissionDenied = false
            authCallBack.invoke()
        }
    }

    fun loginUserByType(){
        var type : UserType=UserType.STREET

        if(permissionDenied==false) {

            firebaseDatabase.child("users").child(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                val temp = it.hasChild("gym") && (it.child("gym").getValue() as Boolean)
                Log.i("firebase", temp.toString())
                if (it.value != null && temp) {
                    type = UserType.GYM
                } else {
                    type = UserType.STREET
                }

                if (type == UserType.STREET)   //doesn't work with simple currentuser
                    goToMainPage()
                else
                    goToGymPage()


            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
                Toast(this).showCustomToast(this, "Error getting data", isError = true)
            }
        }else{
            Log.d("permission", "withoutAutomataLogin")
            withoutAutomataLogin()
        }

    }

    override fun onLocationChanged(p0: Location) {
        Extensions.currentLocation=p0
    }

}
