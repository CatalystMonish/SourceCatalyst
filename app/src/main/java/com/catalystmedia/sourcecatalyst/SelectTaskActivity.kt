package com.catalystmedia.sourcecatalyst

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_select_task.*
import kotlinx.android.synthetic.main.activity_start_intern.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SelectTaskActivity : AppCompatActivity() {
    var currentDate = ""
    var getTaskIntent = ""
    var codeMain = ""
    var selectedTask = ""
    var flip = true
    var flipAccord = true
    var flipAccord1 = true
    var globalTask1 = ""
    var globalTask2 = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_task)
        getTaskIntent = intent.getStringExtra("task").toString()
        checkDate()
        initData()
        btn_show1_select_ll.setOnClickListener {
            if (flipAccord) {
                btn_show1_select_ll.setBackgroundResource(R.drawable.accordian_bg_top_round)
                tv_more1_select.visibility = View.VISIBLE
                btn_show1_select.setImageResource(R.drawable.ic_up)
                tv_more1_select.setAlpha(0.0f);
                tv_more1_select.animate()
                    .translationY(0F)
                    .alpha(1.0f)
                    .setListener(null);
                flipAccord =false
            }
            else if (!flipAccord){
                btn_show1_select_ll.setBackgroundResource(R.drawable.accordian_bg)
                btn_show1_select.setImageResource(R.drawable.ic_down)
                tv_more1_select.animate()
                    .translationY(0F)
                    .alpha(0.0f)
                    .withEndAction {
                        tv_more1_select.visibility = View.GONE
                    }
                flipAccord =true
            }
        }
        btn_show2_select_ll.setOnClickListener {
            if (flipAccord1) {
                btn_show2_select_ll.setBackgroundResource(R.drawable.accordian_bg_top_round)
                tv_more2_select.visibility = View.VISIBLE
                btn_show2_select.setImageResource(R.drawable.ic_up)
                tv_more2_select.setAlpha(0.0f);
                tv_more2_select.animate()
                    .translationY(0F)
                    .alpha(1.0f)
                    .setListener(null);
                flipAccord1 =false
            }
            else if (!flipAccord1){
                btn_show2_select_ll.setBackgroundResource(R.drawable.accordian_bg)
                btn_show2_select.setImageResource(R.drawable.ic_down)
                tv_more2_select.animate()
                    .translationY(0F)
                    .alpha(0.0f)
                    .withEndAction {
                        tv_more2_select.visibility = View.GONE
                    }
                flipAccord1 =true
            }
        }
        rb_task1_select.setOnClickListener {
            selectedTask = globalTask1
            //change bg
            rb_task2_select.isChecked = false
            !flip
        }
        rb_task2_select.setOnClickListener {
            selectedTask = globalTask2
            //change bg
            rb_task1_select.isChecked = false
            flip
        }

        btn_task_new_select.setOnClickListener {
            if(selectedTask!="") {
                changeData()
                Toast.makeText(this@SelectTaskActivity, "Changing Data", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@SelectTaskActivity, "Select a task by tapping on it!", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun checkDate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            currentDate =  current.format(formatter)

        } else {
            var date = Date()
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            currentDate = formatter.format(date)

        }

    }

    private fun changeData() {
        val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child("ongoingStatus").setValue(true)
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child(getTaskIntent).child("taskProblem").setValue(selectedTask)
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child(getTaskIntent).child("currentStartDate").setValue(currentDate).addOnCompleteListener{
            Toast.makeText(this@SelectTaskActivity, "Data Changed", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SelectTaskActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun initData() {
        var usrEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        Log.d("EMAIl", usrEmail)
        var chngEmail = usrEmail.replace('.', ',')
        FirebaseDatabase.getInstance().reference.child("Registrations").child(chngEmail)
            .child("activationCode").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        codeMain = snapshot.value.toString()
                        Log.d("CODEMAIN", codeMain)
                        setText()
                        loadTasks()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }


    private fun loadTasks() {
        //get task type
        val mainCode = codeMain.drop(4)
        var char5 = mainCode.dropLast(1)
        var char6 = mainCode.drop(1)
        var nodeCode = char5+char6
        val taskNo = getTaskIntent.toLowerCase().toString()
        FirebaseDatabase.getInstance().reference.child("Global").child("Internships")
            .child(nodeCode).child(taskNo).child("1").addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        globalTask1 = snapshot.value.toString()
                        val taskArray = snapshot.value.toString().split(":")
                        tv_title1_select.text = taskArray[0]
                        tv_more1_select.text = taskArray[1]
                    }
                    else{
                        globalTask1 = "test:001"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        FirebaseDatabase.getInstance().reference.child("Global").child("Internships")
            .child(nodeCode).child(taskNo).child("2").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        globalTask2 = snapshot.value.toString()
                        val taskArray = snapshot.value.toString().split(":")
                        tv_title2_select.text = taskArray[0]
                        tv_more2_select.text = taskArray[1]
                    }
                    else{
                        btn_show2_select_ll.visibility = View.GONE
                        globalTask2 = "test:002"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        //display task

    }


    private fun setText() {
        if(getTaskIntent == "TASK2"){
        tv_task_val.text = "Congrats end of TASK 1 🎉"}
        else if(getTaskIntent == "TASK3"){
            tv_task_val.text = "Congrats end of TASK 2 🎉"}
    }
}
