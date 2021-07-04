package com.filomenadeveloper.durgente_app.ui.Parente

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.filomenadeveloper.durgente_app.Common
import com.filomenadeveloper.durgente_app.Mensagem.Adapters.UserAdapter
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import com.filomenadeveloper.durgente_app.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ParenteProximoFragment : Fragment() {

    private var mParent: ArrayList<Parent> = ArrayList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adpter: Parent_Adapter
    private lateinit var reference: DatabaseReference
    private lateinit var layoutRecyclerView : LinearLayout
    private lateinit var layoutInicio : LinearLayout
    private lateinit var fireUser: FirebaseUser
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_parentes, container, false)
        val floatingButton: FloatingActionButton = root.findViewById(R.id.btn_addParent)
        layoutRecyclerView  = root.findViewById(R.id.infoRecicleview)
        layoutInicio  = root.findViewById(R.id.infoInicio)
        mRecyclerView = root.findViewById(R.id.recycle_parents)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)

        floatingButton.setOnClickListener {
            val intent  = Intent(context,AddParente::class.java)
            startActivity(intent)

        }

        mParent = ArrayList()
        lerParent()


        return root
    }

    private fun lerParent(){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference(Common.Parents)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                mParent!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val parent = snapshot.getValue(Parent::class.java)!!
                    if (parent!!.idP == firebaseUser!!.uid){
                        mParent.add(parent)

                    }
                }
                if (mParent.size > 0){
                    layoutRecyclerView.visibility = View.VISIBLE
                    layoutInicio.visibility = View.GONE
                    adpter = Parent_Adapter( mParent,context!!)
                    mRecyclerView!!.adapter = adpter
                }else{
                    layoutInicio.visibility = View.VISIBLE
                    layoutRecyclerView.visibility = View.GONE
                }

            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }
}