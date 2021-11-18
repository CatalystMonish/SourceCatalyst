package com.catalystmedia.sourcecatalyst

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash.*
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
    var flipAccord = true
    var flipAccord1 = true
    var globalTask1 = ""
    var globalTask2 = ""
    lateinit var loadDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_intern)
        loadDialog = Dialog(this)
        checkStatus()
        initData()
        showLoadDialog()
        setDays()
        btn_show1_ll.setOnClickListener {
            if (flipAccord) {
                val height = tv_more1.getHeight().toFloat()
                tv_more1.visibility = View.VISIBLE
                btn_show1_ll.setBackgroundResource(R.drawable.accordian_bg_top_round)
                btn_show1.setImageResource(R.drawable.ic_up)
                tv_more1.setAlpha(0.0f);
                tv_more1.animate()
                    .translationY(0F)
                    .alpha(1.0f)
                    .setListener(null);
                flipAccord =false
            }
            else if (!flipAccord){
                btn_show1.setImageResource(R.drawable.ic_down)
                btn_show1_ll.setBackgroundResource(R.drawable.accordian_bg)
                tv_more1.animate()
                    .translationY(0F)
                    .alpha(0.0f)
                    .withEndAction {
                        tv_more1.visibility = View.GONE
                    }
                flipAccord =true
            }
        }
        btn_show2_ll.setOnClickListener {
            if (flipAccord1) {
                tv_more2.visibility = View.VISIBLE
                btn_show2.setImageResource(R.drawable.ic_up)
                btn_show2_ll.setBackgroundResource(R.drawable.accordian_bg_top_round)
                tv_more2.setAlpha(0.0f);
                tv_more2.animate()
                    .translationY(0F)
                    .alpha(1.0f)
                    .setListener(null);
                flipAccord1 =false
            }
            else if (!flipAccord1){
                btn_show2.setImageResource(R.drawable.ic_down)
                btn_show2_ll.setBackgroundResource(R.drawable.accordian_bg)
                tv_more2.animate()
                    .translationY(0F)
                    .alpha(0.0f)
                    .withEndAction {
                        tv_more2.visibility = View.GONE
                    }
                flipAccord1 =true
            }
        }

        btn_start_intern.setOnClickListener {
            if(selectedTask!="") {
                changeData()
            }
            else{
                Toast.makeText(this@StartInternActivity, "Select a task by tapping on it!", Toast.LENGTH_SHORT).show()
            }
        }

        rb_task1.setOnClickListener {
                selectedTask = globalTask1
                //change bg
            rb_task2.isChecked = false
                !flip
        }
        rb_task2.setOnClickListener {
                selectedTask = globalTask2
                //change bg
            rb_task1.isChecked = false
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
        else if(char5 == "W" ){
            iv_intern_start.setImageResource(R.drawable.ic_web)
            tv_intern_title.text = "Web Development"
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
                   globalTask1 = snapshot.value.toString()
                   val taskArray = snapshot.value.toString().split(":")
                   tv_title1.text = taskArray[0]
                   tv_more1.text = taskArray[1]
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
                        globalTask2 = snapshot.value.toString()
                        val taskArray = snapshot.value.toString().split(":")
                        tv_title2.text = taskArray[0]
                        tv_more2.text = taskArray[1]
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
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        loadDialog.dismiss()
    }
}