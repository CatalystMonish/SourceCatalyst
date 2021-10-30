package com.catalystmedia.sourcecatalyst

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_submission.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SubmissionActivity : AppCompatActivity() {
     var taskNo = ""
    var todaysDate = ""
    var ratingGiven = ""
    var triggeredRate = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission)
        checkDate()
        taskNo = intent.getStringExtra("task").toString()
        tv_submission_title.text = "Submission for $taskNo"
        simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
            triggeredRate = true
            ratingGiven = rating.toInt().toString()
        }
        btn_create_submission.setOnClickListener {
            if(et_github_link.text.isNotEmpty()){
                makeSubmission()
            }
            else {
                Toast.makeText(this, "Enter your github Repository Link", Toast.LENGTH_SHORT).show()
            }
        }
        tv_text_github.setOnClickListener {

        }
    }
    private fun checkDate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            todaysDate =  current.format(formatter)
        } else {
            var date = Date()
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            todaysDate = formatter.format(date)
        }

    }

    private fun makeSubmission(){
        if(triggeredRate) {
            var taskNoUpper = ""
            if(taskNo == "Task I"){
                taskNoUpper = "TASK1"
            }
            else if(taskNo == "Task II"){
                taskNoUpper = "TASK2"
            }
            else if(taskNo == "Task III"){
                taskNoUpper = "TASK3"
            }
            Log.d("UPPER", taskNoUpper)
            val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val taskNode: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("Users").child(userUID)
                    .child(taskNoUpper)
            val taskMap = HashMap<String, Any>()
            taskMap["submissionStatus"] = "submitted"
            taskMap["submissionDate"] = todaysDate
            taskMap["rating"] = ratingGiven
            taskNode.updateChildren(taskMap).addOnCompleteListener { task->
                if(task.isSuccessful){
                    //move to nextTask
                    Toast.makeText(this@SubmissionActivity,"Task Submission Done Successfully!",Toast.LENGTH_SHORT).show()
                    //showerConfetti
                    lottie_celb.visibility = View.VISIBLE
                    lottie_celb.playAnimation()
                }
            }
        }

        else {
            Toast.makeText(this@SubmissionActivity, "Please select a rating!", Toast.LENGTH_SHORT).show()
        }
        //add Rating and other data to node



        //make home previous tasks visible and show status as pending


    }
}