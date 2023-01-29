package com.dybich.todoapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user : FirebaseUser

    private lateinit var uid : String
    private lateinit var nickTV : TextView
    private lateinit var emailTV : TextView
    private lateinit var managerTV : TextView

    private lateinit var employeeBTN : Button
    private lateinit var undoneBTN : Button
    private  var isNickEmpty = true
    private  var isEmailEmpty= true
    private var isManagerEmpty =true

    private var nick :String? = "null"
    private var email  = "null"
    private var manager = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        uid = user?.uid.toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)
        nickTV = view.findViewById(R.id.profile_nick_TV)
        emailTV = view.findViewById(R.id.profile_email_TV)
        managerTV = view.findViewById(R.id.profile_manager_TV)

        employeeBTN = view.findViewById(R.id.profile_employee_BTN)
        undoneBTN = view.findViewById(R.id.profile_undone_BTN)


        if(savedInstanceState!=null){
            val db = Firebase.database
            val user = auth.currentUser
            val uid = user?.uid
            val userData=   db.getReference("/users/$uid/")
            userData.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    nick = snapshot.child("username").getValue() as String
                   manager = snapshot.child("manager").getValue() as String
                    email = snapshot.child("email").getValue() as String
                    nickTV.text = nick
                    emailTV.text = email
                    if(manager == "true"){
                        managerTV.text = "You"
                    }
                    else{
                        manager = snapshot.child("leader").getValue() as String

                        if(manager == "null"){
                            managerTV.text = "No leader yet"
                        }
                        else{
                            managerTV.text = manager
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        }
        else{
            val db = Firebase.database
            val user = auth.currentUser
            val uid = user?.uid
            val userData=   db.getReference("/users/$uid/")
            userData.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    nick = snapshot.child("username").value.toString()
                    manager = snapshot.child("manager").value.toString()
                    email = snapshot.child("email").value.toString()
                    nickTV.text = nick
                    emailTV.text = email
                    if(manager == "true"){
                        managerTV.text = "You"
                    }
                    else{
                        manager = snapshot.child("leader").getValue() as String

                        if(manager == "null"){
                            managerTV.text = "No leader yet"
                        }
                        else{
                            managerTV.text = manager
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }


        employeeBTN.setOnClickListener(){
            val intent = Intent(it.context, EmployeeListActivity::class.java)
            startActivity(intent)
        }
        undoneBTN.setOnClickListener(){

        }

        logout(view)




        return view
    }



    private fun logout(view: View){
        val logout = view.findViewById<Button>(R.id.profile_logout_BTN)
        logout.setOnClickListener(){
            requireActivity().run {

                Firebase.auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))

                finishAffinity()
                finish()

            }
        }
    }
}