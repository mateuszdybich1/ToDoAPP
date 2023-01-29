package com.dybich.todoapp

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddEmployeeBottomSheet :  BottomSheetDialogFragment() {



    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var addWorkerETL : TextInputLayout
    private lateinit var addWorkerET : TextInputEditText
    private lateinit var addBTN : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.add_to_project_bottom_sheet,container,false)

        addWorkerETL = view.findViewById(R.id.add_worker_ETL)
        addWorkerET= view.findViewById(R.id.add_worker_ET)
        addBTN = view.findViewById(R.id.add_worker_BTN)

        addBTN.setOnClickListener(){
            addBTN.isEnabled = false
            if(emailValidation()){

                val db = Firebase.database

                val userData=   db.getReference("/users")
                var emailvar = "null"
                var key = "null"
                var emailExists = false



                userData.addListenerForSingleValueEvent(object: ValueEventListener {


                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {


                            for (userSnapshot in snapshot.children) {
                                var email = userSnapshot.child("email").value.toString()
                                if (email == addWorkerET.text.toString()) {
                                    emailExists = true
                                    emailvar = email
                                    key = userSnapshot.key.toString()

                                    val currentUser = auth.currentUser
                                    database.child("users").child(currentUser?.uid.toString()).child("username").get()
                                        .addOnSuccessListener { manager ->
                                            var m = manager.value.toString()
                                            database.child("users").child(key).child("leader").get()
                                                .addOnSuccessListener { leader ->
                                                    if (leader.value.toString() == "null") {
                                                        leader.ref.setValue(m)

                                                            database.child("managers").child(m)
                                                                .child("employees").push()
                                                                .setValue(emailvar)
                                                            Toast.makeText(it.context, "Employee added successfully", Toast.LENGTH_SHORT).show()
                                                            addWorkerET.text?.clear()
                                                            addBTN.isEnabled = true

                                                    } else {
                                                        addWorkerETL.error = "Employee has leader"
                                                        addBTN.isEnabled = true
                                                    }
                                                }


                                        }

                                }
                            }
                            if (!emailExists) {
                                addWorkerETL.error = "Email doesn't exists"
                                addBTN.isEnabled = true
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        try {
                            throw error.toException()
                        }
                        catch (e : NetworkErrorException){
                            Toast.makeText(it.context,"Internet Error",Toast.LENGTH_LONG).show()
                            addBTN.isEnabled = true
                        }
                        catch (e: FirebaseNetworkException){
                            Toast.makeText(it.context,"Internet Error",Toast.LENGTH_LONG).show()
                            addBTN.isEnabled = true
                        }
                    }

                })




            }


        }

        addWorkerET.addTextChangedListener { addWorkerETL.error = null  }


        return  view
    }

    private fun emailValidation() : Boolean{
        if(addWorkerET.text.toString() != "" && !addWorkerET.text.toString().contains(" ")){
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(addWorkerET.text.toString()).matches())
            {
                addWorkerETL.error = "Correct form of email needed"
                return false
            }
            else{
                addWorkerETL.error = null
                return true

            }
        }
        else if(addWorkerET.text.toString() == ""){
            addWorkerETL.error = "Please enter email"
            return false
        }
        else if(addWorkerET.text.toString().contains(" ")){
            addWorkerETL.error = "Email mustn't contain spaces"
            return false
        }
        else{
            return false
        }
    }
}