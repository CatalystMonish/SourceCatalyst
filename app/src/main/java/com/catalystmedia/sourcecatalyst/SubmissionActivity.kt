package com.catalystmedia.sourcecatalyst

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_submission.*

class SubmissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submission)
        val taskNo = intent.getStringExtra("task")
        tv_submission_title.text = "Submission for $taskNo"
        simpleRatingBar.setOnRatingChangeListener { ratingBar, rating, fromUser ->
            Toast.makeText(this, "$rating",Toast.LENGTH_SHORT).show()
            rating_text.text = rating.toString()
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

    private fun makeSubmission(){
        //add Taskno to TASKNO



        //add Rating and other data to node



        //make home previous tasks visible and show status as pending


    }
}