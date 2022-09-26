package com.centrogeo.aplicadorEncuestas.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.centrogeo.aplicadorEncuestas.R
import com.centrogeo.aplicadorEncuestas.VistaEncuesta
import com.centrogeo.aplicadorEncuestas.databinding.ItemEncuestaBinding
import com.example.AplicadorEncuestasCG.model.EncuestaI


class AdapterInsert(private val encuesta:ArrayList<EncuestaI>):RecyclerView.Adapter<AdapterInsert.ViewHolder>(), View.OnClickListener {

   class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener {
       val binding = ItemEncuestaBinding.bind(itemView)
        var context: Context =itemView.context
        var id:String=""


        fun setOnclicListener(id: String) {
            binding.botton2.setOnClickListener(this)
            this.id=id
        }

        override fun onClick(v: View?) {
            val intentEncuesta: Intent = Intent(context, VistaEncuesta::class.java).apply {
              putExtra("idEncuesta",binding.idenc.text)
            }
            context.startActivity(intentEncuesta)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_encuesta,parent,false)
        )

    }

    override fun getItemCount()=encuesta.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val encuestaN =encuesta[position]
        holder.binding.idenc.text=encuestaN.id
        holder.binding.TitleEncuesta.text=encuestaN.encuesta

        holder.setOnclicListener(encuestaN.id)

    }

    override fun onClick(v: View?) {

    }
}