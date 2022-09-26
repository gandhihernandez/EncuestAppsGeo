package com.centrogeo.aplicadorEncuestas

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.bumptech.glide.RequestBuilder
import com.centrogeo.aplicadorEncuestas.WorksManager.WorkSurveyService
import com.centrogeo.aplicadorEncuestas.WorksManager.WorkmanagerP1
import com.centrogeo.aplicadorEncuestas.databinding.ActivityMainBinding
import com.centrogeo.aplicadorEncuestas.io.ApiService
import com.centrogeo.aplicadorEncuestas.io.response.SurveyResponse
import com.centrogeo.aplicadorEncuestas.model.user.UserAuth
import com.centrogeo.aplicadorEncuestas.model.user.UserPost
import com.centrogeo.aplicadorEncuestas.utilidades.NetworkConection
import com.centrogeo.aplicadorEncuestas.utilidades.RetrofitInit
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val key = "My_key"
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginButton: LoginButton
    private lateinit var loginButtonG: SignInButton

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var rcSignIn = 0
    private lateinit var connectivityLiveData:NetworkConection
    private val permissionID = 42
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Inicio de sesión"



        //lifecycleScope.launch{
       //     prueba.update()
     //   }
        fun setUniqueRequest(){
            val surveyRequest:WorkRequest = OneTimeWorkRequest.Builder(WorkSurveyService::class.java)
                .build()
            WorkManager
                .getInstance(this)
                .enqueue(surveyRequest)
        }

        fun setPeriodicRequest(){
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
            val periodicWork= PeriodicWorkRequest.Builder(WorkmanagerP1::class.java,1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
                WorkManager.getInstance(applicationContext).enqueue(periodicWork)
        }

        setUniqueRequest()
        setPeriodicRequest()

        if (!this.permissionsGrantedGPS()) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),permissionID)

        }




// ...



        connectivityLiveData = NetworkConection(application)

       connectivityLiveData.observe(this, { isAvailable->
            when(isAvailable)
            {
                true-> {
                }
                false-> showAlert("no Network")

            }

        })

        //facebook login
        binding.editTextTextEmailAddressLog.setOnClickListener {
            val intent = Intent(this, OptionsMenuActivity::class.java)
            finish()
            startActivity(intent)
        }

        callbackManager = CallbackManager.Factory.create()
        loginButton = binding.Singin
        loginButton.setPermissions("email")

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {

                Log.d("FBLOGIN", loginResult?.accessToken?.token.toString())
                Log.d("FBLOGIN", loginResult?.recentlyDeniedPermissions.toString())
                Log.d("FBLOGIN", loginResult?.recentlyGrantedPermissions.toString())


                val request =
                    GraphRequest.newMeRequest(loginResult?.accessToken) { `object`, _ ->
                        try {
                            //here is the data that you want
                            Log.d("FBLOGIN_JSON_RES", `object`.toString())
                          //  val name = `object`.getString("name")
                          //  val correo = `object`.getString("email")
                        //    var id: String = `object`.getString("id")
// verificacion con la base de dato
                            // showAlert(correo)



                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }

                val parameters = Bundle()
                parameters.putString("fields", "name,email,id")
                request.parameters = parameters
                request.executeAsync()

                // App code
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })


        //login google


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        loginButtonG = binding.signInButtonGMian

        binding.signInButtonGMian.setOnClickListener {
            val googleconf=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()
           val googleClient = GoogleSignIn.getClient(this,googleconf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,rcSignIn)

          //  signIn()
        }




        if (AccessToken.getCurrentAccessToken() != null || GoogleSignIn.getLastSignedInAccount(this) != null) {
            intent = Intent(this, OptionsMenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val prefs = getSharedPreferences(getString(R.string.sharedPrefences), Context.MODE_PRIVATE)
        if (prefs.getBoolean(getString(R.string.key), false)) {
            intent = Intent(this, OptionsMenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        binding.buttonloginA.setOnClickListener {
                    setup()
            postLogin()

        }

        binding.Registrarse.setOnClickListener {
            val intent1 = Intent(this, RegisterActivity::class.java)
            startActivity(intent1)
        }





    }

    private fun postLogin(){
        val jsonObject = JSONObject()
        jsonObject.put("username", "juanDiaz")
        jsonObject.put("password", "1234567898")

        val data = UserPost(
            username = "juanDiaz",
            password = "1234567898"
        )
        if (binding.editTextTextEmailAddressLog.text.isNotEmpty() && binding.editTextTextPasswordLog.text.isNotEmpty()){

        }

        CoroutineScope(Dispatchers.IO).launch {

            RetrofitInit().getRetrofit().create(ApiService::class.java).also {
                it.login(data).enqueue(object : Callback<UserAuth> {
                    override fun onResponse(
                        call: Call<UserAuth>,
                        response: Response<UserAuth>
                    ) {
                        Log.i("retrofit", response.body().toString())
                        showHome(response.body()?.user?.email.toString(), response.body()?.user?.name.toString(), response.body()?.token.toString())
                    }

                    override fun onFailure(call: Call<UserAuth>, t: Throwable) {
                        Log.i("retrofirFailure",call.toString())
                        Log.i("retrofirFailure",t.toString())
                    }
                })
            }
        }
    }


    private fun showHome(email: String, name:String,token:String) {
        val homeIntent = Intent(this, OptionsMenuActivity::class.java)
        val prefs = getSharedPreferences(getString(R.string.sharedPrefences), Context.MODE_PRIVATE)
        val editor =  prefs.edit()
        editor.putBoolean(getString(R.string.key), true)
        editor.putString("Email",email)
        editor.putString("Name",name)
        if(!token.isNullOrBlank()){
            editor.putString("token", token)
        }
        editor.apply()
        startActivity(homeIntent)
    }



    private fun showAlert(Message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(Message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
     //  callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account= task.getResult(ApiException::class.java)!!


                val credential=GoogleAuthProvider.getCredential(account.idToken,null)

                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
                    if (it.isSuccessful){

                       showHome(account.email?:"",account.displayName?:"" ,"token")
                    }else
                    {
                        showAlert("Error con el login Google")
                    }
                }


            }catch (e:ApiException){
                showAlert(e.toString())
            }

           // handleSignInResult()
        }

    }

    //funcion donde se encuentran los resultados del si





    private fun setup() {
        if (binding.editTextTextEmailAddressLog.text.isNotEmpty() && binding.editTextTextPasswordLog.text.isNotEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                binding.editTextTextEmailAddressLog.text.toString(),
                binding.editTextTextPasswordLog.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {

                    showHome(it.result?.user?.email ?: "", it.result?.user?.displayName ?:"","token")

                } else {
                    showAlert("Error contraseña o correo erroneos")
                }
            }
        }
    }


    private fun permissionsGrantedGPS() = REQUIRED_PERMISSIONS_GPS.all {
        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
    }
    companion object {
        private val REQUIRED_PERMISSIONS_GPS= arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

}