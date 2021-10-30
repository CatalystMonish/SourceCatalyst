package com.catalystmedia.sourcecatalyst

import android.app.Dialog
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
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_start_intern.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class StartInternActivity : AppCompatActivity() {
    var currentDate = ""
    var codeMain = ""
    var selectedTask = ""
    var flip = true
    lateinit var loadDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_intern)
        loadDialog = Dialog(this)
        checkStatus()
        initData()
        showLoadDialog()
        setDays()

        btn_start_intern.setOnClickListener {
            if(selectedTask!="") {
                changeData()
            }
            else{
                Toast.makeText(this@StartInternActivity, "Select a task by tapping on it!", Toast.LENGTH_SHORT).show()
            }
        }

        tv_task1.setOnClickListener {
                selectedTask = tv_task1.text.toString()
                //change bg
                tv_task1_bg.visibility = View.VISIBLE
                tv_task2_bg.visibility = View.INVISIBLE
                !flip
        }
        tv_task2.setOnClickListener {
                selectedTask = tv_task2.text.toString()
                //change bg
                tv_task1_bg.visibility = View.INVISIBLE
                tv_task2_bg.visibility = View.VISIBLE
                flip
            }

    }

    private fun setDays() {
        val date = Date()
        var df = SimpleDateFormat("dd/MM/yyyy")
        val c1 = Calendar.getInstance()
        currentDate = df.format(date) // get current date here
        c1.add(Calendar.DAY_OF_YEAR, 30)
        df = SimpleDateFormat("dd/MM/yyyy")
        val resultDate = c1.time
        val dueDate = df.format(resultDate)
        tv_date_total.text = currentDate + " to " + dueDate
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
                        setTaskData()
                        setTasks()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }



    private fun setTaskData() {
        val mainCode = codeMain.drop(4)
        var char5 = mainCode.dropLast(1)
        var char6 = mainCode.drop(1)


        if(char5 == "A"){
            iv_intern_start.setImageResource(R.drawable.ic_android_4)
            tv_intern_title.text = "Android Development"
        }
        else if(char5 == "P" ){
            iv_intern_start.setImageResource(R.drawable.ic_python)
            tv_intern_title.text = "Python Development"
        }
        if(char6 == "B"){
            tv_intern_level.text = "Basic Level"
        }
        else if(char6 == "A"){
            tv_intern_level.text = "Advanced Level"
        }

    }


    private fun setTasks() {
        //get task type
        val mainCode = codeMain.drop(4)
        var char5 = mainCode.dropLast(1)
        var char6 = mainCode.drop(1)
        var nodeCode = char5+char6
    Log.d("CODELOG", nodeCode)
        //get data from firebase

         FirebaseDatabase.getInstance().reference.child("Global").child("Internships")
             .child(nodeCode).child("task1").child("1").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){
                   tv_task1.text = snapshot.value.toString()
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
         FirebaseDatabase.getInstance().reference.child("Global").child("Internships")
            .child(nodeCode).child("task1").child("2").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        tv_task2.text = snapshot.value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        //display task

    }

    private fun checkStatus() {
        val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child("ongoingStatus").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val status = snapshot.value.toString().toBoolean()
                    if(status){
                        loadDialog.dismiss()
                        val intent = Intent(this@StartInternActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    else if(!status){
                        loadDialog.dismiss()
                        start_intern_ll.visibility = View.VISIBLE
                        cat_lottie.visibility = View.GONE
                        Toast.makeText(this@StartInternActivity, "No On going Tasks Detected: NEW INTERNSHIP", Toast.LENGTH_SHORT).show()
                    }

                }
                else{
                    loadDialog.dismiss()
                    start_intern_ll.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun showLoadDialog() {
        loadDialog.setContentView(R.layout.wait_dialog)
        loadDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        loadDialog.setCancelable(false)
        loadDialog.show()

    }
    private fun changeData() {
        val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child("ongoingStatus").setValue(true)
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child("currentStartDate").setValue(currentDate)
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child("TASK1").child("taskProblem").setValue(selectedTask)
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child("TASK1").child("currentStartDate").setValue(currentDate).addOnCompleteListener{
            Toast.makeText(this@StartInternActivity, "Data Changed", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@StartInternActivity, HomeActivity::class.java)
            startActivity(intent)
        }
        loadDialog.dismiss()
    }
}