package com.catalystmedia.sourcecatalyst

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.loading_dialog.*
import kotlinx.android.synthetic.main.resources_dialog.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import kotlinx.android.synthetic.main.activity_start_intern.*
import java.util.Collections.rotate


class HomeActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser
    private var todaysDate = ""
    var codeMain = ""
    var whatsappLink = ""
    var task1Trigger = false
    var task2Trigger = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initActivity()
        checkDate()
        startAnimationCustom()
        getCode()
        checkSubmissionStatus("TASK1")
        checkSubmissionStatus("TASK2")
        //onclickListeners
        btn_guide.setOnClickListener {
            showResourceDialog()
        }
        iv_profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(this,
            Pair.create(iv_profile, "iv_profile_trans"))
            startActivity(intent, activityOptions.toBundle())
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this, EndActivity::class.java)
            startActivity(intent)
            Animatoo.animateSlideRight(this)
        }

        btn_submission.setOnClickListener {
            val intent = Intent(this, SubmissionActivity::class.java)
            intent.putExtra("task", (tv_task_no.text.toString()))
            startActivity(intent)
        }
        home_swipe.setOnRefreshListener {
            initActivity()
            checkDate()
            startAnimationCustom()
            getCode()
            checkSubmissionStatus("TASK1")
            checkSubmissionStatus("TASK2")
            Handler(Looper.getMainLooper()).postDelayed({
              home_swipe.isRefreshing = false
            }, 1000)
        }
    }

    private fun startAnimationCustom() {
        val animation:Animation = AnimationUtils.loadAnimation(this, R.anim.text_animation)
        animation.interpolator = EasingInterpolator(Ease.EASE_OUT_EXPO)
        home_swipe.startAnimation(animation)
    }

    private fun getCode(){
        var usrEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        Log.d("EMAIl", usrEmail)
        var chngEmail = usrEmail.replace('.', ',')
        FirebaseDatabase.getInstance().reference.child("Registrations").child(chngEmail)
            .child("activationCode").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        codeMain = snapshot.value.toString()
                        Log.d("CODEMAIN", codeMain)

                        nextCode()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun checkSubmissionStatus(taskVal:String){
        val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(userUID).child(taskVal).child("submissionStatus").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var statusValue = snapshot.value.toString()
                    if (taskVal == "TASK1") {
                        tv_hide_previous.visibility = View.VISIBLE
                        tv_hide1_previous.visibility = View.VISIBLE
                        tv_hide1_previous_status.text = statusValue
                        tv_task_no.text = "Task II"
                        task1Trigger = true
                        val pass = "TASK2"
                        setOngoingProblem(pass)
                        setProperProgress(pass)

                    } else if(taskVal == "TASK2"){
                        tv_hide_previous.visibility = View.VISIBLE
                        tv_hide2_previous.visibility = View.VISIBLE
                        tv_hide2_previous_status.text = statusValue
                        tv_task_no.text = "Task III"
                        task2Trigger = true
                        val pass = "TASK3"
                        setOngoingProblem(pass)
                        setProperProgress(pass)
                    }

                }
                else if(!task1Trigger && !task2Trigger){
                    //submission for both 1 and 2 dosent exist
                    val pass = "TASK1"
                    tv_task_no.text = "Task I"
                    setOngoingProblem(pass)
                    setProperProgress(pass)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun setProperProgress(pass: String) {
        val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
            .child(pass).child("currentStartDate").addListenerForSingleValueEvent(object:ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.exists()){
                     //add 10 days to currentStartDate
                     var dateInString = snapshot.value.toString()
                     tv_task_start_date.text = "Start Date - "+ dateInString
                     Log.d("DATEIWANT", dateInString)
                     //find days remaining
                     val nowDate = todaysDate.toString()
                     val date1: Date
                     val date2: Date
                     val dates = SimpleDateFormat("dd/MM/yyyy")
                     date1 = dates!!.parse(dateInString)
                     date2 = dates!!.parse(nowDate)
                     Log.d("start", dateInString)
                     Log.d("today", nowDate)
                     val difference: Long = kotlin.math.abs(date1.time - date2.time)
                     val differenceDates = difference / (24 * 60 * 60 * 1000)
                     val tvDays = differenceDates
                     var dayInTen = tvDays.toFloat()
                     var dayInTenRe = (10 - dayInTen).toInt()
                     progress_day.progress = dayInTen
                     tv_days_left.text =  "$dayInTenRe Days Left"
                 }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun nextCode() {
        val mainCode = codeMain.drop(4)
        var char5 = mainCode.dropLast(1)
        var char6 = mainCode.drop(1)
        var nodeCode = char5+char6
        Log.d("HomeCode", nodeCode)
        FirebaseDatabase.getInstance().reference.child("Global").child("Internships")
            .child(nodeCode).child("groupLink").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        whatsappLink = snapshot.value.toString()
                        Log.d("LinkWP", whatsappLink)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        fab_help.setOnClickListener {
            Toast.makeText(this@HomeActivity,"You May Join this Group",Toast.LENGTH_SHORT).show()
            var webpage = Uri.parse(whatsappLink)

            if (!whatsappLink.startsWith("http://") && !whatsappLink.startsWith("https://")) {
                webpage = Uri.parse("http://$whatsappLink")
            }

            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }
    }

//    private fun taskMonitor(tvDays: Long) {
//        val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
//        //get current day (if day> 10 ) change task and save old task data to databse and show previous task view
//
//        //get current day
//        val currentDay = tvDays.toInt()
//        when {
//            currentDay in 10..19 -> {
//                //task 1 should be complete
//                FirebaseDatabase.getInstance().reference.child("Users").child(userUID).child("TASK1").child("status")
//                    .addListenerForSingleValueEvent(object : ValueEventListener{
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            if(snapshot.exists()){
//                                val status = snapshot.value.toString()
//                                if(status == "onGoing"){
//                                    val taskNo = "TASK1"
//                                    completeTask(taskNo)
//                                }
//                                else if (status == "completed"){
//                                    //task already completed do nothing
//                                    Toast.makeText(this@HomeActivity, "Already on Task 2", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                            else{
//                                    val taskNo = "TASK1"
//                                    completeTask(taskNo)
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            TODO("Not yet implemented")
//                        }
//                    })}
//            currentDay in 20..29 -> {
//                //task 2 should be complete
//                FirebaseDatabase.getInstance().reference.child("Users").child(userUID).child("TASK2").child("status")
//                    .addListenerForSingleValueEvent(object : ValueEventListener{
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            if(snapshot.exists()){
//                                val status = snapshot.value.toString()
//                                if(status == "onGoing"){
//                                    val taskNo = "TASK2"
//                                    completeTask(taskNo)
//                                }
//                                else if (status == "completed"){
//                                    //task already completed do nothing
//                                    Toast.makeText(this@HomeActivity, "Already on Task 3", Toast.LENGTH_SHORT).show()
//                                }
//
//                            }
//                            else {
//                                val taskNo = "TASK2"
//                                completeTask(taskNo)
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            TODO("Not yet implemented")
//                        }
//                    })}
//            currentDay >= 30 -> {
//
//                //task 3 should be complete
//                //save and delete all task data and complete the internship!!
//                Toast.makeText(this@HomeActivity, "Your Internship Should be complete", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//private fun completeTask(taskNo:String) {
//    val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
//   //check what info to save
//    //start date //taskstatus //taskproblemCode //submissionStatus //submissionVerfiedStatus
//    val taskNode:DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userUID).child(taskNo)
//    val taskMap = HashMap<String, Any>()
//    taskMap["status"] = "completed"
//    taskMap["taskProblemCode"] = "task code goes here"
//    taskNode.updateChildren(taskMap).addOnCompleteListener { task->
//        if(task.isSuccessful){
//            Toast.makeText(this@HomeActivity,"$taskNo completed succesfully!",Toast.LENGTH_SHORT).show()
//        }
//    }
//}

    private fun initActivity() {

        //TODO:check for banners, warnings etc.


        //get Username and Photo from google
        val userName = user!!.displayName.toString()
        Glide.with(this@HomeActivity).load(user!!.photoUrl).into(iv_profile)
        tv_user_welcome.text = "Hello, $userName"
    }



    private fun checkDate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            todaysDate =  current.format(formatter)
            setDay()
        } else {
            var date = Date()
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            todaysDate = formatter.format(date)
            setDay()
        }

    }

    private fun setDay(){
        FirebaseDatabase.getInstance().reference.child("Users").child(user!!.uid).child(
            "currentStartDate").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val startDate = snapshot.value.toString()
                    val nowDate = todaysDate.toString()
                    val date1: Date
                    val date2: Date
                    val dates = SimpleDateFormat("dd/MM/yyyy")
                    date1 = dates!!.parse(startDate)
                    date2 = dates!!.parse(nowDate)
                    val difference: Long = kotlin.math.abs(date1.time - date2.time)
                    val differenceDates = difference / (24 * 60 * 60 * 1000)
                    val tvDays = differenceDates + 1
//                    taskMonitor(tvDays)
                    if (tvDays in 1..9) {
//                        tv_task_no.text = "Task I"
                        tv_top_day.text = "Day 0$tvDays"
//                        var dayInTen = tvDays.toFloat()
//                        progress_day.progress = dayInTen
//                        var dayInTenRe = (10 - dayInTen).toInt()
//                        tv_days_left.text = "$dayInTenRe Days Left"
//                        val pass = "TASK1"
//                        setOngoingProblem(pass)
                    } else if (tvDays in 10..19) {
//                        tv_task_no.text = "Task II"
                        tv_top_day.text = "Day $tvDays"
                        tv_hide_previous.visibility = View.VISIBLE
                        tv_hide1_previous.visibility = View.VISIBLE
//                        var dayInTen = (tvDays - 10).toFloat()
//                        var dayInTenRe = (10 - dayInTen).toInt()
//                        progress_day.progress = dayInTen
//                        tv_days_left.text =  "$dayInTenRe Days Left"
//                        val pass = "TASK2"
//                        setOngoingProblem(pass)
                    }
                    else if (tvDays in 20..30) {
//                        tv_task_no.text = "Task III"
                        tv_top_day.text = "Day $tvDays"
//                        var dayInTen = (tvDays - 20).toFloat()
                        tv_hide_previous.visibility = View.VISIBLE
                        tv_hide1_previous.visibility = View.VISIBLE
                        tv_hide2_previous.visibility = View.VISIBLE
//                        progress_day.progress = dayInTen
//                        var dayInTenRe = (10 - dayInTen).toInt()
//                        tv_days_left.text =  "$dayInTenRe Days Left"
//                        val pass = "TASK3"
//                        setOngoingProblem(pass)
                    }
                    else {
                        //day exceeds 30
                        tv_top_day.text = "Error in your data contact for Help!"
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setOngoingProblem(pass: String) {
        val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child(pass).child("taskProblem").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    tv_current_problem.text = snapshot.value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    fun showResourceDialog() {
        val alertDialog = Dialog(this)
        alertDialog.setContentView(R.layout.resources_dialog_new)
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.btn_close_resources.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
}