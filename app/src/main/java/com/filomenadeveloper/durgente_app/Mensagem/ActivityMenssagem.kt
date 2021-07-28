package com.filomenadeveloper.durgente_app.Mensagem

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.filomenadeveloper.durgente_app.Common
import com.filomenadeveloper.durgente_app.Mensagem.Adapters.MessgerAdapter
import com.filomenadeveloper.durgente_app.Mensagem.Dialog.DialogReviewSendImage
import com.filomenadeveloper.durgente_app.Mensagem.Dialog.DialogReviewSendImage.OnCallback
import com.filomenadeveloper.durgente_app.Mensagem.Models.Chat
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import com.filomenadeveloper.durgente_app.Model.Orgao
import com.filomenadeveloper.durgente_app.R
import com.filomenadeveloper.durgente_app.Servicos.FirebaseService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ActivityMenssagem : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var text_enviar: EditText
    private lateinit var btn_enviar: FloatingActionButton
    private lateinit var btn_enviar_image: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var fireUser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private var dados = FirebaseDatabase.getInstance().reference
    private lateinit var userid: String
    private lateinit var resultUri: Uri
    private var IMAGE_GALLERY_REQUEST: Int = 111
    private lateinit var messegerAdpter: MessgerAdapter
    var mChat: ArrayList<Chat> = ArrayList()
    private var notify = false

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)
        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }
        imageView = findViewById(R.id.imageView1)
        textView = findViewById(R.id.user)
        recyclerView = findViewById(R.id.text_mensag)
        text_enviar = findViewById(R.id.text_send)
        btn_enviar = findViewById(R.id.btn_send)
        btn_enviar_image = findViewById(R.id.send_image)

        // apiService = cliente.getCliente("https://fcm.googleapis.com").create(ApiService.class);
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        intent = intent
        userid = intent.getStringExtra("userid")!!
        fireUser = FirebaseAuth.getInstance().currentUser


        btn_enviar.setOnClickListener {
            notify = true
            val msg = text_enviar.text.toString()
            if (msg != "") {
                SendMenssagem(fireUser.uid, userid, msg)
            } else {
                Toast.makeText(
                    this@ActivityMenssagem,
                    "Nao pode enviar menssagem vazia",
                    Toast.LENGTH_LONG
                ).show()
            }
            text_enviar.setText("")
        }

        reference = FirebaseDatabase.getInstance().reference.child(Common.USUARIO).child(Common.CUSTOMER_INFO_ORGAO)
            .child(userid!!)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(Orgao::class.java)!!
                textView.setText(user.number)
                if (user.avatar == "default") {
                    imageView.setImageResource(R.mipmap.ic_launcher_round)
                } else {
                    Glide.with(this@ActivityMenssagem).load(user.avatar).into(imageView)
                }
                readerMessager(fireUser.uid, userid, user.avatar)
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })

        btn_enviar_image.setOnClickListener(View.OnClickListener {
            OpenGallery()
        })
    }

    fun SendMenssagem(sender: String, receive: String, menssagem: String) {
        val chat = Chat()
        chat.dateTime = getCurrentDate()
        chat.uri = ""
        chat.mensagem = menssagem.toString()
        chat.type = "TEXT"
        chat.sender = fireUser.uid
        chat.receive = userid
        dados.child("chats").push().setValue(chat)


        // Adicionar usuario do chat fragment
        val chatRef = FirebaseDatabase.getInstance().getReference("chatList")
            .child(fireUser!!.uid).child(userid!!)
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid)
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
        reference = FirebaseDatabase.getInstance().reference.child(Common.USUARIO).child(Common.CUSTOMER_INFO_REFERENCE)
            .child(fireUser!!.uid)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(CustomerInfo::class.java)!!
                if (notify) {
                    // sendNotification(receive, user.getNome(), smg);
                }
                notify = false
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }


    fun readerMessager(myid: String, userid: String, imagemurl: String) {
        reference = FirebaseDatabase.getInstance().getReference("chats")
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                mChat.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat: Chat = snapshot.getValue(Chat::class.java)!!
                    if (chat.receive == myid && chat.sender == userid ||
                        chat.receive == userid && chat.sender == myid
                    ) {
                        mChat.add(chat)
                    }
                    messegerAdpter = MessgerAdapter(mChat, this@ActivityMenssagem)
                    recyclerView!!.adapter = messegerAdpter
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    fun sendImage(image: String) {
        val chat = Chat()
        chat.dateTime = getCurrentDate()
        chat.uri = image
        chat.mensagem = "null"
        chat.type = "IMAGE"
        chat.sender = fireUser.uid
        chat.receive = userid
        dados.child("chats").push().setValue(chat).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun OpenGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(intent, "Selecionar a Imagem"),
            IMAGE_GALLERY_REQUEST
        )
    }

    fun reviewImage(bitmap: Bitmap) {
        DialogReviewSendImage(this@ActivityMenssagem, bitmap).show(object :
                OnCallback {
            override fun onButtonSendClink() {
                if (resultUri != null) {
                    val progressbar: ProgressDialog = ProgressDialog(this@ActivityMenssagem)
                    progressbar.setMessage("Enviando Imagem")
                    progressbar.show()

                    FirebaseService(this@ActivityMenssagem).updateImagemToFirebaseStore(
                            resultUri,
                            object :
                                    FirebaseService.OnCallback {
                                override fun onUpdateLloadSucess(uri: String) {
                                    sendImage(uri)
                                    progressbar.dismiss()
                                }

                                override fun onUpdateLloadFailure(e: Exception) {
                                    TODO("Not yet implemented")
                                }
                            })

                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            var imageUri: Uri = data!!.data!!
                resultUri = imageUri
                    try{
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                    reviewImage(bitmap)
                 } catch (e: IOException) {
                     e.printStackTrace()
                 }
            }
        }


    fun getCurrentDate(): String {
        val data: Date = Calendar.getInstance().time
        val formatter: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val tody: String = formatter.format(data)

        val currentDataTime: Calendar = Calendar.getInstance()
        val hora: SimpleDateFormat = SimpleDateFormat("hh:mm a")
        val currentTime: String = hora.format(currentDataTime.time)
        return "$tody $currentTime"
    }


}