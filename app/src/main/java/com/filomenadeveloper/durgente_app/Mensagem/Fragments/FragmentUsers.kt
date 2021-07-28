package com.filomenadeveloper.durgente_app.Mensagem.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.filomenadeveloper.durgente_app.R
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.filomenadeveloper.durgente_app.Common
import com.filomenadeveloper.durgente_app.Mensagem.Adapters.UserAdapter
import com.filomenadeveloper.durgente_app.Mensagem.Models.ChatList
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import com.filomenadeveloper.durgente_app.Model.Orgao
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class FragmentUsers : Fragment() {

   private lateinit var v: View
    private lateinit var uselist: List<ChatList>
    private var mUser: ArrayList<Orgao> = ArrayList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adpter: UserAdapter
    private lateinit var reference: DatabaseReference
    private lateinit var fireUser: FirebaseUser



    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.activity_chat, container, false)
        mRecyclerView = v.findViewById(R.id.recyclerViewChat)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        mUser = ArrayList()
        LerUsuarios()
        return v
    }

    private fun LerUsuarios() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child(Common.USUARIO).child(Common.CUSTOMER_INFO_ORGAO)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                mUser!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(Orgao::class.java)!!
                    assert(firebaseUser != null)
                    if (!user.id.equals(firebaseUser!!.uid)) { //erro
                        mUser.add(user)
                    }
                }
                adpter = UserAdapter( mUser,context!!)
                mRecyclerView!!.adapter = adpter
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }
}