package com.dybich.todoapp

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ToDoBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(taskName : String, manager: String ?= "false") = ToDoBottomSheet().apply {
            arguments = Bundle().apply {
                putString("taskName", taskName)
                putString("manager",manager)
            }
        }
    }


    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var btnStart : Button
    private lateinit var btnDelete : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        val view =  inflater.inflate(R.layout.todo_bottom_sheet_list, container, false)
        btnStart = view.findViewById(R.id.buttonStart)
        btnDelete = view.findViewById(R.id.buttonDelete)

        val taskName = arguments?.getString("taskName").toString()
        val manager = arguments?.getString("manager").toString()
        val currentUser = auth.currentUser


        if(manager == "true"){

            btnStart.setOnClickListener(){

                database.child("users").child(currentUser?.uid.toString()).child("username").get()
                    .addOnSuccessListener { username ->
                        val username = username.value.toString()
                        val db = Firebase.database
                        val userData=   db.getReference("/managers/$username/tasks/ToDo")

                        userData.addListenerForSingleValueEvent(object: ValueEventListener {


                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){

                                    for(userSnapshot in snapshot.children){
                                        var currentName = userSnapshot.child("name").value.toString()
                                        if(currentName == taskName){
                                            var task = userSnapshot.getValue(Task::class.java)
                                            task!!.startedBy = username.toString()
                                            database.child("managers").child(username).child("tasks").child("Doing").push().setValue(task)
                                            userSnapshot.ref.removeValue()
                                            Toast.makeText(it.context,"Task started! Now it's in section \"Doing\".",Toast.LENGTH_SHORT).show()
                                            this@ToDoBottomSheet.dismiss()
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                try {
                                    throw error.toException()
                                }
                                catch (e : NetworkErrorException){
                                    Toast.makeText(it.context,"Internet Error",Toast.LENGTH_LONG).show()
                                }
                                catch (e: FirebaseNetworkException){
                                    Toast.makeText(it.context,"Internet Error",Toast.LENGTH_LONG).show()
                                }
                            }})

                    }

            }
            btnDelete.setOnClickListener(){
                database.child("users").child(currentUser?.uid.toString()).child("username").get()
                    .addOnSuccessListener { username ->
                        val username = username.value.toString()
                        val db = Firebase.database
                        val userData=   db.getReference("/managers/$username/tasks/ToDo")

                        userData.addListenerForSingleValueEvent(object: ValueEventListener {


                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){

                                    for(userSnapshot in snapshot.children){
                                        var currentName = userSnapshot.child("name").value.toString()
                                        if(currentName == taskName){
                                            userSnapshot.ref.removeValue()

                                            Toast.makeText(it.context,"Task deleted successfully!",Toast.LENGTH_SHORT).show()
                                            this@ToDoBottomSheet.dismiss()
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                try {
                                    throw error.toException()
                                }
                                catch (e : NetworkErrorException){
                                    Toast.makeText(it.context,"Internet Error",Toast.LENGTH_LONG).show()
                                }
                                catch (e: FirebaseNetworkException){
                                    Toast.makeText(it.context,"Internet Error",Toast.LENGTH_LONG).show()
                                }
                            }})

                    }
            }

        }
        else{
            btnDelete.visibility = View.GONE

            btnStart.setOnClickListener(){

                database.child("users").child(currentUser?.uid.toString()).child("leader").get()
                    .addOnSuccessListener { username ->
                        val username = username.value.toString()

                        val db = Firebase.database
                        val userData=   db.getReference("/managers/$username/tasks/ToDo")

                        userData.addListenerForSingleValueEvent(object: ValueEventListener {


                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){

                                    for(userSnapshot in snapshot.children){
                                        var currentName = userSnapshot.child("name").value.toString()
                                        if(currentName == taskName){
                                            database.child("users").child(currentUser?.uid.toString()).child("username").get()
                                                .addOnSuccessListener { employeeName ->
                                                    val employeeName = employeeName.value.toString()
                                                    var task = userSnapshot.getValue(Task::class.java)
                                                    task!!.startedBy = employeeName
                                                    database.child("managers").child(username).child("tasks").child("Doing").push().setValue(task)
                                                    userSnapshot.ref.removeValue()
                                                    Toast.makeText(it.context,"Task started! Now it's in section \"Doing\".",Toast.LENGTH_SHORT).show()
                                                    this@ToDoBottomSheet.dismiss()
                                                }
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                try {
                                    throw error.toException()
                                }
                                catch (e : NetworkErrorException){
                                    Toast.makeText(it.context,"Internet Error",Toast.LENGTH_LONG).show()
                                }
                                catch (e: FirebaseNetworkException){
                                    Toast.makeText(it.context,"Internet Error",Toast.LENGTH_LONG).show()
                                }
                            }})
                    }

            }

        }
        return view
    }


}