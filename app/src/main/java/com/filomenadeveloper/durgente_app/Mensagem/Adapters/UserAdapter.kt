package com.filomenadeveloper.durgente_app.Mensagem.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.filomenadeveloper.durgente_app.Mensagem.ActivityMenssagem
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import com.filomenadeveloper.durgente_app.Model.Orgao
import com.filomenadeveloper.durgente_app.R
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class UserAdapter  (val mUser: ArrayList<Orgao>,val mContext:Context): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    val MSG_TYPE_LEFT = 0
    val MSG_TYPE_RIGHT = 1
    private lateinit var  mLayoutInflater: LayoutInflater
    private lateinit var firebaseUser: FirebaseUser


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imagem: ImageView = itemView.findViewById(R.id.imageViewUser);
        var nome: TextView = itemView.findViewById(R.id.username);

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_contatos, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Orgao = mUser[position]
        holder.nome.text = user.number
        if (user.avatar.equals("default")) {
            holder.imagem.setImageResource(R.mipmap.ic_launcher_round)
        } else {
            Glide.with(mContext).load(user.avatar).into(holder.imagem)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ActivityMenssagem::class.java)
            intent.putExtra("userid", user.id)
            mContext.startActivity(intent)
        }
    }


}