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
import android.os.Build
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.lang.Exception
import android.view.WindowManager





class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash)
        setStatusBarTransparent()



        iv_splash.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate))
       if(isOnline()!!){
//           Toast.makeText(this, "Connection Successful", Toast.LENGTH_SHORT).show()
           Handler(Looper.getMainLooper()).postDelayed({
               val intent = Intent(this, LoginActivity::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent);
           }, 5000)
       }
        else{
           iv_heart.visibility = View.GONE
           tv_splash_bottom.text = "Unable to reach our Servers :("
           tv_splash_bottom.setTextColor(Color.parseColor("#c40000"))
        }
        }
    private fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val decorView: View = window.getDecorView()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
            window.setStatusBarColor(Color.TRANSPARENT)
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