package com.centrogeo.aplicadorEncuestas.adapters


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.centrogeo.aplicadorEncuestas.R
import com.centrogeo.aplicadorEncuestas.databinding.PreguntaEncuestaBinding
import com.centrogeo.aplicadorEncuestas.model.PregEncuesta
import com.centrogeo.aplicadorEncuestas.model.respuestas.Answer
import com.centrogeo.aplicadorEncuestas.model.respuestas.Answers
import com.centrogeo.aplicadorEncuestas.model.respuestas.Location
import com.centrogeo.aplicadorEncuestas.utilidades.Conexion_db_enc
import com.centrogeo.aplicadorEncuestas.utilidades.Const
import com.google.firebase.firestore.FirebaseFirestore


class AdapterEncuestas( private val PregEncuesta: ArrayList<PregEncuesta>, longitud: String, latitud: String, correo:String, id_encuesta:String): RecyclerView.Adapter<AdapterEncuestas.ViewHolder>(), View.OnClickListener {

    private var longitude=longitud
    private var latitude=latitud
    private var rews: Answers = Answers(ArrayList(),correo, Location(latitude,longitude),id_encuesta)
    private val answer = ArrayList<Answer>()



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding=PreguntaEncuestaBinding.bind(itemView)
        var context = itemView.context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.pregunta_encuesta, parent, false)
        )
    }

    override fun getItemCount() = PregEncuesta.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pregN = PregEncuesta[position]
        holder.binding.Pregunta.text = pregN.title

        val r = RadioGroup(holder.context)
        var checkbox: CheckBox
        val button: Button

        val arrayOptions = pregN.options.split(",")


        //
        //  GenerateCheckButton(arra,holder.context,holder.opciones,checkbox)
        when (pregN.type) {
            "Radio" -> {
                answer.add(Answer("",""))
                generateRadioButton(arrayOptions, holder.context, holder.binding.linerarLbuttonR, r)
                r.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
                    val b = radioGroup.findViewById<RadioButton>(i).text.toString()
                    answer[position]= Answer(b,PregEncuesta[position].title)
                }
            }
            "Checkbutton" -> {
                val opti = ArrayList<String>()
                answer.add(Answer("",PregEncuesta[position].title))
                for (item in arrayOptions.indices) {
                    opti.add((arrayOptions[item]))
                }
                holder.binding.linerarLbuttonR.removeAllViews()
                for (item2 in opti) {
                    checkbox = CheckBox(holder.context)
                    checkbox.text = item2
                    checkbox.setOnCheckedChangeListener { compoundButton: CompoundButton, _: Boolean ->
                        if (compoundButton.isChecked) {
                            if(answer[position].Answer.isBlank()||answer[position].Answer.isEmpty()){
                                answer[position].Answer+="${compoundButton.text}"
                            }else{
                                answer[position].Answer+=",${compoundButton.text}"
                            }
                            Log.i("checkbutton_True",answer[position].Answer)
                        } else {
                            if(answer[position].Answer.startsWith(compoundButton.text)){
                                answer[position].Answer= answer[position].Answer.replace("${compoundButton.text},","",true)
                                if(answer[position].Answer == compoundButton.text){
                                    answer[position].Answer= answer[position].Answer.replace("${compoundButton.text}","",true)
                                }
                            }else{
                                answer[position].Answer= answer[position].Answer.replace(",${compoundButton.text}","",true)
                            }
                            Log.i("checkbutton_False",answer[position].Answer)
                        }
                    }
                    holder.binding.linerarLbuttonR.addView(checkbox)
                }
            }
            "Text" -> {
                val text = EditText(holder.context)
                answer.add(Answer("", PregEncuesta[position].title))
                text.hint = "Escriba su respuesta"
                text.minLines = 5
                text.inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
                text.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                    if ((keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) || (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP)) {
                      text.clearFocus()
                        val tr = text.text.toString()
                        answer[position].Answer = tr
                        Log.i("id",text.id.toString())
                        return@OnKeyListener false
                    }
                    false
                })
                text.addTextChangedListener {
                    val tr = text.text.toString()
                    answer[position].Answer = tr
                    Log.i("id",answer[position].Answer)
                }
                holder.binding.linerarLbuttonR.addView(text)
            }
            "Submit" -> {
                var successFirebaseUpdate = false
                val progresbar = ProgressBar(holder.context)
                progresbar.visibility= View.GONE
                progresbar.foregroundGravity = Gravity.CENTER
                holder.binding.Pregunta.visibility = View.GONE
                button = Button(holder.context)
                button.text = holder.context.getString(R.string.Botton_enviar_encuesta)
                button.setOnClickListener {
                    progresbar.visibility = View.VISIBLE
                    button.visibility = View.GONE
                    rews.Answers = answer
                        if (!verResp(answer)) {
                            val database = FirebaseFirestore.getInstance()
                            database.collection("Respuestas").add(rews)
                                .addOnSuccessListener {
                                    val conn = Conexion_db_enc(holder.context, "db_usuarios", null, 1)
                                    val db: SQLiteDatabase = conn.writableDatabase
                                    db.execSQL("DELETE FROM ${Const.tabla_IdEncuestas} WHERE ${Const.id_encuesta}=='${rews.Survey}'")
                                    db.execSQL("DELETE FROM ${Const.tabla_preguntas} WHERE ${Const.id_encuesta_abreviado}=='${rews.Survey}'")
                                    db.close()
                                    conn.close()
                                    successFirebaseUpdate=true
                                    SendMessage(holder.context)
                                }
                                database.clearPersistence()
                                if(!successFirebaseUpdate){
                                    val conn = Conexion_db_enc(holder.context, "db_usuarios", null, 1)
                                    val db: SQLiteDatabase = conn.writableDatabase
                                    db.execSQL("DELETE FROM ${Const.tabla_Respuestas} WHERE ${Const.id_encuesta_abreviado} = '${rews.Survey}'")
                                    db.execSQL("DELETE FROM ${Const.tabla_RespuestasPendientes} WHERE ${Const.id_encuesta} = '${rews.Survey}'")
                                    for (i in rews.Answers.indices) {
                                        db.execSQL("INSERT INTO Respuestas(RESPUESTA,PREGUNTA,ID_ENC,ID_PREG) VALUES ('${rews.Answers[i].Answer}','${rews.Answers[i].Question}','${rews.Survey}','${rews.Survey}_${i}')")
                                    }
                                    db.execSQL("DELETE FROM ${Const.tabla_IdEncuestas} WHERE ${Const.id_encuesta} == '${rews.Survey}'")
                                    db.execSQL("DELETE FROM ${Const.tabla_preguntas} WHERE ${Const.id_encuesta_abreviado} =='${rews.Survey}'")
                                    db.execSQL("INSERT INTO ${Const.tabla_RespuestasPendientes}(${Const.id_encuesta},${Const.latitud},${Const.longitud},${Const.correo}) VALUES ('${rews.Survey}','${rews.Location.latitude}','${rews.Location.longitude}','${rews.Email}') ")
                                    db.close()
                                    conn.close()
                                    SendMessage(holder.context)
                                }
                        } else {
                            progresbar.visibility = View.GONE
                            button.visibility = View.VISIBLE
                            val builder = AlertDialog.Builder(holder.context)
                            builder.setTitle("Error")
                            builder.setMessage("Hay una pregunta sin responder")
                            builder.setPositiveButton("Aceptar", null)
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }
                }
                holder.binding.linerarLbuttonR.addView(progresbar)
                holder.binding.linerarLbuttonR.addView(button)
            }
        }
    }

    override fun onClick(v: View?) {
    }

    private fun verResp(Ans:ArrayList<Answer>):Boolean{
        for (i in Ans.indices){
            if(Ans[i].Answer.isBlank()||Ans[i].Answer.isEmpty()){
                return true
            }
        }
        return false
    }

    private fun generateRadioButton(
        arra: List<String>,
        context: Context,
        linearLayout: LinearLayout,
        radioG: RadioGroup
    ) {
        val opti = ArrayList<String>()
        for (item2 in arra.indices) {
            opti.add((arra[item2]))
        }

        radioG.orientation = RadioGroup.VERTICAL
        linearLayout.removeAllViews()

        for (item in opti) {
            val r1 = RadioButton(context)
            r1.text = item
            val r12 = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            // r.removeAllViews()
            radioG.addView(r1, r12)
        }
        linearLayout.addView(radioG)
    }

    private fun SendMessage(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Encuesta Enviada")
        builder.setMessage("Encuesta enviada correctamente")
        builder.setPositiveButton("Aceptar"){ _: DialogInterface, _: Int ->
            (context as Activity).finish()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}


