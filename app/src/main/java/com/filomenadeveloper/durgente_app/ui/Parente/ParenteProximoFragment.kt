package com.filomenadeveloper.durgente_app.ui.Parente

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.filomenadeveloper.durgente_app.Common
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

        val swipeHandler = object : SwipeController(context!!){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = mRecyclerView.adapter as Parent_Adapter
                val possion = viewHolder.adapterPosition
                val myParent = adapter.mParent[possion]
                adapter.removeAt(viewHolder.adapterPosition)
                val biuld = AlertDialog.Builder(context)
                biuld.setTitle("Elimina Parente Proxímo").setMessage("Deseja elimina este parente?")
                        .setNegativeButton("Cancela") { dialogInterface, _ -> dialogInterface.dismiss() }
                        .setPositiveButton("Elimina"){ dialogInterface, _ ->
                            val firebaseUser = FirebaseAuth.getInstance().currentUser
                            val reference = FirebaseDatabase.getInstance().getReference(Common.Parents)
                            reference.addValueEventListener(object : ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (snapshot in snapshot.children) {
                                        val parent = snapshot.getValue(Parent::class.java)!!
                                        val childKey: String = snapshot.key!!
                                        if (parent!!.idP == firebaseUser!!.uid && parent!!.phoneP == myParent!!.phoneP ){
                                            reference.child(childKey).removeValue()

                                        }
                                    }
                                }

                            })
                    }.setCancelable(false)
                val dialog = biuld.create()
                dialog.setOnShowListener {
                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(ContextCompat.getColor(context!!,android.R.color.holo_red_dark))
                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!,R.color.colorAccent))
                }
                dialog.show()

            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)

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
                    try {
                        layoutRecyclerView.visibility = View.VISIBLE
                        layoutInicio.visibility = View.GONE
                        adpter = Parent_Adapter( mParent,context!!)
                        mRecyclerView!!.adapter = adpter
                    }catch (exception: KotlinNullPointerException){

                    }


                }else{
                    layoutInicio.visibility = View.VISIBLE
                    layoutRecyclerView.visibility = View.GONE
                }

            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }
}