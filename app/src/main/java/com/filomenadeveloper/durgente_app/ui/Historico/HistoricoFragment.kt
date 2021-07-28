package com.filomenadeveloper.durgente_app.ui.Historico

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.filomenadeveloper.durgente_app.Common
import com.filomenadeveloper.durgente_app.Mensagem.Models.Chat
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import com.filomenadeveloper.durgente_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class HistoricoFragment : Fragment() {

private lateinit var mHistoryDatabase: DatabaseReference
    private lateinit var patologia: EditText
    private lateinit var hipert_check: CheckBox
    private lateinit var diabet_check: CheckBox
    private lateinit var alergia_check: CheckBox
    private var hiper:String =""
    private  var diab:String = ""
    private  var alerg:String = ""
    private  var mPatolog:String = ""
    private  var mSanguino:String = ""
    private lateinit var btn_salve: Button
    private lateinit var btn_cancelar: Button
    private lateinit var layout: ConstraintLayout
    private lateinit var spinner: Spinner
    private lateinit var database: FirebaseDatabase
    private lateinit var customerStoryReeq: DatabaseReference
    private lateinit var adapter: ArrayAdapter<CharSequence>


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_historico, container, false)

        layout = root.findViewById(R.id.view_story)
         spinner = root.findViewById(R.id.spinner_sanguino)
        patologia = root.findViewById(R.id.patologia)
        hipert_check = root.findViewById(R.id.checkboxHiper)
        diabet_check = root.findViewById(R.id.checkboxDiabetico)
        alergia_check = root.findViewById(R.id.checkboxAlergico)
        btn_salve = root.findViewById(R.id.btn_save_historico)
        btn_cancelar = root.findViewById(R.id.btn_cancell_historico)
        database = FirebaseDatabase.getInstance()
        customerStoryReeq = database.reference.child(Common.CUSTOMER_STORY_REFERENCE).child(FirebaseAuth.getInstance().currentUser.uid)

        adapter =ArrayAdapter.createFromResource(context!!, R.array.sanguino_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapter

        evento(layout)
        getUserInfo()
        btn_salve.setOnClickListener {
            SalveStory()
        }


        return root
    }

    fun evento(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkboxHiper -> {
                    if (checked) {
                        hiper = "Sim"
                    } else {
                        hiper = "Nao"
                    }
                }
                R.id.checkboxAlergico -> {
                    if (checked) {
                        alerg = "Sim"
                    } else {
                        alerg = "nao"
                    }
                }
                R.id.checkboxDiabetico ->{
                    if (checked) {
                        diab = "Sim"
                    } else {
                        diab = "nao"
                    }
                }
                // TODO: Veggie sandwich
            }
        }
    }
    private fun SalveStory(){
        val model = Story_Pacient()
        model.sanguino = spinner.selectedItem.toString()
        model.patologia = patologia.text.toString()
        model.hipertenso = hiper
        model.alergico = alerg
        model.diabtico = diab
        model.idSP = FirebaseAuth.getInstance().currentUser.uid

        customerStoryReeq.setValue(model)
                .addOnFailureListener { e->
                    val progressbar: ProgressDialog = ProgressDialog(context)
                    progressbar.setMessage("Enviando Imagem")
                    progressbar.show()
                }
                .addOnSuccessListener {
                    Toast.makeText(context,"Salvo com Sucesso",Toast.LENGTH_SHORT).show()

                }
    }

 private fun getUserInfo() {
  customerStoryReeq.addValueEventListener(object: ValueEventListener{
      override fun onCancelled(error: DatabaseError) {
          TODO("Not yet implemented")
      }

      override fun onDataChange(snapshot: DataSnapshot) {
          if (snapshot.exists() ){
              val story: Story_Pacient = snapshot.getValue(Story_Pacient::class.java)!!
              patologia.setText(story.patologia)
              spinner.setSelection(adapter.getPosition(story.sanguino))

          }
      }
  })
 }
}