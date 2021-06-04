package com.filomenadeveloper.durgente_app.Servicos

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception
import kotlin.collections.HashMap

class FirebaseService {

     var content: Context
    lateinit var downloadUrl : Uri
    constructor(content: Context){
        this.content = content
    }

    fun updateImagemToFirebaseStore(uri:Uri,onCallback: OnCallback){
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("imageChat/"+System.currentTimeMillis()+"_"+getFileExtention(uri))
        storageReference.putFile(uri).addOnCompleteListener { task ->
           // var urltask : Task<Uri> = it.storage.downloadUrl
           if(task.isSuccessful) {
               storageReference.downloadUrl.addOnSuccessListener { ur->
                   val downloaded: String = ur.toString()
                   onCallback.onUpdateLloadSucess(downloaded)
               }
           }
        }.addOnFailureListener(OnFailureListener {
            onCallback.onUpdateLloadFailure(it)

        })
    }

    fun getFileExtention(uri:Uri): String? {
        val contentResolver: ContentResolver=  content.contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    interface OnCallback{
        fun onUpdateLloadSucess(uri: String )
        fun onUpdateLloadFailure(e: Exception)
    }
}