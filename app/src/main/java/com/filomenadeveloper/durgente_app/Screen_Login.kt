package com.filomenadeveloper.durgente_app

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.*
import java.util.concurrent.TimeUnit

class Screen_Login : AppCompatActivity() {
    private lateinit var btn_login: Button
    private lateinit var edit_number: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_login)

        edit_number = findViewById(R.id.editTextNumber)
        btn_login = findViewById(R.id.button_verificar)
        var number = edit_number!!.text

    }
}