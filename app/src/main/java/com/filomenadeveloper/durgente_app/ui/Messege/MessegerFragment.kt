package com.filomenadeveloper.durgente_app.ui.Messege

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.filomenadeveloper.durgente_app.Mensagem.Adapters.SectionsPagerAdapter
import com.filomenadeveloper.durgente_app.Mensagem.Fragments.FragmentUsers
import com.filomenadeveloper.durgente_app.Mensagem.Fragments.FragmentChat
import com.filomenadeveloper.durgente_app.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A placeholder fragment containing a simple view.
 */
class MessegerFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_messeger, container, false)
        val sectionsPagerAdapter = SectionsPagerAdapter(context!!, fragmentManager!!)
        val viewPager: ViewPager = root.findViewById(R.id.view_pager)
        sectionsPagerAdapter.AddFragment(FragmentUsers(),"Emergências")
        sectionsPagerAdapter.AddFragment(FragmentChat(),"Conversas")
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = root.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        return root
    }

    private fun getUserData() {
        val driverId = FirebaseAuth.getInstance().currentUser.uid
        val assignedCustomerRef = FirebaseDatabase.getInstance().reference.child("Users").child("Customers").child(driverId)
        assignedCustomerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("name").value != null) {
                    //    val mUsername = findViewById<View>(R.id.usernameChat) as TextView
                      //  mUsername.text = dataSnapshot.child("name").value.toString()
                    }
                    if (dataSnapshot.child("profileImageUrl").value != null) {
                     //   val mProfileImage: ImageView = findViewById<View>(R.id.imageViewChat) as ImageView
                       // Glide.with(application).load(dataSnapshot.child("profileImageUrl").value.toString()).apply(RequestOptions.circleCropTransform()).into(mProfileImage)
                    }
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }
}