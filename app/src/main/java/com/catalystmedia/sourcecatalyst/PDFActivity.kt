package com.catalystmedia.sourcecatalyst

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_pdfactivity.*
import java.io.File

class PDFActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfactivity)
        var Uri = intent.getStringExtra("uri")
        Toast.makeText(this@PDFActivity, Uri, Toast.LENGTH_LONG).show()
        pdfView.fromFile(File(Uri))
            .load();
    }
}