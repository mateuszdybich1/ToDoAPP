package com.dybich.todoapp

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat



class DoingFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth


    private lateinit var informationsTV : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Doing_Adapter

    private var todotasks : ArrayList<Task> = arrayListOf()
    private var finalList : ArrayList<Task> = arrayListOf()
    private var  isManager = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_doing, container, false)

        recyclerView = view.findViewById(R.id.Doing_RV)
        informationsTV = view.findViewById(R.id.fdoing_TV)

        if(savedInstanceState!=null){

            finalList = savedInstanceState.getParcelableArrayList<Task>("restoreList")!!
            if(finalList.size > 0){
                informationsTV.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recyclerView.layoutManager = LinearLayoutManager(view.context)
                adapter = Doing_Adapter(finalList!!)
                recyclerView.adapter = adapter
            }

            getLIST(view)
        }
        else{
            getLIST(view)
        }

        return view
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("restoreList",finalList)
    }


    private fun getLIST(view: View){

        val db = Firebase.database
        val user = auth.currentUser

        val uid = user?.uid

        database.child("users").child(uid.toString()).child("manager").get().addOnSuccessListener {manager->
            if(manager.value == "true"){
                database.child("users").child(uid.toString()).child("username").get().addOnSuccessListener {
                    var nick = it.value.toString()

                    val userData=   db.getReference("/managers/$nick/tasks/Doing")
                    userData.addValueEventListener(object: ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            todotasks = arrayListOf<Task>()
                            if(snapshot.exists()){
                                recyclerView.visibility = View.VISIBLE
                                recyclerView.layoutManager = LinearLayoutManager(view.context)

                                for(userSnapshot in snapshot.children){


                                    val task = userSnapshot.getValue(Task::class.java)
                                    if (task != null ) {
                                        todotasks.add(task)
                                    }
                                }
                                if(todotasks.size > 0){

                                    informationsTV.visibility = View.GONE
                                    var sortedList = todotasks.sortedBy {
                                        SimpleDateFormat("dd-MM-yyyy HH:mm").parse(it.deadline).time
                                    }
                                    finalList.clear()
                                    finalList.addAll(sortedList)
                                    adapter = Doing_Adapter(finalList)
                                    recyclerView.adapter = adapter
                                }
                                else{
                                    informationsTV.text = "No tasks, please start one"
                                    informationsTV.visibility = View.VISIBLE
                                }

                            }
                            else{
                                informationsTV.text = "No tasks, please start one"
                                informationsTV.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE

                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }
            }
            else{

                val currUser=   db.getReference("/users/${uid.toString()}/leader")

                currUser.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.value != "null"){
                            informationsTV.visibility = View.GONE
                            val leader = snapshot.value.toString()

                            val userData=   db.getReference("/managers/$leader/tasks/Doing")
                            userData.addValueEventListener(object: ValueEventListener {

                                @RequiresApi(Build.VERSION_CODES.O)
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    todotasks = arrayListOf<Task>()
                                    if(snapshot.exists()){

                                        recyclerView.layoutManager = LinearLayoutManager(view.context)

                                        for(userSnapshot in snapshot.children){


                                            val task = userSnapshot.getValue(Task::class.java)
                                            if (task != null ) {
                                                todotasks.add(task)
                                            }
                                        }
                                        if(todotasks.size > 0){


                                            var listt = todotasks.sortedBy {
                                                SimpleDateFormat("dd-MM-yyyy HH:mm").parse(it.deadline).time
                                            }

                                            adapter = Doing_Adapter(listt)


                                            informationsTV.visibility = View.GONE

                                            recyclerView.adapter = adapter
                                            recyclerView.visibility = View.VISIBLE
                                        }
                                    }
                                    else{
                                        informationsTV.text = "No Doing tasks found. Start a task!"
                                        informationsTV.visibility = View.VISIBLE
                                        recyclerView.visibility = View.GONE
                                    }


                                }
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        }
                        else{

                            informationsTV.text = "Please ask your leader, to add you to the project"
                            informationsTV.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
    }


}