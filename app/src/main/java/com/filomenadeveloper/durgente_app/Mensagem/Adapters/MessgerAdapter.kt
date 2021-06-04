package com.filomenadeveloper.durgente_app.Mensagem.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.filomenadeveloper.durgente_app.Mensagem.Models.Chat
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import com.filomenadeveloper.durgente_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MessgerAdapter (val mChat: ArrayList<Chat>,val mContext: Context,val imagurl: String): RecyclerView.Adapter<MessgerAdapter.ViewHolder>() {

    val MSG_TYPE_LEFT = 0
    val MSG_TYPE_RIGHT = 1
    private lateinit var  mLayoutInflater: LayoutInflater
   private lateinit var firebaseUser: FirebaseUser


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var chat_imagem: ImageView = itemView.findViewById(R.id.foto_chat);
        var show_msg: TextView = itemView.findViewById(R.id.show_meng);
        var loyout_text: LinearLayout = itemView.findViewById(R.id.layout_text)
        var loyout_imagem: LinearLayout = itemView.findViewById(R.id.layout_imagens)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType === MSG_TYPE_RIGHT) {
            val v: View = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
            ViewHolder(v)
        } else {
            val v: View = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
            ViewHolder(v)
        }
    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chats: Chat = mChat[position]
        if(chats.type == "IMAGE"){
            holder.loyout_imagem.visibility = View.VISIBLE
            holder.loyout_text.visibility = View.GONE
            Glide.with(mContext).load(chats.uri).into(holder.chat_imagem)

            }else {
            holder.loyout_imagem.visibility = View.GONE
            holder.loyout_text.visibility = View.VISIBLE
            holder.show_msg.text = chats.mensagem
        }

    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (mChat.get(position)!!.sender == firebaseUser.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

}