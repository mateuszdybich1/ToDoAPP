package com.dybich.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class TaskDescriptionActivity : AppCompatActivity() {

    private lateinit var name_TV : TextView
    private lateinit var taskShortDescr_TV : TextView
    private lateinit var deadline_TV : TextView
    private lateinit var author_TV : TextView
    private lateinit var extraInfo_TV : TextView
    private lateinit var creationTime_TV : TextView
    private lateinit var startedBy_TV : TextView
    private lateinit var endedBy_TV : TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_description_layout)

        val name : String = intent.getStringExtra("taskName").toString()
        val taskShortDescr : String = intent.getStringExtra("taskShortDescr").toString()
        val deadline : String = intent.getStringExtra("deadline").toString()
        val author : String = intent.getStringExtra("author").toString()
        val extraInfo : String = intent.getStringExtra("extraInfo").toString()
        val creationTime : String = intent.getStringExtra("creationTime").toString()
        val startedBy : String = intent.getStringExtra("startedBy").toString()
        val endedBy : String = intent.getStringExtra("endedBy").toString()


        name_TV = findViewById(R.id.tdl_name_TV)
        taskShortDescr_TV = findViewById(R.id.tdl_sd_TV)
        deadline_TV = findViewById(R.id.tdl_deadline_TV)
        author_TV = findViewById(R.id.tdl_author_TV)
        extraInfo_TV = findViewById(R.id.tdl_extraInfo_TV)
        creationTime_TV = findViewById(R.id.tdl_creationTime_TV)
        startedBy_TV = findViewById(R.id.tdl_startedBy_TV)
        endedBy_TV = findViewById(R.id.tdl_endedBy_TV)

        name_TV.text = name
        taskShortDescr_TV.text = taskShortDescr
        deadline_TV.text = deadline
        author_TV.text = author
        extraInfo_TV.text = extraInfo
        creationTime_TV.text = creationTime
        startedBy_TV.text = startedBy
        endedBy_TV.text = endedBy


    }
}