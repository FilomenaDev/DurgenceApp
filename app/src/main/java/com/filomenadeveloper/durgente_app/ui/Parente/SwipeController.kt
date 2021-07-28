package com.filomenadeveloper.durgente_app.ui.Parente

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.filomenadeveloper.durgente_app.R

abstract class SwipeController(context: Context) :
         ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
     private val icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)
     private val intrinsicWidth = icon!!.intrinsicWidth
     private val intrinsicHeight = icon!!.intrinsicHeight
     private val background = ColorDrawable()
     private val backgroundColor = Color.parseColor("#f44336")

     override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
         return false
     }

     override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
         val itemView = viewHolder.itemView
         val itemHeight = itemView.bottom - itemView.top

         // Draw the red delete background

         background.color = backgroundColor
         background.setBounds(
                 itemView.right + dX.toInt(),
                 itemView.top,
                 itemView.right,
                 itemView.bottom
         )
         background.draw(canvas)

         // Calculate position of delete icon
         val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
         val iconMargin = (itemHeight - intrinsicHeight) / 2
         val iconLeft = itemView.right - iconMargin - intrinsicWidth
         val iconRight = itemView.right - iconMargin
         val iconBottom = iconTop + intrinsicHeight

         // Draw the delete icon
         icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
         icon.draw(canvas)
         super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
     }
 }