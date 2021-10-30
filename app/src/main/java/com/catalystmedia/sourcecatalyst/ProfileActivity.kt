package com.catalystmedia.sourcecatalyst

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.catalystmedia.sourcecatalyst.adapters.ProfileAdapter
import com.catalystmedia.sourcecatalyst.models.Profile
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_start_intern.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class ProfileActivity : AppCompatActivity() {
    val user = FirebaseAuth.getInstance().currentUser
    private var profileAdapter: ProfileAdapter? = null
    private var itemList: MutableList<Profile>? = null
    private var firebaseUser: FirebaseUser? = null
    var codeMain = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        var recyclerView: RecyclerView? = null
        recyclerView = findViewById(R.id.layout_profile_recycler)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        itemList = ArrayList()
        profileAdapter = ProfileAdapter(this, itemList as ArrayList<Profile>)
        recyclerView.adapter = profileAdapter
        getHistory()
        initActivity()
        startAnimationCustom()
        getCode()
        iv_back_profile.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                this,
                Pair.create(iv_profile_activity, "iv_profile_trans")
            )
            startActivity(intent, activityOptions.toBundle())
        }
    }

    private fun startAnimationCustom() {
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.text_animation)
        animation.interpolator = EasingInterpolator(Ease.EASE_OUT_EXPO)
        cv_ongoing.startAnimation(animation)
        layout_profile_recycler.startAnimation(animation)

    }

    private fun initActivity() {
        //load username and profile pic
        val userName = user!!.displayName.toString()
        Glide.with(this@ProfileActivity).load(user!!.photoUrl).into(iv_profile_activity)
        tv_username_profile.text = userName

        //set register date
        FirebaseDatabase.getInstance().reference.child("Users").child(user!!.uid).child("Info")
            .child("firstRegisterDate").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstDate = snapshot.value.toString()
                    val formatter = SimpleDateFormat("dd/MM/yyyy")
                    val date = formatter.parse(firstDate)
                    val desiredFormat = SimpleDateFormat("MMM yyyy").format(date)//Aug 2019
                    tv_date_first.text = "Member since $desiredFormat"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getHistory() {
        val historyRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser!!.uid.toString())
            .child("History")
        historyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    itemList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        val item = snapshot.getValue(Profile::class.java)
                        itemList!!.add(item!!)
                    }
                    profileAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getCode() {
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

    private fun nextCode() {
        val mainCode = codeMain.drop(4)
        var char5 = mainCode.dropLast(1)
        var char6 = mainCode.drop(1)
        if (char5 == "A") {
            re_course_img_profile.setImageResource(R.drawable.ic_android_4)
            re_tv_title_profile.text = "Android Development"
        } else if (char5 == "P") {
            re_course_img_profile.setImageResource(R.drawable.ic_python)
            re_tv_title_profile.text = "Python Development"
        }
        if (char6 == "B") {
            re_tv_level_profile.text = "Basic Level"
        } else if (char6 == "A") {
            re_tv_level_profile.text = "Advanced Level"
        }
    }
}