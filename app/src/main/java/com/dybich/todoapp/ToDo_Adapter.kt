package com.dybich.todoapp

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ToDo_Adapter(private var taskList : ArrayList<Task>, private val nick: String ) : RecyclerView.Adapter<My_ViewHolder>() {
        private lateinit var database: DatabaseReference
        private lateinit var auth: FirebaseAuth

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): My_ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.card_cell, parent, false)
                return My_ViewHolder(layoutInflater)
        }

        override fun getItemCount(): Int {
                return taskList.size
        }


        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: My_ViewHolder, position: Int) {
                database = Firebase.database.reference

                var taskName = taskList[position].name
                var taskShortDescr = taskList[position].shortDescr
                var deadline = taskList[position].deadline
                var extraInfo = taskList[position].extraInfo
                var author = taskList[position].author
                var creationTime = taskList[position].creationTime
                var startedBy = taskList[position].startedBy
                var endedBy = taskList[position].endedBy
                holder.cellName.text = taskName
                holder.cellShortDescr.text = taskShortDescr
                holder.cellDeadline.text = deadline




                val card = holder.cellCard
                card.setOnClickListener() {

                        val intent = Intent(it.context, TaskDescriptionActivity::class.java)
                        intent.putExtra("taskName", taskName)
                        intent.putExtra("taskShortDescr", taskShortDescr)
                        intent.putExtra("deadline", deadline)
                        intent.putExtra("author", author)
                        intent.putExtra("extraInfo", extraInfo)
                        intent.putExtra("creationTime", creationTime)
                        intent.putExtra("startedBy", startedBy)
                        intent.putExtra("endedBy", endedBy)
                        it.context.startActivity(intent)
                }

//                var templist = arrayListOf<Task>()
//                templist.addAll(taskList)
//
//
//                val currentTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
//                val currentTime = LocalDateTime.now().format(currentTimeFormat).toString()
//                val currencik = LocalDateTime.parse(currentTime, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
//                for(i in taskList){
//                        val time = LocalDateTime.parse(i.deadline, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
//                        var curr = time.compareTo(currencik)
//                        if(curr<0){
//                                templist.remove(i)
//                                database.child("managers").child(nick).child("tasks").child("ToDo").push().child("deadline").get().addOnSuccessListener {
//                                        if(it.value.toString() == i.deadline){
//                                                it.ref.removeValue()
//                                                database.child("managers").child(nick).child("tasks").child("Undone").push().setValue(i)
//                                        }
//                                }
//
//                        }
//                }


                card.setOnLongClickListener {
                        val manager: FragmentManager = (it.context as FragmentActivity).supportFragmentManager

                        database = Firebase.database.reference
                        auth = FirebaseAuth.getInstance()
                        val currentUser = auth.currentUser
                        database.child("users").child(currentUser?.uid.toString()).child("manager").get()
                                .addOnSuccessListener { ismanager ->
                                        if (ismanager.value.toString() == "true") {

                                                if (taskName != null) {
                                                        ToDoBottomSheet.newInstance(taskName,"true").show(manager, ToDoBottomSheet().tag)
                                                }
                                        }
                                        else{
                                                if (taskName != null) {
                                                        ToDoBottomSheet.newInstance(taskName).show(manager, ToDoBottomSheet().tag)
                                                }
                                        }

                                }


                        true

                }
        }
}



class My_ViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
        val cellName : TextView = itemView.findViewById(R.id.name)
        val cellShortDescr : TextView = itemView.findViewById(R.id.short_Descr)
        val cellDeadline: TextView = itemView.findViewById(R.id.deadline)
        val cellCard : MaterialCardView = itemView.findViewById(R.id.my_task_Card)

}