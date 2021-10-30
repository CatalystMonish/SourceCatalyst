package com.catalystmedia.sourcecatalyst

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_activation.*
import kotlinx.android.synthetic.main.loading_dialog.*


class ActivationActivity : AppCompatActivity() {
    var flip: Boolean = false
    lateinit var shared: SharedPreferences
    lateinit var loadDialog: Dialog
    var mainCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shared = getPreferences(Context.MODE_PRIVATE)
        setContentView(R.layout.activity_activation)
        loadDialog = Dialog(this)
        showLoadDialog()
        checkIfActivated()
        et_pass_1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (et_pass_1.text.isNotEmpty()) {
                    et_pass_1.clearFocus()
                    et_pass_2.requestFocus()
                    et_pass_2.isCursorVisible = true

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        et_pass_2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (et_pass_2.text.isNotEmpty()) {
                    et_pass_2.clearFocus()
                    et_pass_3.requestFocus()
                    et_pass_3.isCursorVisible = true

                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (et_pass_2.text.isEmpty()) {
                    et_pass_2.clearFocus()
                    et_pass_1.requestFocus()
                    et_pass_1.isCursorVisible = true
                }
            }
        })
        et_pass_3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (et_pass_3.text.isNotEmpty()) {
                    et_pass_3.clearFocus()
                    et_pass_4.requestFocus()
                    et_pass_4.isCursorVisible = true

                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (et_pass_3.text.isEmpty()) {
                    et_pass_3.clearFocus()
                    et_pass_2.requestFocus()
                    et_pass_2.isCursorVisible = true
                }
            }
        })
        et_pass_4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (et_pass_4.text.isNotEmpty()) {
                    et_pass_4.clearFocus()
                    et_pass_5.requestFocus()
                    et_pass_5.isCursorVisible = true

                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (et_pass_4.text.isEmpty()) {
                    et_pass_4.clearFocus()
                    et_pass_3.requestFocus()
                    et_pass_3.isCursorVisible = true
                }
            }
        })
        et_pass_5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (et_pass_5.text.isNotEmpty()) {
                    et_pass_5.clearFocus()
                    et_pass_6.requestFocus()
                    et_pass_6.isCursorVisible = true

                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (et_pass_5.text.isEmpty()) {
                    et_pass_5.clearFocus()
                    et_pass_4.requestFocus()
                    et_pass_4.isCursorVisible = true
                }
            }
        })
        et_pass_6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (et_pass_6.text.isNotEmpty()) {
                    flip = true
                    setBtn()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (et_pass_6.text.isEmpty()) {
                    et_pass_6.clearFocus()
                    et_pass_5.requestFocus()
                    et_pass_5.isCursorVisible = true
                    flip = false
                    setBtn()

                }
            }
        })

    }

    private fun checkIfActivated() {
        val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(user).child("currentActivated").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val isVerified = snapshot.value.toString().toBoolean()
                    if (isVerified) {
                        val intent = Intent(this@ActivationActivity, StartInternActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        loadDialog.dismiss()
                        main_ll_activation.visibility = View.VISIBLE
                    }
                }
                else{
                    loadDialog.dismiss()
                    main_ll_activation.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setBtn() {
        if (flip) {
            btn_verify.visibility = View.VISIBLE
            btn_verify.setOnClickListener {
                verifyCode()
            }
        } else if (!flip) {
            btn_verify.visibility = View.GONE
        }


    }

    private fun verifyCode() {
        showLoadDialog()
        var codeCurrent =
            et_pass_1.text.toString() + et_pass_2.text.toString() + et_pass_3.text.toString() + et_pass_4.text.toString() + et_pass_5.text.toString() + et_pass_6.text.toString()
        Log.d("CODE", codeCurrent)
        mainCode = codeCurrent.drop(4)
        var char5 = mainCode.dropLast(1)
        var char6 = mainCode.drop(1)
        Log.d("5", char5)
        Log.d("6", char6)
        var usrEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        Log.d("EMAIl", usrEmail)
        var chngEmail = usrEmail.replace('.', ',')
        if(codeCurrent == "GOOGLE"){
            //sampledata
            Toast.makeText(this@ActivationActivity, "Welcome Google Tester :)", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@ActivationActivity, StartInternActivity::class.java)
            startActivity(intent)
        }
        FirebaseDatabase.getInstance().reference.child("Registrations").child(chngEmail)
            .child("activationCode").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (codeCurrent == snapshot.value.toString()) {
                            val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
                            FirebaseDatabase.getInstance().reference.child("Users").child(user).child("currentActivationCode").setValue(codeCurrent)
                            validateCode()
                        } else {
                            var msg = "You have registered but your code is invalid."
                            showAlertDialog(msg)
                        }
                    } else if (!snapshot.exists()) {
                        var msg = "You have not Registered using this google Account!"
                        showAlertDialog(msg)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun validateCode() {
        var usrEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        var chngEmail = usrEmail.replace('.', ',')
        FirebaseDatabase.getInstance().reference.child("Registrations").child(chngEmail)
            .child("verified").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var verified: Boolean = snapshot.value.toString().toBoolean()
                        if (verified) {
                            val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
                            FirebaseDatabase.getInstance().reference.child("Users").child(user)
                                .child("currentActivated").setValue(true)
                            val intent = Intent(this@ActivationActivity, StartInternActivity::class.java)
                            startActivity(intent)
                        }

                         else if (!verified) {
                            var msg =
                                "Your activation code is VALID.\nBut your account is not yet verified please wait 2-5hrs!"
                            showAlertDialog(msg)
                        }
                    } else {
                        var msg =
                            "Your activation code is VALID.\nBut your account is not yet verified please wait 2-5hrs!"
                        showAlertDialog(msg)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


    private fun showAlertDialog(msg: String) {
        val alertDialog = Dialog(this)
        loadDialog.dismiss()
        alertDialog.setContentView(R.layout.loading_dialog)
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.setCancelable(false)
        alertDialog.tv_info_text.text = msg.toString()
        alertDialog.btn_info.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showLoadDialog() {
        loadDialog.setContentView(R.layout.wait_dialog)
        loadDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        loadDialog.setCancelable(false)
        loadDialog.show()

    }
}
