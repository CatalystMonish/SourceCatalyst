package com.catalystmedia.sourcecatalyst

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.catalystmedia.sourcecatalyst.adapters.VideosAdapter
import com.catalystmedia.sourcecatalyst.models.Documents
import com.catalystmedia.sourcecatalyst.models.Videos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VideoActivity : AppCompatActivity() {

    private var videosAdapter: VideosAdapter? = null
    private var itemList: MutableList<Videos>? = null
    var taskNo = ""
    var nodeCode = ""
    var codeMain = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)


        var recyclerView: RecyclerView? = null
        recyclerView = findViewById(R.id.layout_videos_recycler)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        itemList = ArrayList()
        videosAdapter = VideosAdapter(this, itemList as ArrayList<Videos>)
        recyclerView.adapter = videosAdapter
        getCode()
        taskNo = intent.getStringExtra("task").toString()
        Log.d("taskNoFinal", taskNo)
    }

        private fun getCode(){
            var usrEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
            Log.d("EMAIl", usrEmail)
            var chngEmail = usrEmail.replace('.', ',')
            FirebaseDatabase.getInstance().reference.child("Registrations").child(chngEmail)
                .child("activationCode").addListenerForSingleValueEvent(object :
                    ValueEventListener {
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
            var nodeCode = char5 + char6
            var collegeCode = codeMain.dropLast(4)
            Log.d("nodeCodeFinal", nodeCode)
            getDocuments(nodeCode,collegeCode)
        }

        private fun getDocuments(code:String,collegeCode:String) {
            FirebaseDatabase.getInstance().reference.child("Global").child("Internships")
                .child("Private").child(collegeCode.toString())
                .child(code)
                .child(taskNo).child("videos").addListenerForSingleValueEvent(object:
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            itemList!!.clear()
                            for (snapshot in snapshot.children) {
                                val item = snapshot.getValue(Videos::class.java)
                                itemList!!.add(item!!)
                            }
                            videosAdapter!!.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@VideoActivity, "Hello", Toast.LENGTH_LONG).show()
                    }

                })
        }
    }