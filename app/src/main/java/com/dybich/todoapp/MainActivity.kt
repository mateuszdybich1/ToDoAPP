package com.dybich.todoapp

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val passwordLayout : TextInputLayout by lazy {
        findViewById(R.id.Password_ETL)
    }
    private val passwordET : TextInputEditText by lazy {
        findViewById(R.id.Password_ET)
    }
    private val emailLayout : TextInputLayout by lazy {
        findViewById(R.id.Email_ETL)
    }
    private val emailET : TextInputEditText by lazy {
        findViewById(R.id.Email_ET)
    }
    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.Loading_Circle)
    }

    private lateinit var Forgot_Pass_TV: TextView
    private lateinit var Sign_Up_TV : TextView

    private lateinit var login_TV : TextView
    private lateinit var loginBTN : Button

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var fadeIN: Animation
    private lateinit var fadeOUT: Animation




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fadeIN = AnimationUtils.loadAnimation(this, com.google.android.material.R.anim.abc_fade_in)
        fadeOUT = AnimationUtils.loadAnimation(this, com.google.android.material.R.anim.abc_fade_out)

        Forgot_Pass_TV = findViewById(R.id.Forgot_Pass_TV)
        Sign_Up_TV = findViewById(R.id.Sign_Up_TV)


        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        login_TV = findViewById(R.id.Btn_TV)
        loginBTN = findViewById(R.id.Login_BTN)


        disableErrors()


        if(auth.currentUser!=null){
                val intent = Intent(this, LoggedInActivity::class.java)
                startActivity(intent)

                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                finish()
        }

        loginBTN.setOnClickListener(){

            if(emailValidation() == true){
                startLoading()

                auth.signInWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString()).addOnCompleteListener {

                    if (it.isSuccessful) {

                        val currentUser = auth.currentUser
                        database.child("users").child(currentUser?.uid.toString()).child("isconfirmed").get()
                            .addOnSuccessListener { it ->
                                if (it.value == "true") {
                                    val intent = Intent(this, LoggedInActivity::class.java)
                                    startActivity(intent)
                                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

                                    finish()


                                } else {
                                    val rand = Random.nextInt(1111,9999)
                                    codeVerification(rand)
                                }
                            }

                    } else {
                        try {
                            throw it.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            emailLayout.error = "Email does not exists"

                        } catch (e: FirebaseAuthException){

                            passwordLayout.error = "Wrong password"

                        }
                            stopLoading()

                    }
                }


            }

        }

    }
    private fun disableErrors(){
        emailET.addTextChangedListener {
            emailLayout.error = null
        }
        passwordET.addTextChangedListener {
            passwordLayout.error = null
        }

    }

    private fun codeVerification(rand:Int){
        val value = rand.toString()
        val currentUser = auth.currentUser
        val email = currentUser?.email.toString()

        val  url = "https://marketnft.000webhostapp.com/"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val postRequest = object: StringRequest(Method.POST,url,  Response.Listener<String?> {
                response ->
            Toast.makeText(this,response, Toast.LENGTH_LONG).show()

            val intent = Intent(this,ConfirmMailActivity::class.java)
            intent.putExtra("rand", value)
            startActivity(intent)
            finish()

        }, Response.ErrorListener {
                error ->
            Log.d("TAG", error.message.toString())
            Toast.makeText(this,error.message.toString(), Toast.LENGTH_LONG).show()
        })
        {
            override fun getParams() : Map<String,String>{
                val params = HashMap<String,String>()
                params["email"]= email
                params["code"] = value
                return params
            }
        }
        requestQueue.add(postRequest)
    }

    private fun startLoading(){

        login_TV.startAnimation(fadeOUT)
        login_TV.visibility = View.GONE

        passwordET.isEnabled = false
        emailET.isEnabled = false
        loginBTN.isEnabled = false
        Forgot_Pass_TV.isEnabled = false
        Sign_Up_TV.isEnabled = false

        progressBar.startAnimation(fadeIN)
        progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading(){

        progressBar.visibility = View.GONE
        progressBar.startAnimation(fadeOUT)
        login_TV.visibility = View.VISIBLE
        login_TV.startAnimation(fadeIN)
        loginBTN.isEnabled = true

        passwordET.isEnabled = true
        emailET.isEnabled = true
        Forgot_Pass_TV.isEnabled = true
        Sign_Up_TV.isEnabled = true

    }



    private fun emailValidation() : Boolean{
        if(emailET.text.toString() != "" && !emailET.text.toString().contains(" ")){
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailET.text.toString()).matches())
            {
                emailLayout.error = "Correct form of email needed"
                return false
            }
            else{
                emailLayout.error = null
                return true

            }
        }
        else if(emailET.text.toString() == ""){
            emailLayout.error = "Please enter email"
            return false
        }
        else if(emailET.text.toString().contains(" ")){
            emailLayout.error = "Email mustn't contain spaces"
            return false
        }
        else{
            return false
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }



    fun registerClicked(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)

    }
    fun forgPassClicked(view: View) {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }
}