package com.centrogeo.aplicadorEncuestas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.centrogeo.aplicadorEncuestas.databinding.ActivityRegisterBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject


@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginButton: LoginButton
    private lateinit var loginButtonG: SignInButton

    private lateinit var info: EditText
    private lateinit var corr: EditText
    private lateinit var dataUser: JSONObject
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var rcsignin = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Registrarse"


        //token de acceso de FB

        val accessToken = AccessToken.getCurrentAccessToken()
//        val isLoggedIn = accessToken != null && !accessToken.isExpired

        //campos

        info = findViewById(R.id.NombreSingUP)
        corr = findViewById(R.id.correoSingUP)


        //login de google primera parte

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        loginButtonG = findViewById(R.id.sign_in_buttonG)

        binding.signInButtonG.setOnClickListener { signIn() }


        //Login Facebook


        callbackManager = CallbackManager.Factory.create()
        loginButton = findViewById(R.id.login_buttonFA)
        loginButton.setPermissions("email")

        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (accessToken != null || account != null) {
            intent = Intent(this,OptionsMenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            info.setText("")
            corr.setText("")

        }


        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(loginResult: LoginResult?) {
                Log.d("FBLOGIN", loginResult?.accessToken?.token.toString())
                Log.d("FBLOGIN", loginResult?.recentlyDeniedPermissions.toString())
                Log.d("FBLOGIN", loginResult?.recentlyGrantedPermissions.toString())
                val request =
                    GraphRequest.newMeRequest(loginResult?.accessToken) { `object`, _ ->
                        try {
                            //here is the data that you want
                            Log.d("FBLOGIN_JSON_RES", `object`.toString())
                            dataUser = `object`
                            val name = `object`.getString("name")
                            val correo = `object`.getString("email")
                            //var id: String = `object`.getString("id")
                            showHome(correo)
                            info.setText(name)
                            corr.setText(correo)
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


        //SQL signup normal


        val campoContrasena:EditText=findViewById(R.id.editPasswordSignUP)





        binding.button.setOnClickListener {

            if(corr.text.isNotEmpty() && campoContrasena.text.isNotEmpty()&& info.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(corr.text.toString(),campoContrasena.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        showHome(it.result?.user?.email?:"")
                        val prefs = getSharedPreferences(getString(R.string.sharedPrefences), 0)
                        val editor =  prefs.edit()

                        editor.putBoolean(getString(R.string.key), true)
                        editor.apply()
                    }else{
                        showAlert()
                    }
                }
            }else{
                showAlert()
            }


        }


    }

//signUp Google parte 2

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, rcsignin)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcsignin) {
            val task: com.google.android.gms.tasks.Task<GoogleSignInAccount>? =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult()
        }

    }

    //funcion donde se encuentran los resultados del si

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleSignInResult() {
        try {
            //val account = completedTask?.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personName = acct.displayName
                //val personGivenName = acct.givenName
                //val personFamilyName = acct.familyName
                val personEmail = acct.email
                //val personId = acct.id
                //val personPhoto: Uri? = acct.photoUrl

                info.setText(personName)
                corr.setText(personEmail)
                if (personName != null) {
                    if (personEmail != null) {
                        showHome(personEmail)

                    }
                }
                //Glide.with(this).load(personPhoto.toString()).into(porfileIm)


            }
            //startActivity(intent)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("errror", "signInResult:failed code=" + e.statusCode)

        }
    }

    //funciones de alerta y de showHome


    private fun showHome(email: String) {
        val homeIntent: Intent = Intent(this, OptionsMenuActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error durante el registro revise sus datos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }




}

