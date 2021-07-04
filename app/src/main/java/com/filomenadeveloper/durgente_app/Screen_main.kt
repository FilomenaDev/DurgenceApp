package com.filomenadeveloper.durgente_app

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.filomenadeveloper.durgente_app.Utils.UserUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.lang.NullPointerException
import java.net.URI

class Screen_main : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var waitingDialogo: AlertDialog
    private lateinit var image_avatar: ImageView
    private lateinit var storageReference: StorageReference
    lateinit var resultUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

         drawerLayout = findViewById(R.id.drawer_layout)
         navView = findViewById(R.id.nav_view)
         navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.nav_inicio, R.id.nav_historico, R.id.nav_menssagem,R.id.nav_parentes
                ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        init()
    }

    private fun init() {
        storageReference = FirebaseStorage.getInstance().reference
        waitingDialogo = AlertDialog.Builder(this).setMessage("Processando")
                .setCancelable(false)
                .create()

        val headeview = navView.getHeaderView(0)
        val text_name = headeview.findViewById<View>(R.id.text_name) as TextView
        val text_Phone = headeview.findViewById<View>(R.id.text_phone) as TextView
        image_avatar = headeview.findViewById<View>(R.id.imageView_avatar) as ImageView
try {
    text_name.text = Common.biuldwelcameMessage()
    text_Phone.text = Common.correntUser!!.phoneNumber
}catch (e: NullPointerException){

}


        if (Common.correntUser != null && Common.correntUser!!.avatar != null && !TextUtils.isEmpty(Common.correntUser!!.avatar))
            Glide.with(this).load(Common.correntUser!!.avatar).into(image_avatar)

        image_avatar.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
            showDialogUpload()
        })
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
             var imageUri: Uri = data!!.data!!
             resultUri = imageUri
             try {
                 val bitmap =
                         MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                 Glide.with(application)
                         .load(bitmap) // Uri of the picture
                         .apply(RequestOptions()).into(image_avatar)
             } catch (e: IOException) {
                 e.printStackTrace()
             }

         }
     }

    private fun showDialogUpload(){
        val builder = AlertDialog.Builder(this@Screen_main)

        builder.setTitle("Imagem de Perfil").setMessage("Deseja alterar foto do perfil")
                .setNegativeButton("Cancela") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton("Mudar"){ dialogInterface, _ ->
                    if (resultUri != null){

                        val avatarFolder = storageReference.child("avatars/"+FirebaseAuth.getInstance().currentUser!!.uid)
                        avatarFolder.putFile(resultUri!!)
                                .addOnFailureListener { e ->
                                    Snackbar.make(drawerLayout,e.message!!,Snackbar.LENGTH_LONG).show()
                                }.addOnCompleteListener { task ->

                                    if (task.isSuccessful){
                                        avatarFolder.downloadUrl.addOnSuccessListener { uri->
                                            val update_data = HashMap<String,Any>()
                                            update_data.put("avatar",uri.toString())
                                            UserUtils.updateUser(drawerLayout,update_data)
                                        }
                                    }
                                    waitingDialogo.dismiss()
                                }.addOnProgressListener { taskSnapshot ->
                                    val progress = (100.0*taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                                    waitingDialogo.setMessage(StringBuilder("Atualizar ").append(progress).append("%"))
                                }
                    }
                }.setCancelable(false)
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this@Screen_main,android.R.color.holo_red_dark))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this@Screen_main,R.color.colorAccent))
        }
        dialog.show()

    }

    companion object{
        val PICK_IMAGE_REQUEST = 7272
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_settings){
            val build = AlertDialog.Builder(this@Screen_main)
            build.setTitle("Sair").setMessage("Deseja sair do Aplicativo ?")
                    .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss()  }
                    .setPositiveButton("Sair"){dialog, _ ->
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@Screen_main,SplashScreenActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }.setCancelable(false)
            val dialogo = build.create()
            dialogo.setOnShowListener {
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(resources.getColor(android.R.color.holo_red_dark))
                dialogo.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(resources.getColor(R.color.colorAccent))

            }
            dialogo.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
      val n=  menuInflater.inflate(R.menu.screen_main, menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}