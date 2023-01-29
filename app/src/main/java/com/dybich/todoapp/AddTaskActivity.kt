package com.dybich.todoapp

import android.accounts.NetworkErrorException
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.time.Duration.Companion.hours

class AddTaskActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val addnameETL : TextInputLayout by lazy {
        findViewById(R.id.add_name_ETL)
    }
    private val addnameET : TextInputEditText by lazy {
        findViewById(R.id.add_name_ET)
    }

    private val addshortDescrETL : TextInputLayout by lazy {
        findViewById(R.id.add_shortDescr_ETL)
    }
    private val addshortDescrET : TextInputEditText by lazy {
        findViewById(R.id.add_shortDescr_ET)
    }

    private val addextraInfoETL : TextInputLayout by lazy {
        findViewById(R.id.add_extraInfo_ETL)
    }
    private val addextraInfoET : TextInputEditText by lazy {
        findViewById(R.id.add_extraInfo_ET)
    }

    private lateinit var selectedDate : TextView

    private lateinit var pickdateBTN : Button
    private lateinit var pickhourBTN : Button
    private lateinit var addtaskBTN : Button



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_task_layout)

        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()


        pickdateBTN = findViewById(R.id.pick_date_BTN)
        pickhourBTN = findViewById(R.id.pick_hour_BTN)
        addtaskBTN = findViewById(R.id.add_task_BTN)

        selectedDate = findViewById(R.id.add_task_date_TV)

        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())  //HH:mm

        val formatterNOW = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val currentTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

        var date : String?= null
        var time : String ?=null
        date = LocalDateTime.now().plusDays(1).format(formatterNOW).toString()
        time = "23:59"
        //Current time of task
        val currentTime = LocalDateTime.now().format(currentTimeFormat).toString()

        //Default deadline
        selectedDate.text = date + " " + time

        pickdateBTN.setOnClickListener(){
            val datepick = DatePickerDialog(it.context,DatePickerDialog.OnDateSetListener{view, myear, mmonth, mdayOfMonth ->

                c.set(myear,mmonth,mdayOfMonth)
                date = formatter.format(c.timeInMillis).toString()
                //Set deadline date
                selectedDate.text = date + " " + time
            },year, month,day)

            datepick.show()
        }

        pickhourBTN.setOnClickListener(){
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(10).setTitleText("Select deadline time")
                    .build()

            picker.show(supportFragmentManager, "tag");

            picker.addOnPositiveButtonClickListener {
                var hour = picker.hour
                var minutes = picker.minute
                time = String.format(Locale.getDefault(),"%02d:%02d",hour,minutes)

                //Set deadline time
                selectedDate.text = date + " " + time
            }

        }

        resetErrors()
        addtaskBTN.setOnClickListener(){

            if(nameCheck() && descrCheck() && extraInfoCheck()){

                val currentuser = auth.currentUser!!
                val uid = currentuser?.uid

                var author = "null"
                database.child("users").child(uid.toString()).child("username").get()
                    .addOnSuccessListener { username ->
                        author = username.value.toString()
                        var shortDescr = addshortDescrET.text.toString()
                        if(shortDescr == "" || shortDescr == " "){
                            shortDescr = "No short description"
                        }

                        var extraInfo = addextraInfoET.text.toString()
                        if(extraInfo == "" || extraInfo == " "){
                            extraInfo = "No extra informations"
                        }
                        val task = Task(addnameET.text.toString(), shortDescr,
                            extraInfo,author, currentTime, selectedDate.text.toString())

                        database.child("managers").child(author).child("tasks").child("ToDo").push().setValue(task)
                        Toast.makeText(it.context,"Task added successfully",Toast.LENGTH_LONG).show()
                        val intent = Intent(this, LoggedInActivity::class.java)
                        startActivity(intent)
                        finish()
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


        }

    }

    private fun resetErrors() {
        addnameET.addTextChangedListener {
            if(addnameET.text.toString().length <=40){
                addnameETL.error = null
            }
            else{
                addnameETL.error = "Title too long"
            }
        }
        addshortDescrET.addTextChangedListener {
            if(addshortDescrET.text.toString().length <=100){
                addshortDescrETL.error = null
            }
            else{
                addshortDescrETL.error = "Too long"
            }
        }
        addextraInfoET.addTextChangedListener {
            if(addextraInfoET.text.toString().length <=400){
                addextraInfoETL.error = null
            }
            else{
                addextraInfoETL.error = "Too long"
            }
        }


    }

    private fun nameCheck() : Boolean{
        if(addnameET.text.toString() == ""){
            addnameETL.error = "Empty field not allowed"
            return false
        }
        else if(addnameET.text.toString().length > 40){
            addnameETL.error = "Title too long"
            return false
        }
        else{
            return true
        }
    }
    private fun descrCheck() : Boolean{
        if(addnameET.text.toString().length > 100){
            addnameETL.error = "Too long"
            return false
        }
        else{
            return true
        }
    }

    private fun extraInfoCheck() : Boolean{
        if(addextraInfoET.text.toString().length > 400){
            addextraInfoETL.error = "Too long"
            return false
        }
        else{
            return true
        }
    }


}