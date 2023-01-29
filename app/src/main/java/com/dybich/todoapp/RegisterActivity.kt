package com.dybich.todoapp

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class RegisterActivity : AppCompatActivity() {


    private val nickLayout : TextInputLayout by lazy {
        findViewById(R.id.Nickname_ETL)
    }
    private val nickET : TextInputEditText by lazy {
        findViewById(R.id.Nickname_ET)
    }

    private val repPassLayout : TextInputLayout by lazy {
        findViewById(R.id.Rep_Pass_ETL)
    }
    private val repPassET : TextInputEditText by lazy {
        findViewById(R.id.Rep_Pass_ET)
    }

    private val passwordLayout : TextInputLayout by lazy {
        findViewById(R.id.Reg_password_ETL)
    }
    private val passwordET : TextInputEditText by lazy {
        findViewById(R.id.Reg_password_ET)
    }
    private val emailLayout : TextInputLayout by lazy {
        findViewById(R.id.Reg_email_ETL)
    }
    private val emailET : TextInputEditText by lazy {
        findViewById(R.id.Reg_email_ET)
    }

    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.Register_PB)
    }

    private lateinit var btnTV : TextView


    private lateinit var registerBTN : Button
    private lateinit var switch : Switch


    private lateinit var fadeIN: Animation
    private lateinit var fadeOUT: Animation


    private lateinit var currentuser : FirebaseUser
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)




        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()


        switch = findViewById(R.id.register_S)
        registerBTN = findViewById(R.id.Register_BTN)
        btnTV = findViewById(R.id.Register_TV)







        fadeIN = AnimationUtils.loadAnimation(this, com.google.android.material.R.anim.abc_fade_in)
        fadeOUT = AnimationUtils.loadAnimation(this, com.google.android.material.R.anim.abc_fade_out)

        disableErrors()


        registerBTN.setOnClickListener() {
            startLoading()
            if(nickCheck() && emailValidation() && passCheck() && repPassCheck()){
                var nicknamevar = nickET.text.toString()
                var emailvar = emailET.text.toString()
                var passvar = passwordET.text.toString()

                database.child("logins").child(nicknamevar).get().addOnSuccessListener {
                    Log.d("TAG", "navigation_fragments")
                    if (it.value == nicknamevar) {
                        nickLayout.error = "Nickname already used"
                        stopLoading()

                    }
                    else{

                        auth.createUserWithEmailAndPassword(emailvar, passvar)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success")
                                    val confirmed = "false"
                                    val rand = Random.nextInt(1111,9999)
                                    if(switch.isChecked){
                                        addUser(nicknamevar,emailvar,confirmed, "true", rand)
                                    }
                                    else{
                                        addUser(nicknamevar,emailvar,confirmed, "False", rand)
                                    }

                                }
                                else{
                                    emailLayout.error = "Email exists. Please sign in"
                                    stopLoading()
                                }
                            }
                    }
                }.addOnFailureListener {
                    try{
                        throw it
                    }
                    catch (e : NetworkErrorException){
                        Toast.makeText(this,"Internet Error",Toast.LENGTH_LONG).show()
                    }
                    catch (e: FirebaseNetworkException){
                        Toast.makeText(this,"Internet Error",Toast.LENGTH_LONG).show()
                    }
                }

            }
            else{
                stopLoading()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addUser(login: String, email: String, confirmed: String, manager: String, rand:Int)
    {
        currentuser = auth.currentUser!!
        val uid = currentuser?.uid
        val user = User(login, email,confirmed, manager)

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val current = LocalDateTime.now().format(formatter)

        val deadline = LocalDateTime.now().plusDays(1).format(formatter)
        val task = Task("First task", "This is short description","this is extra information of the task","admin", current, deadline)
        database.child("users").child(uid.toString()).setValue(user)

        database.child("logins").child(login).setValue(login)



        if(manager == "true"){
            database.child("managers").child(login).child("tasks").child("ToDo").push().setValue(task)
        }

        codeVerification(rand)

    }


    private fun startLoading(){

        btnTV.startAnimation(fadeOUT)
        btnTV.visibility = View.GONE

        passwordET.isEnabled = false
        emailET.isEnabled = false
        repPassET.isEnabled = false
        nickET.isEnabled = false
        registerBTN.isEnabled = false



        progressBar.startAnimation(fadeIN)
        progressBar.visibility = View.VISIBLE

    }

    private fun stopLoading(){

        progressBar.visibility = View.GONE
        progressBar.startAnimation(fadeOUT)
        btnTV.visibility = View.VISIBLE
        btnTV.startAnimation(fadeIN)
        registerBTN.isEnabled = true

        passwordET.isEnabled = true
        emailET.isEnabled = true
        repPassET.isEnabled = true
        nickET.isEnabled = true

    }

    private fun disableErrors(){
        nickET.addTextChangedListener {
            nickLayout.error = null
        }

        emailET.addTextChangedListener {
            emailLayout.error = null
        }
        passwordET.addTextChangedListener {
            passwordLayout.error = null
        }
        repPassET.addTextChangedListener {
            repPassLayout.error = null
        }

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


    private fun codeVerification(rand:Int){
        val value = rand.toString()
        val currentUser = auth.currentUser
        val email = currentUser?.email.toString()
        val  url = "https://marketnft.000webhostapp.com/"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val postRequest = object: StringRequest(Method.POST,url,  Response.Listener<String?> { response ->
            Toast.makeText(this,response, Toast.LENGTH_LONG).show()
            val intent = Intent(this,ConfirmMailActivity::class.java)
            intent.putExtra("rand", value)
            startActivity(intent)
            finish()

        }, Response.ErrorListener { error ->
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

    private fun nickCheck() : Boolean{

            val id = nickET.text.toString()
            if(id == "" ){
                nickLayout.error = "Nickname is empty"
                return false
            }
            else if(id.contains(" ")){
                nickLayout.error = "Nickname mustn't contain spaces"
                return false
            }
            else if(id.length > 15){
                nickLayout.error = "Nickname too long"
                return false
            }

            else {
                nickLayout.error = null
                return true

            }
    }



    private fun passCheck() : Boolean{


            val id = passwordET.text.toString()
            if(id == "" ){
                passwordLayout.error = "Password is empty"
                return false
            }
            else if(id.contains(" ")){
                passwordLayout.error = "Password mustn't contain spaces"
                return false
            }
            else if( id.length < 6){
                passwordLayout.error = "Password too short"
                return false
            }
            else if(id.length > 20){
                passwordLayout.error = "Password too long"
                return false
            }
            else {
                passwordLayout.error = null
                 return true

            }


    }
    private fun repPassCheck() : Boolean{

            if(repPassET.text.toString() != passwordET.text.toString() && passwordET.text.toString() != "" ){
                repPassLayout.error = "Repeat password is incorrect"
                return false
            }
            else {
                repPassLayout.error = null
                return true
            }

    }
}