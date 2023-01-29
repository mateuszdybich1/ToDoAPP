package com.dybich.todoapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Doing_Adapter(private val taskList : List<Task>) : RecyclerView.Adapter<My_ViewHolder>() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): My_ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.card_cell, parent, false)
        return My_ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }


    override fun onBindViewHolder(holder: My_ViewHolder, position: Int) {
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


        card.setOnLongClickListener {
            val manager: FragmentManager = (it.context as FragmentActivity).supportFragmentManager

            database = Firebase.database.reference
            auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            database.child("users").child(currentUser?.uid.toString()).child("manager").get()
                .addOnSuccessListener { ismanager ->
                    if (ismanager.value.toString() == "true") {

                        if (taskName != null) {
                            DoingBottomSheet.newInstance(taskName,"true").show(manager, DoingBottomSheet().tag)
                        }
                    }
                    else{
                        if (taskName != null) {
                            DoingBottomSheet.newInstance(taskName).show(manager, DoingBottomSheet().tag)
                        }
                    }

                }


            true

        }
    }
}



