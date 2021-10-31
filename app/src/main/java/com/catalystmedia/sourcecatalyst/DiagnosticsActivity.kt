package com.catalystmedia.sourcecatalyst

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_diagnostics.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.iv_profile_activity
import kotlinx.android.synthetic.main.activity_profile.tv_username_profile
import java.text.SimpleDateFormat
import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast


class DiagnosticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnostics)
        initActivity()
    }

    private fun initActivity() {
        val user = FirebaseAuth.getInstance().currentUser
        //load username and profile pic
        val userName = user!!.displayName.toString()
        Glide.with(this@DiagnosticsActivity).load(user!!.photoUrl).into(iv_diag_activity)
        tv_username_diag.text = userName
        val UID =  user!!.uid.toString()
        tv_uid.text = "UID:\n$UID"
        tv_uid.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("UID", UID)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this@DiagnosticsActivity, "Copied UID to Clipboard", Toast.LENGTH_SHORT).show()
        }

        tv_username_diag_copy.text = user!!.displayName.toString()
    }
}