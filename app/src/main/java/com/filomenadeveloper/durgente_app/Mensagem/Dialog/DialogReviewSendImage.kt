package com.filomenadeveloper.durgente_app.Mensagem.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import com.filomenadeveloper.durgente_app.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jsibbold.zoomage.ZoomageView
import java.util.*
class DialogReviewSendImage(
        var content: Context,
        var bitmap: Bitmap,
        var dialog: Dialog
        ){

    constructor(content: Context, bitmap: Bitmap) : this(
            content,
            bitmap,
            dialog = Dialog(content)
    ){
        initalize()
    }


    private lateinit var image: ZoomageView
    private lateinit var flagBtn: FloatingActionButton

    fun initalize(){
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR)
        dialog.setContentView(R.layout.activity_review_send_image)
        Objects.requireNonNull(dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)))
        dialog.setCancelable(true)
        val lp : WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(Objects.requireNonNull(dialog.window!!.attributes))
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes =lp

         image  = dialog.findViewById(R.id.image_zoom)
         flagBtn  = dialog.findViewById(R.id.btn_toSend)
    }

    fun show(onCallbacks: OnCallback ) {
        dialog.show()
        image.setImageBitmap(bitmap)
        flagBtn.setOnClickListener(View.OnClickListener {
           onCallbacks.onButtonSendClink()
            dialog.dismiss()
        })

  }
    interface OnCallback{
        fun onButtonSendClink()
    }
}
