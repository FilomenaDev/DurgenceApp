package com.filomenadeveloper.durgente_app

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import com.filomenadeveloper.durgente_app.Utils.UserUtils
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit

class SplashScreenActivity : AppCompatActivity() {

    companion object{
        private val LOGIN_REQUEST_CODE = 7171
    }

    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener

    private lateinit var database: FirebaseDatabase
    private lateinit var customerInfoReeq: DatabaseReference
    private lateinit var progress_bar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_splash)

      //  progress_bar = findViewById(R.id.progress_bar)



        init()

    }

    private fun init() {
        database = FirebaseDatabase.getInstance()
        customerInfoReeq = database.getReference(Common.USUARIO)
       providers = Arrays.asList(AuthUI.IdpConfig.PhoneBuilder().build(),
       AuthUI.IdpConfig.GoogleBuilder().build())

        firebaseAuth = FirebaseAuth.getInstance()
        listener = FirebaseAuth.AuthStateListener { myFirebaseAuth ->
            val user = myFirebaseAuth.currentUser
            if (user != null){

                FirebaseInstanceId.getInstance().instanceId
                    .addOnFailureListener {
                        Toast.makeText(this@SplashScreenActivity,""+it.message,Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { instanceIdResult ->
                        Log.d("Token: ",instanceIdResult.token)
                        UserUtils.updateToken(this@SplashScreenActivity,instanceIdResult.token)
                    }

              checkUserFirebase()
            }else{
                startActivity(Intent(this, MainActivity::class.java))

            }
        }
    }

    private fun checkUserFirebase() {
        customerInfoReeq.child(Common.CUSTOMER_INFO_REFERENCE).child(FirebaseAuth.getInstance().currentUser.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SplashScreenActivity,""+error.message,Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                      //  Toast.makeText(this@SplashScreenActivity,"Este usuario ja existe",Toast.LENGTH_SHORT).show()
                        val model = snapshot.getValue(CustomerInfo ::class.java)
                        goToHomeActivity(model)
                    }else{
                       // showRegisterLayout()
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    }
                }

            })
    }

    private fun goToHomeActivity(model: CustomerInfo?) {
        Common.correntUser = model
        startActivity(Intent(this, Screen_main::class.java))
        finish()

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_REQUEST_CODE){
            val request = IdpResponse.fromResultIntent(data)
            if (requestCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
            }else{
                Toast.makeText(this@SplashScreenActivity,""+request!!.error!!.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        delaySplashScreen()
    }

    override fun onStop() {
        if (firebaseAuth != null && listener != null) firebaseAuth.removeAuthStateListener(listener)
        super.onStop()
    }

    private fun delaySplashScreen() {
        Completable.timer(3,TimeUnit.SECONDS,AndroidSchedulers.mainThread())
            .subscribe {
                firebaseAuth.addAuthStateListener(listener)
            }
    }

}