package com.catalystmedia.sourcecatalyst

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*

import android.net.ConnectivityManager
import android.widget.Toast
import java.lang.Exception


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash)
            iv_splash.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate))
       if(isOnline()!!){
           Toast.makeText(this, "Connection Successful", Toast.LENGTH_SHORT).show()
           Handler(Looper.getMainLooper()).postDelayed({
               val intent = Intent(this, LoginActivity::class.java)
               startActivity(intent)
               finish()
           }, 2000)
       }
        else{
           tv_splash_bottom.text = "Unable to reach our Servers :("
           tv_splash_bottom.setTextColor(Color.parseColor("#c40000"))
        }
        }

    fun isOnline(): Boolean? {
        try {
            val p1 =
                Runtime.getRuntime().exec("ping -c 1 www.google.com")
            val returnVal = p1.waitFor()
            return returnVal == 0
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return false
    }
}