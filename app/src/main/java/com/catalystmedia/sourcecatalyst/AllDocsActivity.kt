package com.catalystmedia.sourcecatalyst

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_all_docs.*

class AllDocsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_docs)
        cv_task_docs.setOnClickListener {
            val intent = Intent(this@AllDocsActivity, DocumentsActivity::class.java)
            startActivity(intent)
        }
    }
}