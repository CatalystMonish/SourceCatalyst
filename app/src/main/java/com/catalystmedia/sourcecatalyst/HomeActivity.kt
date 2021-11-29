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


class HomeActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser
    private var todaysDate = ""
    var codeMain = ""
    var whatsappLink = ""
    var task1Trigger = false
    var task2Trigger = false
    var flipAccord = false
    var nodeCode = ""
    var globalCurrentTask = ""
    var task1Link = ""
    var task2Link = ""
    var task3Link = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initActivity()
        checkDate()
        startAnimationCustom()
        getCode()
        checkSubmissionStatus("TASK1")
        checkSubmissionStatus("TASK2")
        checkSubmissionStatus("TASK3")
        //onclickListeners
        btn_guide.setOnClickListener {
//            showResourceDialog()
            //TODO: Launch Doc Activity


            val currentTaskNo = globalCurrentTask
            val intent = Intent(this@HomeActivity, FinalDocumentsActivity::class.java)
            intent.putExtra("task", currentTaskNo)
            startActivity(intent)


        }
        iv_profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(this,
            Pair.create(iv_profile, "iv_profile_trans"))
            startActivity(intent, activityOptions.toBundle())
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this, UtilitiesActivity::class.java)
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
//            startAnimationCustom()
            getCode()
            checkSubmissionStatus("TASK1")
            checkSubmissionStatus("TASK2")
            checkSubmissionStatus("TASK3")
            Handler(Looper.getMainLooper()).postDelayed({
              home_swipe.isRefreshing = false
            }, 1000)
        }
        btn_show_home_ll.setOnClickListener {
            if (flipAccord) {
                btn_show_home_ll.setBackgroundResource(R.drawable.accordian_bg_top_round)
                tv_more_home.visibility = View.VISIBLE
                btn_show_home.setImageResource(R.drawable.ic_up)
                tv_more_home.setAlpha(0.0f)
                tv_more_home.animate()
                    .translationY(0F)
                    .alpha(1.0f)
                    .setListener(null)
                flipAccord =false
            }
            else if (!flipAccord){
                btn_show_home_ll.setBackgroundResource(R.drawable.accordian_bg)
                btn_show_home.setImageResource(R.drawable.ic_down)
                tv_more_home.animate()
                    .translationY(0F)
                    .alpha(0.0f)
                    .withEndAction {
                        tv_more_home.visibility = View.GONE
                    }
                flipAccord =true
            }
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
                        if(statusValue == "pending"){
                            tv_hide1_previous_status.setBackgroundResource(R.drawable.btn_bg_round_red)
                            tv_hide1_previous_status.text = statusValue
                        }
                        else{
                            tv_hide1_previous_status.setBackgroundResource(R.drawable.btn_bg_round_green)
                            tv_hide1_previous_status.text = statusValue
                        }
                        tv_task_no.text = "Task II"
                        globalCurrentTask = "task2"
                        task1Trigger = true
                        val pass = "TASK2"
                        runTaskChecker(pass)
                        setOngoingProblem(pass)
                        setProperProgress(pass)

                    } else if(taskVal == "TASK2"){
                        tv_hide_previous.visibility = View.VISIBLE
                        tv_hide2_previous.visibility = View.VISIBLE
                        if(statusValue == "pending"){
                            tv_hide2_previous_status.setBackgroundResource(R.drawable.btn_bg_round_red)
                            tv_hide2_previous_status.text = statusValue
                        }
                        else{
                            tv_hide2_previous_status.setBackgroundResource(R.drawable.btn_bg_round_green)
                            tv_hide2_previous_status.text = statusValue
                        }
                        tv_task_no.text = "Task III"
                        globalCurrentTask = "task3"
                        task2Trigger = true
                        val pass = "TASK3"
                        runTaskChecker(pass)
                        setOngoingProblem(pass)
                        setProperProgress(pass)
                    }
                    else if(taskVal =="TASK3"){
                        tv_task_no.text = "Task III"
                        globalCurrentTask = "task3"
                        runTaskChecker(taskVal)
                        completeInternShip()
                    }

                }
                else if(!task1Trigger && !task2Trigger){
                    //submission for both 1 and 2 dosent exist
                    val pass = "TASK1"
                    tv_task_no.text = "Task I"
                    globalCurrentTask = "task1"
                    setOngoingProblem(pass)
                    setProperProgress(pass)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun runTaskChecker(pass: String) {
        val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
            .child(pass).child("taskProblem").addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var getTask = snapshot.value.toString()
                        if(getTask ==""){
                            taskSelector(pass)
                        }
                    }
                    else{
                        taskSelector(pass)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    private fun taskSelector(pass: String) {
        val intent = Intent(this@HomeActivity, SelectTaskActivity::class.java)
        intent.putExtra("task",pass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun completeInternShip() {
        val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        Toast.makeText(this@HomeActivity, "Data Parceled and Uploaded", Toast.LENGTH_LONG).show()

        FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
            .child("TASK1").child("link").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        task1Link = snapshot.value.toString()
                        Log.d("TASK1LINK", task1Link)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
            .child("TASK2").child("link").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        task2Link = snapshot.value.toString()
                        Log.d("TASK2LINK", task2Link)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
            .child("TASK3").child("link").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        task3Link = snapshot.value.toString()
                        Log.d("TASK3LINK", task3Link)
                        Handler(Looper.getMainLooper()).postDelayed({
                            completeInternshipUpload() }, 1000)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }

    private fun completeInternshipUpload(){
        val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        //change ongoingStatus
        //if(activated!false but History exits take to profile make start internship button visible)
        //change activated false
        //parcel data into history save title, code, status, startdate, cetificateLink
        val mainCode = codeMain.drop(4)
        var char5 = mainCode.dropLast(1)
        var char6 = mainCode.drop(1)
        var nodeCode = char5+char6
        val historyNode:DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userUID).child("History")
        val taskMap = HashMap<String, Any>()
        taskMap["courseID"] = nodeCode
        taskMap["completionStatus"] = "completed"
        taskMap["startDate"] = todaysDate.toString()
        taskMap["certificateLink"] = "certificate"
        taskMap["task1Link"] = task1Link
        taskMap["task2Link"] = task2Link
        taskMap["task3Link"] = task3Link
        historyNode.push().updateChildren(taskMap).addOnCompleteListener { task->
            FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
                .child("TASK1").removeValue()
            FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
                .child("TASK2").removeValue()
            FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
                .child("TASK3").removeValue()
            FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
                .child("ongoingStatus").removeValue()
            FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
                .child("currentActivated").removeValue().addOnCompleteListener { task->
                    //take to new activity
                    val intent = Intent(this@HomeActivity, EndedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("isOld", true)
                    startActivity(intent)
                }
        }

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
                     taskMonitor(dayInTen.toLong(), pass)
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

    private fun taskMonitor(tvDays: Long, pass: String) {
        val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        //get current day (if day> 10 ) change task and save old task data to database and show previous task view

        //get current day
        val currentDay = tvDays.toInt()
    if(currentDay >= 10){
        completeTask(pass)
    }
    }

private fun completeTask(taskNo:String) {
    val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
   //check what info to save
    //start date //taskstatus //taskproblemCode //submissionStatus //submissionVerfiedStatus
    val taskNode:DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userUID).child(taskNo)
    val taskMap = HashMap<String, Any>()
    taskMap["status"] = "completed"
    taskMap["submissionStatus"] = "pending"
    taskNode.updateChildren(taskMap).addOnCompleteListener { task->
        if(task.isSuccessful){
            Toast.makeText(this@HomeActivity,"$taskNo completed succesfully!",Toast.LENGTH_SHORT).show()
            if(taskNo == "TASK1"){
                val newTaskNo = "TASK2"
            runTaskChecker(newTaskNo)}
            else if(taskNo == "TASK2"){
                val newTaskNo = "TASK3"
                runTaskChecker(newTaskNo)}
        }

    }
}

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
                        //TODO:END TASK
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
                    val taskArray = snapshot.value.toString().split(":")
                    tv_title_home.text = taskArray[0]
                    tv_more_home.text = taskArray[1]
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