package com.dybich.todoapp

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
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

class EmployeeListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmployeeList_Adapter

    private lateinit var employeeList : ArrayList<Employee>


    private lateinit var noEmployeesTV : TextView
    private lateinit var managerTV : TextView

    private var  isManager = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.employee_list_layout)

        noEmployeesTV = findViewById(R.id.eli_no_employees_TV)
        managerTV = findViewById(R.id.employee_list_manager_TV)
        recyclerView = findViewById(R.id.eli_RV)



        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        val db = Firebase.database
        val user = auth.currentUser
        val uid = user?.uid

        database.child("users").child(uid.toString()).child("manager").get().addOnSuccessListener { manager ->
            if (manager.value == "true") {
                managerTV.text = "You"
                database.child("users").child(uid.toString()).child("username").get().addOnSuccessListener {
                    var nick = it.value.toString()

                    val userData=   db.getReference("/managers/$nick/employees")
                    userData.addValueEventListener(object: ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            employeeList = arrayListOf<Employee>()
                            if(snapshot.exists()){
                                recyclerView.visibility = View.VISIBLE
                                recyclerView.layoutManager = LinearLayoutManager(this@EmployeeListActivity)

                                for(userSnapshot in snapshot.children){


                                    val employee = userSnapshot.value.toString()
                                    if (employee != null ) {

                                        employeeList.add(Employee(employee))
                                    }
                                }
                                if(employeeList.size > 0){

                                    noEmployeesTV.visibility = View.GONE

                                    adapter = EmployeeList_Adapter(employeeList,true,nick)
                                    recyclerView.adapter = adapter
                                }

                            }
                            else{
                                noEmployeesTV.text = "No partners added to project yet."
                                noEmployeesTV.visibility = View.VISIBLE
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


                database.child("users").child(uid.toString()).child("email").get().addOnSuccessListener { nickname ->
                    var nick = nickname.value.toString()
                    val currUser=   db.getReference("/users/${uid.toString()}/leader")
                    currUser.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.value != "null"){

                                noEmployeesTV.visibility = View.GONE
                                val leader = snapshot.value.toString()
                                managerTV.text = leader
                                val userData=   db.getReference("/managers/$leader/employees")
                                userData.addValueEventListener(object: ValueEventListener {


                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        if(snapshot.exists()){
                                            employeeList = arrayListOf<Employee>()
                                            recyclerView.layoutManager = LinearLayoutManager(this@EmployeeListActivity)
                                            for(userSnapshot in snapshot.children){

                                                val employee = userSnapshot.value.toString()
                                                if(employee == nick){
                                                    employeeList.add(Employee("You"))
                                                }
                                                else{
                                                    employeeList.add(Employee(employee))
                                                }
                                            }


                                        }
                                        adapter = EmployeeList_Adapter(employeeList,false)
                                        noEmployeesTV.visibility = View.GONE
                                        recyclerView.adapter = adapter
                                        recyclerView.visibility = View.VISIBLE

                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })

                            }
                            else{
                                managerTV.text = "No leader yet"
                                noEmployeesTV.text = "Please ask your leader, to add you to the project"
                                noEmployeesTV.visibility = View.VISIBLE
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
    }
}