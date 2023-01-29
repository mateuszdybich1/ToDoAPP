package com.dybich.todoapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ToDoFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var expandBTN : FloatingActionButton
    private lateinit var addTaskBTN : ExtendedFloatingActionButton
    private lateinit var addPersonBTN : ExtendedFloatingActionButton

    private lateinit var informationsTV : TextView


    private lateinit var  animToBottom : Animation
    private lateinit var  animFromBottom : Animation
    private lateinit var animRotateOpen : Animation
    private lateinit var animRotateClose : Animation

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ToDo_Adapter

    private var todotasks : ArrayList<Task> = arrayListOf()

    private lateinit var restoredList : ArrayList<Task>

    private var  isManager = false
    private var leader = "false"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()





    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_to_do, container, false)
        animToBottom =  AnimationUtils.loadAnimation(view.context,R.anim.to_bottom_anim)
        animFromBottom = AnimationUtils.loadAnimation(view.context,R.anim.from_bottom_anim)
        animRotateOpen =  AnimationUtils.loadAnimation(view.context,R.anim.rotate_open_anim)
        animRotateClose =  AnimationUtils.loadAnimation(view.context,R.anim.rotate_close_anim)
        recyclerView = view.findViewById(R.id.ToDo_RV)
        expandBTN = view.findViewById(R.id.expand_options_BTN)
        addTaskBTN = view.findViewById(R.id.add_task_BTN)
        addPersonBTN = view.findViewById(R.id.add_person_BTN)
        informationsTV = view.findViewById(R.id.ftd_TV)


            getLIST(view)
            if(accCheck(view))
            {
                setVisibility(view)

            }


        return view
    }






    private fun accCheck(view: View): Boolean{
        val currentUser = auth.currentUser
        database.child("users").child(currentUser?.uid.toString()).child("manager").get()
            .addOnSuccessListener { it ->
                if(it.value == "true"){
                    isManager= true
                    setVisibility(view)
                }
            }
        return isManager
    }


    private fun getLIST(view: View){

        val db = Firebase.database
        val user = auth.currentUser

        val uid = user?.uid

        database.child("users").child(uid.toString()).child("manager").get().addOnSuccessListener {manager->
            if(manager.value == "true"){
                database.child("users").child(uid.toString()).child("username").get().addOnSuccessListener {
                    var nick = it.value.toString()

                    val userData=   db.getReference("/managers/$nick/tasks/ToDo")
                    userData.addValueEventListener(object: ValueEventListener {

                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onDataChange(snapshot: DataSnapshot) {
                            todotasks = arrayListOf<Task>()
                            if(snapshot.exists()){
                                recyclerView.visibility = View.VISIBLE
                                recyclerView.layoutManager = LinearLayoutManager(view.context)


                                for(userSnapshot in snapshot.children){
                                    val task = userSnapshot.getValue(Task::class.java)
                                    if (task != null) {
                                        todotasks.add(task)
                                    }

                                }
                                if(todotasks.size > 0){

                                    var listt = todotasks.sortedBy {
                                        SimpleDateFormat("dd-MM-yyyy HH:mm").parse(it.deadline).time
                                    }

                                    adapter = ToDo_Adapter(ArrayList(listt),nick)
                                    recyclerView.adapter = adapter

                                   // checkDate(ArrayList(listt),nick)

                                }
                                else{
                                    informationsTV.text = "No tasks, please create one"
                                    informationsTV.visibility = View.VISIBLE
                                }

                            }
                            else{
                                informationsTV.text = "No tasks, please create one"
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

                currUser.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.value != "null"){
                            informationsTV.visibility = View.GONE
                            val leader = snapshot.value.toString()

                            val userData=   db.getReference("/managers/$leader/tasks/ToDo")
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

                                            adapter = ToDo_Adapter(ArrayList(listt), leader)

                                            informationsTV.visibility = View.GONE

                                            recyclerView.adapter = adapter
                                            recyclerView.visibility = View.VISIBLE


                                        }
                                    }
                                    else{
                                        informationsTV.text = "No ToDo tasks found. Only leader is able to create tasks."
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
                            recyclerView.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })



            }

        }



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDate(arrayList: ArrayList<Task>, nick: String) {

        var newlist = arrayListOf<Task>()
        newlist.addAll(arrayList)
        val db = Firebase.database
        val currentTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val currentTime = LocalDateTime.now().format(currentTimeFormat).toString()
        val currencik = LocalDateTime.parse(currentTime, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
        for(i in arrayList){
            val time = LocalDateTime.parse(i.deadline, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
            var curr = time.compareTo(currencik)
            if(curr<0){
                newlist.remove(i)
                database.child("managers").child(nick).child("tasks").child("ToDo").push().child("deadline").get().addOnSuccessListener {
                    if(it.value.toString() == i.deadline){
                        it.ref.removeValue()
                        database.child("managers").child(nick).child("tasks").child("Undone").push().setValue(i)
                    }
                }

            }
        }
        adapter = ToDo_Adapter(ArrayList(newlist),nick)
        recyclerView.adapter = adapter

    }


    private fun setVisibility(view: View) {

        expandBTN.visibility = View.VISIBLE
        addTaskBTN.visibility = View.INVISIBLE
        addPersonBTN.visibility = View.INVISIBLE
        var clicked = false
        expandBTN.setOnClickListener(){
            clicked = !clicked
            setVisibility(clicked)
            setAnimation(clicked)
        }

        addPersonBTN.setOnClickListener(){
            val manager : FragmentManager = (it.context as FragmentActivity).supportFragmentManager

            AddEmployeeBottomSheet().show(manager, AddEmployeeBottomSheet().tag)
        }

        addTaskBTN.setOnClickListener(){
            val intent = Intent(it.context,AddTaskActivity::class.java)
            startActivity(intent)
            LoggedInActivity().finish()

        }


    }

    private fun setAnimation(clicked: Boolean) {
        if(clicked){
            addTaskBTN.startAnimation(animFromBottom)
            addPersonBTN.startAnimation(animFromBottom)
            expandBTN.startAnimation(animRotateOpen)
        }
        else{

            addTaskBTN.startAnimation(animToBottom)
            addPersonBTN.startAnimation(animToBottom)
            expandBTN.startAnimation(animRotateClose)
        }

    }

    private fun setVisibility(clicked: Boolean) {
        if(clicked){
            addTaskBTN.visibility = View.VISIBLE
            addPersonBTN.visibility = View.VISIBLE
        }
        else{
            addTaskBTN.visibility = View.INVISIBLE
            addPersonBTN.visibility = View.INVISIBLE
        }

    }

}