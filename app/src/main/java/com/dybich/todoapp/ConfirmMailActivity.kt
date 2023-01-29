package com.dybich.todoapp

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ConfirmMailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference


    private val et1: EditText by lazy {
        findViewById(R.id.et1)
    }
    private val et2: EditText by lazy {
        findViewById(R.id.et2)
    }
    private val et3: EditText by lazy {
        findViewById(R.id.et3)
    }
    private val et4: EditText by lazy {
        findViewById(R.id.et4)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_mail_layout)

        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        reset()
        val rand :String = intent.getStringExtra("rand").toString()
        setListener(rand)


    }


    private fun setListener(rand:String){
        setTextChangeListener(fromET = et1, targetET = et2)
        setTextChangeListener(fromET = et2, targetET = et3)
        setTextChangeListener(fromET = et3, targetET = et4)
        setTextChangeListener(fromET = et4, done = {
            verifyCode(rand)
        })

        setKeyListener(fromET = et4, backToET = et3)
        setKeyListener(fromET = et3, backToET = et2)
        setKeyListener(fromET = et2, backToET = et1)
    }
    private fun focus(){
        et1.isEnabled = true
        et1.postDelayed({
            et1.requestFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(et1, InputMethodManager.SHOW_FORCED)
        },500)
    }
    private fun reset(){
        et1.isEnabled = false
        et2.isEnabled = false
        et3.isEnabled = false
        et4.isEnabled = false

        et1.setText("")
        et2.setText("")
        et3.setText("")
        et4.setText("")
        focus()
    }

    private fun setTextChangeListener(fromET: EditText,targetET:EditText?=null, done:(()->Unit)?= null){
        fromET.addTextChangedListener {
            it?.let {
                    string->
                if(string.isNotEmpty()){
                    targetET?.let {
                            editText->
                        editText.isEnabled = true
                        editText.requestFocus()
                    }?: run{
                        done?.let {
                                done ->
                            done()
                        }
                    }
                    fromET.clearFocus()
                    fromET.isEnabled = false
                }
            }
        }
    }
    private fun setKeyListener(fromET:EditText,backToET:EditText){
        fromET.setOnKeyListener{
                _,_,event ->
            if(null != event && KeyEvent.KEYCODE_DEL== event.keyCode){
                backToET.isEnabled = true
                backToET.requestFocus()
                backToET.setText("")
                fromET.clearFocus()
                fromET.isEnabled=false
            }
            false
        }
    }

    private fun verifyCode(rand:String){
        Log.d("TAG", rand)
        val otpCode = "${et1.text.toString().trim()}" + "${et2.text.toString().trim()}" +"${et3.text.toString().trim()}" + "${et4.text.toString().trim()}"
        if(4!= otpCode.length){
            return
        }
        if(otpCode == rand){

            val currentUser = auth.currentUser
            database.child("users").child(currentUser?.uid.toString()).child("isconfirmed").setValue("true")
            database.child("users").child(currentUser?.uid.toString()).child("isconfirmed").get().addOnSuccessListener {
                if(it.value == "true"){

                    val intent = Intent(this,LoggedInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }
        else{
            Toast.makeText(this,"Wrong verification code. Try again", Toast.LENGTH_LONG).show()
            reset()
        }
    }


    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}



