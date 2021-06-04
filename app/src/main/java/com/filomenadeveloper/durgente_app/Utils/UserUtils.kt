package com.filomenadeveloper.durgente_app.Utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.filomenadeveloper.durgente_app.Common
import com.filomenadeveloper.durgente_app.Model.TokenModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater

object UserUtils {

    fun updateUser(view: View, updater: Map<String,Any>){
        FirebaseDatabase.getInstance().reference.child(Common.USUARIO).child(Common.CUSTOMER_INFO_REFERENCE)
            .child(FirebaseAuth.getInstance().currentUser.uid)
            .updateChildren(updater).addOnSuccessListener {
                Snackbar.make(view!!,"Informacao atualizada com sucesso",Snackbar.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Snackbar.make(view!!, it.message.toString(),Snackbar.LENGTH_SHORT).show()
            }
    }

    fun updateToken(context: Context, token:String){
        val tokenModel = TokenModel();
        tokenModel.token =token

        FirebaseDatabase.getInstance().getReference(Common.TOKEN_REFERENCE)
            .child(FirebaseAuth.getInstance().currentUser.uid)
            .setValue(token)
            .addOnFailureListener {
                Toast.makeText(context, it.message.toString(),Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {

            }
    }
}