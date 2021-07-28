package com.filomenadeveloper.durgente_app.Mensagem.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.filomenadeveloper.durgente_app.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.filomenadeveloper.durgente_app.Common
import com.filomenadeveloper.durgente_app.Mensagem.Adapters.UserAdapter
import com.filomenadeveloper.durgente_app.Mensagem.Models.ChatList
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import com.filomenadeveloper.durgente_app.Model.Orgao
import com.filomenadeveloper.durgente_app.Utils.UserUtils
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId


class FragmentChat: Fragment() {

    private lateinit var v: View
    private var mUser: ArrayList<Orgao> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adpter: UserAdapter
    private lateinit var fireUser: FirebaseUser
    private lateinit  var reference: DatabaseReference
    private var userlist: ArrayList<ChatList> = ArrayList()


    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.activity_user, container, false)
        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        fireUser = FirebaseAuth.getInstance().currentUser
        userlist = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("chatList").child(fireUser.uid)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                userlist!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val chatList: ChatList = snapshot.getValue(ChatList::class.java)!!
                    userlist!!.add(chatList)
                }
                ChatList()
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
        UserUtils.updateToken(requireContext(), FirebaseInstanceId.getInstance().token!!)

        return v
    }


    private fun ChatList() {
        mUser = ArrayList()
        reference = FirebaseDatabase.getInstance().reference.child(Common.USUARIO).child(Common.CUSTOMER_INFO_ORGAO)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                mUser.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(Orgao::class.java)!!
                    for (chatList:ChatList in userlist) {
                        if (user.id == chatList.id) {
                            mUser.add(user)

                        }
                    }
                }
                adpter = UserAdapter(mUser,context!!)
                recyclerView!!.adapter = adpter
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    /*  private void readChat(){
        mUser = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Usuarios");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    //disply 1 user no chat
                    for(String id : userlist){
                        if(user.getId().equals(id)){
                            if(mUser.size() != 0){
                                for(User user1 : mUser){
                                    if(!user.getId().equals(user1.getId())){
                                        mUser.add(user);
                                    }
                                }
                            }else {
                                mUser.add(user);
                            }
                        }
                    }
                }
                adpter = new UserAdapter(getContext(),mUser);
                recyclerView.setAdapter(adpter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}