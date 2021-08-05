package com.filomenadeveloper.durgente_app.ui.Parente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentTransaction
import com.filomenadeveloper.durgente_app.Common
import com.filomenadeveloper.durgente_app.MainActivity
import com.filomenadeveloper.durgente_app.R
import com.filomenadeveloper.durgente_app.Utils.UserUtils
import com.firebase.ui.auth.AuthUI
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

class AddParente: AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var ParentsInfo: DatabaseReference
    private lateinit var nome : EditText
    private lateinit var phone : EditText
    private lateinit var adrecces : EditText
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parent)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        spinner = findViewById(R.id.spinner_grau_parent)
        val btn_cancel : Button = findViewById(R.id.cancel_contact)
        val btn_salvar : Button = findViewById(R.id.btn_salveP)
        nome  = findViewById(R.id.nomeP)
        phone = findViewById(R.id.telefoneP)
        adrecces = findViewById(R.id.adreccess)
        val outlinedTextField: TextInputLayout = findViewById(R.id.outlinedTextField)

        val inputText = outlinedTextField.editText?.text.toString()

        outlinedTextField.editText?.doOnTextChanged { _, _, _, _ -> }

        btn_salvar.setOnClickListener {
            SaveParents()
        }

        btn_cancel.setOnClickListener {
           BlackFragment()

        }

        ArrayAdapter.createFromResource(
                this,
                R.array.grauparentesco_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    private fun SaveParents() {
        val parent = Parent()
        parent.nameP = nome.text.toString()
        parent.phoneP = phone.text.toString()
        parent.adressP = adrecces.text.toString()
        parent.grauP = spinner.selectedItem.toString()
        parent.idP = FirebaseAuth.getInstance().currentUser.uid

        ParentsInfo = FirebaseDatabase.getInstance().reference.child(Common.Parents)
        ParentsInfo.push().setValue(parent).addOnFailureListener {

        }.addOnSuccessListener {
            Toast.makeText(this@AddParente,"Registrado com Sucesso",Toast.LENGTH_SHORT).show()
            BlackFragment()
        }

    }

    private fun BlackFragment(){
        try {
            var a = supportFragmentManager.beginTransaction()
            var fr = ParenteProximoFragment()
            a.replace(R.id.addParent,fr)
            a.addToBackStack(null)
            finish()
            a.commit()

        }catch (e:Exception){

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}