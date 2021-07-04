package com.filomenadeveloper.durgente_app.ui.Historico

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.filomenadeveloper.durgente_app.R
import com.google.firebase.database.*


class HistoricoFragment : Fragment() {

private lateinit var mHistoryDatabase: DatabaseReference

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_historico, container, false)

        mHistoryDatabase

        val spinner: Spinner = root.findViewById(R.id.spinner_sanguino)


        ArrayAdapter.createFromResource(
                context!!,
                R.array.sanguino_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        return root
    }

    private fun getUserInfo() {
        mCustomerDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as Map<String, Any>?
                    if (map!!["name"] != null) {
                        mName = map["name"].toString()
                        mNameField.setText(mName)
                    }
                    if (map["phone"] != null) {
                        mPhone = map["phone"].toString()
                        mPhoneField.setText(mPhone)
                    }
                    if (map["bi"] != null) {
                        mBi = map["bi"].toString()
                        mBiField.setText(mBi)
                    }
                    if (map["alergico"] != null) {
                        mAlergico = map["alergico"].toString()
                        mAlergicoField.setText(mAlergico)
                    }
                    if (map["diabetico"] != null) {
                        mChekbDiabetico.setChecked(true)
                    }
                    if (map["hipertenso"] != null) {
                        mChekHipertenso.setChecked(true)
                    }
                    if (map["Sanguino"] != null) {
                        mSanguino = map["Sanguino"].toString()
                        mSsngue.setText(mSanguino)
                    }
                    if (map["profileImageUrl"] != null) {
                        mProfileImageUrl = map["profileImageUrl"].toString()
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}