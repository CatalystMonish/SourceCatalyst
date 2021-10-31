package com.catalystmedia.sourcecatalyst

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_utilities.*

class UtilitiesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_utilities)
        iv_back_tools.setOnClickListener {
            finish()
        }
        cv_certificate.setOnClickListener {
            Toast.makeText(this, "Feature Not Implemented Yet!", Toast.LENGTH_SHORT).show()
        }
        cv_help_tools.setOnClickListener {
            Toast.makeText(this, "You may Join this Group to ask your Queries!", Toast.LENGTH_SHORT).show()
            var webpage = Uri.parse("https://chat.whatsapp.com/E2kFTLGWeVAJmp8dgwfMtH")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }
        cv_diagnostics.setOnClickListener {
            val intent = Intent(this@UtilitiesActivity, DiagnosticsActivity::class.java)
            startActivity(intent)
        }
        cv_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            val intent = Intent(this@UtilitiesActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}