package com.filomenadeveloper.durgente_app

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import com.filomenadeveloper.durgente_app.Utils.UserUtils
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var btn_login_phone: Button

    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var database: FirebaseDatabase
    private lateinit var customerInfoReeq: DatabaseReference
    private lateinit var progress_bar: ProgressBar
    private lateinit var user_id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_login_phone = findViewById(R.id.btn_phone_sign_in)

        btn_login_phone.setOnClickListener(View.OnClickListener {
            //startActivity(Intent(this, Screen_Login::class.java))
            ShowLoginLayout()
        })

        init()
    }

    private fun init() {
        database = FirebaseDatabase.getInstance()
        customerInfoReeq = database.reference
        providers = Arrays.asList(AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())
        firebaseAuth = FirebaseAuth.getInstance()
        listener = FirebaseAuth.AuthStateListener { myFirebaseAuth ->
            val user = myFirebaseAuth.currentUser
            if (user != null){

                FirebaseInstanceId.getInstance().instanceId
                        .addOnFailureListener {
                            Toast.makeText(this@MainActivity,""+it.message,Toast.LENGTH_SHORT).show()
                        }.addOnSuccessListener { instanceIdResult ->
                           // Log.d("Token: ",instanceIdResult.token)
                            UserUtils.updateToken(this@MainActivity,instanceIdResult.token)
                        }


            }
        }
    }

    private fun checkUserFirebase() {
        customerInfoReeq.child(Common.USUARIO).child(Common.CUSTOMER_INFO_REFERENCE).child(FirebaseAuth.getInstance().currentUser.uid)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MainActivity,""+error.message,Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            //  Toast.makeText(this@SplashScreenActivity,"Este usuario ja existe",Toast.LENGTH_SHORT).show()
                            val model = snapshot.getValue(CustomerInfo ::class.java)
                            goToHomeActivity(model)
                        }else{
                             showRegisterLayout(user_id)
                        }
                    }

                })
    }

    private fun ShowLoginLayout() {

        val dialogNamber = Dialog(this@MainActivity)
        dialogNamber.setContentView(R.layout.screen_login)
        var edit_number = dialogNamber.findViewById(R.id.editTextNumber)as EditText
        var btn_login = dialogNamber.findViewById(R.id.button_verificar)as Button
        var number = edit_number!!.text

        btn_login.setOnClickListener(View.OnClickListener {
            if (number.isEmpty()) {
                Toast.makeText(this@MainActivity, "Por favor insere o seu numero de telefone", Toast.LENGTH_SHORT).show()
            }else{
                firebaseAuth.languageCode = "pt"
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+244$number",60, TimeUnit.SECONDS,this,
                        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                singInUser(credential)
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                Toast.makeText(this@MainActivity,"Falhas"+e.localizedMessage, Toast.LENGTH_LONG).show()
                            }

                            override fun onCodeSent(verificarId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                                super.onCodeSent(verificarId, forceResendingToken)

                                val dialog = Dialog(this@MainActivity)
                                dialog.setContentView(R.layout.verify_popup)
                                val edit_verifyCode = dialog.findViewById(R.id.editTextVerify) as EditText
                                val btn_verifyCode = dialog.findViewById(R.id.btn_verifyCode) as Button
                                btn_verifyCode.setOnClickListener(View.OnClickListener {
                                    val verifyCode = edit_verifyCode.text.toString()
                                    if (verificarId.isEmpty())
                                        return@OnClickListener

                                    //crear credencial
                                    var credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificarId ,verifyCode)
                                    singInUser(credential)
                                })
                                dialogNamber.dismiss()
                                dialog.show()
                            }
                        })

            }
        })

        dialogNamber.show()

    }

    private fun singInUser(credential: PhoneAuthCredential){
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful){
                        user_id = firebaseAuth.currentUser.uid
                        checkUserFirebase()
                    }else{
                        Toast.makeText(this@MainActivity,"OPT"+it.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                })

    }

    private fun goToHomeActivity(model: CustomerInfo?) {
        Common.correntUser = model
        startActivity(Intent(this, Screen_main::class.java))
        finish()

    }

    private fun showRegisterLayout(userId : String) {
        val builde = AlertDialog.Builder(this,R.style.Theme_AppCompat_Dialog)
        val itemview = LayoutInflater.from(this).inflate(R.layout.layout_register,null)

        val edit_frist_name = itemview.findViewById(R.id.edit_name) as EditText
        val edit_last_name = itemview.findViewById(R.id.edit_sobrename) as EditText
        val edit_phone_number = itemview.findViewById(R.id.edit_phone_number) as EditText
        val btn_continuo = itemview.findViewById(R.id.register) as Button

        //set data

        if (FirebaseAuth.getInstance().currentUser!!.phoneNumber != null &&
                !TextUtils.isDigitsOnly(FirebaseAuth.getInstance().currentUser!!.phoneNumber))
            edit_phone_number.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)

        //View
        builde.setView(itemview)
        val dialog = builde.create()
        dialog.show()

        // Event
        btn_continuo.setOnClickListener {
            if (TextUtils.isDigitsOnly(edit_frist_name.text.toString())){
                Toast.makeText(this@MainActivity,"Por favor insere o seu nome",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (TextUtils.isDigitsOnly(edit_last_name.text.toString())){
                Toast.makeText(this@MainActivity,"Por favor insere o seu sobrenome",Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }else if (TextUtils.isDigitsOnly(edit_phone_number.text.toString())){
                Toast.makeText(this@MainActivity,"Por favor insere o seu numero de telefone",Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }else{
                val model = CustomerInfo()
                model.firstName = edit_frist_name.text.toString()
                model.lastName = edit_last_name.text.toString()
                model.phoneNumber = edit_phone_number.text.toString()
                model.id = userId

                customerInfoReeq.child(Common.USUARIO).child(Common.CUSTOMER_INFO_REFERENCE).child(FirebaseAuth.getInstance().currentUser.uid)
                        .setValue(model)
                        .addOnFailureListener { e->
                            Toast.makeText(this@MainActivity,""+e.message,Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            progress_bar.visibility = View.GONE
                        }
                        .addOnSuccessListener {
                            Toast.makeText(this@MainActivity,"Registrado com Sucesso",Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            goToHomeActivity(model)
                        }
            }
        }
    }

    override fun onStop() {
        if (firebaseAuth != null && listener != null) firebaseAuth.removeAuthStateListener(listener)
        super.onStop()
    }

}