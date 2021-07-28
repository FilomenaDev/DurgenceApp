package com.filomenadeveloper.durgente_app.ui.Parente

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.filomenadeveloper.durgente_app.R

class Parent_Adapter (val mParent: ArrayList<Parent>, val mContext: Context): RecyclerView.Adapter<Parent_Adapter.ViewHolder>() {

    fun removeAt(position: Int) {
        mParent.removeAt(position)
        notifyItemRemoved(position)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(mContext).inflate(R.layout.item_parent, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return mParent.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parent : Parent = mParent[position]
       holder.nome.text = parent.nameP
        holder.phone.text = parent.phoneP

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chat_imagem: ImageView = itemView.findViewById(R.id.imageViewParent)
        var nome : TextView = itemView.findViewById(R.id.parentname)
        var phone : TextView = itemView.findViewById(R.id.parentphone)

    }

}