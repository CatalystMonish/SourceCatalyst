package com.catalystmedia.sourcecatalyst

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class ProfileActivity : AppCompatActivity() {
    val user = FirebaseAuth.getInstance().currentUser
    private var profileAdapter: ProfileAdapter?= null
    private var itemList: MutableList<Profile> ?= null
    private var firebaseUser: FirebaseUser? = null


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
        iv_back_profile.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(iv_profile_activity, "iv_profile_trans"))
            startActivity(intent, activityOptions.toBundle())
        }
    }
    private fun startAnimationCustom() {
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.text_animation)
        animation.interpolator = EasingInterpolator(Ease.EASE_OUT_EXPO)
        layout_profile_recycler.startAnimation(animation)
    }
    private fun initActivity(){
        //load username and profile pic
        val userName = user!!.displayName.toString()
        Glide.with(this@ProfileActivity).load(user!!.photoUrl).into(iv_profile_activity)
        tv_username_profile.text = userName

        //set register date
        FirebaseDatabase.getInstance().reference.child("Users").child(user!!.uid).child("Info").child("firstRegisterDate").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
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
        val historyRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid.toString())
            .child("History")
        historyRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    itemList!!.clear()
                    for(snapshot in dataSnapshot.children){
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
}