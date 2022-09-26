package com.centrogeo.aplicadorEncuestas.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.centrogeo.aplicadorEncuestas.VistaEncuesta
import com.centrogeo.aplicadorEncuestas.databinding.FragmentAddSurveyBinding
import com.centrogeo.aplicadorEncuestas.io.ApiService
import com.centrogeo.aplicadorEncuestas.io.response.SurveyResponse
import com.centrogeo.aplicadorEncuestas.model.Survey.SurveyApi
import com.centrogeo.aplicadorEncuestas.model.encuesta.Survey
import com.centrogeo.aplicadorEncuestas.utilidades.Conexion_db_enc
import com.centrogeo.aplicadorEncuestas.utilidades.Const
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.net.ssl.*
import com.centrogeo.aplicadorEncuestas.utilidades.RetrofitInit


class AddSurveyFragment : Fragment() {

    private val permissionID = 42
    private lateinit var homeViewModel: AddSurveyModel
    private var _binding: FragmentAddSurveyBinding? = null
    private val binding get() = _binding!!


    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView( inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeViewModel = ViewModelProvider(this).get(AddSurveyModel::class.java)
        _binding= FragmentAddSurveyBinding.inflate(inflater,container,false)



        binding.descargarEncuestaND.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.descargarEncuestaND.visibility = View.GONE

          ///  descargar(binding.CodigoEncuestaND.text.toString())

            binding.progressBar.visibility = View.GONE
            binding.descargarEncuestaND.visibility = View.VISIBLE
            searchByCode(binding.CodigoEncuestaND.text.toString())
        }

        return binding.root
    }

    private fun searchByCode(id:String){
        val prefs = activity?.getSharedPreferences("preferenciasAplicador",Context.MODE_PRIVATE)
        val token = prefs?.getString("token","")
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitInit().getRetrofit().create(ApiService::class.java).also {
                    it.getSurvey("Bearer $token",id).enqueue(object : Callback<SurveyApi>{
                        override fun onResponse(
                            call: Call<SurveyApi>,
                            response: Response<SurveyApi>
                        ) {
                            response.body()?.let { it1 -> execSQl2(it1) }
                           Log.i("retrofit",response.body().toString())
                        }

                        override fun onFailure(call: Call<SurveyApi>, t: Throwable) {
                            Log.i("retrofirFailure",call.toString())
                            Log.i("retrofirFailure",t.toString())
                        }
                    })
                }
        }
    }



    private fun verificarCodigoEncuesta(document : QueryDocumentSnapshot, codigo_encuesta:String): Boolean {
        return document.data["codigo"] == codigo_encuesta

    }

    private fun descargar(codigo_encuesta:String){
        var success=false
        try {
            val database = FirebaseFirestore.getInstance()
            val gson= Gson()
            database.collection("Surveys")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(verificarCodigoEncuesta(document, codigo_encuesta)) {
                            val surveysJSon:String=gson.toJson(document.data)
                            val survey: Survey =gson.fromJson(surveysJSon, Survey::class.java)
                            execSQl(document,survey)
                            success=true
                        }
                    }
                    if (!success){
                        showAlert()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("MY_TASK", "Error getting documents: ", exception)
                }
        }catch (e:Exception){

        }
    }

    private fun execSQl( document: QueryDocumentSnapshot, survey: Survey){
        val conn = Conexion_db_enc(activity, "db_usuarios", null, 1)
        val db: SQLiteDatabase = conn.writableDatabase


          // a eliminar  db.execSQL("DELETE FROM ENCUESTAS_LIST WHERE ID_ENCUESTA ='${document.id}'")
            db.execSQL("DELETE FROM ${Const.tabla_IdEncuestas} WHERE ${Const.id_encuesta}='${document.id}'")
            //db.execSQL("INSERT INTO ENCUESTA(ID_ENC,TITLE) VALUES ('${survey.id}','${survey.Titulo}')")
            db.execSQL("DELETE FROM ${Const.tabla_preguntas} WHERE ID_ENC='${document.id}'")

        for (i in survey.preguntas.indices) {
            var opciones =""
            for (j in survey.preguntas[i].respuestas.indices){
                opciones += "${survey.preguntas[i].respuestas[j].respuesta},"
            }
            if (opciones.endsWith(",", false)) {
                opciones= opciones.removeRange(opciones.length-1,opciones.length)
            }
            db.execSQL("INSERT INTO ENCUESTA_PREG(PREGUNTA,OPCIONES,TIPO,ID_ENC,ID_PREG) VALUES " +
                    "('${survey.preguntas[i].pregunta}','${opciones}','${survey.preguntas[i].tipoS}','" +
                    "${document.id}','${document.id}_${i}')")
        }
        db.close()
        showMessage(survey.id,survey.nombre)

    }

    private fun execSQl2( survey: SurveyApi){
        val conn = Conexion_db_enc(activity, "db_usuarios", null, 1)
        val db: SQLiteDatabase = conn.writableDatabase


        // a eliminar  db.execSQL("DELETE FROM ENCUESTAS_LIST WHERE ID_ENCUESTA ='${document.id}'")
        db.execSQL("DELETE FROM ${Const.tabla_IdEncuestas} WHERE ${Const.id_encuesta}='${survey._id.`$oid`}'")
        //db.execSQL("INSERT INTO ENCUESTA(ID_ENC,TITLE) VALUES ('${survey.id}','${survey.Titulo}')")
        db.execSQL("DELETE FROM ${Const.tabla_preguntas} WHERE ID_ENC='${survey._id.`$oid`}'")

        for (i in survey.preguntas.indices) {
            var opciones =""
            for (j in survey.preguntas[i].opciones.indices){
                opciones += "${survey.preguntas[i].opciones[j].opcion},"
            }
            if (opciones.endsWith(",", false)) {
                opciones= opciones.removeRange(opciones.length-1,opciones.length)
            }
            db.execSQL("INSERT INTO ENCUESTA_PREG(PREGUNTA,OPCIONES,TIPO,ID_ENC,ID_PREG) VALUES " +
                    "('${survey.preguntas[i].pregunta}','${opciones}','${survey.preguntas[i].tipoS}','" +
                    "${survey._id.`$oid`}','${survey._id.`$oid`}_${i}')")
        }
        db.close()
        Log.i("sql",survey._id.`$oid`)
        showMessage(survey._id.`$oid`,survey.nombre)

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage("Encuesta no encontrada o codigo mal escrito")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showMessage(id:String,titulo:String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Resultado Exitoso")
        builder.setMessage("Encuesta Añadida exitosamente. Desea responderla ahora?")
        builder.setPositiveButton("Si") { _, _ ->
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                leerubicacionactual(id,titulo,true)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionID )
            }
        }
        builder.setNegativeButton("No") { _ , _ ->
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                leerubicacionactual(id,titulo,false)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionID )
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun leerubicacionactual( id: String,titulo: String,responder: Boolean) {
        val conn = Conexion_db_enc(activity, "db_usuarios", null, 1)
        val db: SQLiteDatabase = conn.writableDatabase
        if (ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {

                        val latitud = location.latitude.toString()
                        val longitud = location.longitude.toString()
                     //   db.execSQL("INSERT INTO ENCUESTAS_LIST(ID_ENCUESTA,LATITUD,LONGITUD) VALUES ('${id}','${latitud}','${longitud}')")
                        db.execSQL("INSERT INTO ${Const.tabla_IdEncuestas}(${Const.id_encuesta},${Const.titulo},${Const.latitud},${Const.longitud}) VALUES ('$id','$titulo','$latitud','$longitud')")
                        binding.CodigoEncuestaND.text.clear()
                        db.close()
                        responderAhora(responder,id)


                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                requireActivity().finish()
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(),arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION ), permissionID )
        }
    }

    private fun responderAhora(responder:Boolean, id: String){
        if (responder){
            val intentEncuesta: Intent = Intent(activity, VistaEncuesta::class.java).apply {
                putExtra("idEncuesta",id)
            }
            startActivity(intentEncuesta)

        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
            numUpdates = 1

        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallBack,
            Looper.myLooper()
        )
    }

    private val mLocationCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            mLastLocation.latitude.toString()
            mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}