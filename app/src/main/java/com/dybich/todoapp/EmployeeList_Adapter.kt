package com.dybich.todoapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EmployeeList_Adapter(private val employeeList : List<Employee>, private val menager : Boolean?=false,private val leader : String?="null") : RecyclerView.Adapter<Employee_ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Employee_ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.employee_list_item, parent, false)
        return Employee_ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }


    override fun onBindViewHolder(holder: Employee_ViewHolder, position: Int) {

        var name = holder.nameTV

        var currentName = employeeList[position].name
        name.text = currentName
        var deleteBTN = holder.deleteBTN

        val database = Firebase.database.reference
        val auth = FirebaseAuth.getInstance()
        val db = Firebase.database
        val user = auth.currentUser
        val uid = user?.uid
        if (menager == true) {

            deleteBTN.setOnClickListener() {


                val userData = db.getReference("/users")
                userData.addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {

                            for (userSnapshot in snapshot.children) {

                                var email = userSnapshot.child("email").value.toString()

                                if (email == currentName) {

                                    userSnapshot.child("leader").ref.setValue("null")
                                }
                            }


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                val employeePath = db.getReference("/managers/$leader/employees")
                employeePath.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            for (userSnapshot in snapshot.children) {

                                var email = userSnapshot.value.toString()

                                if (email == currentName) {
                                    userSnapshot.ref.removeValue()
                                    Toast.makeText(it.context,"Employee removed from project",Toast.LENGTH_LONG).show()
                                }
                            }


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })


            }
        } else {
            deleteBTN.visibility = View.GONE
        }

    }
}



class Employee_ViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){

    val nameTV : TextView = itemView.findViewById(R.id.eli_nickname_TV)

    val deleteBTN : Button = itemView.findViewById(R.id.eli_delete_BTN)



}