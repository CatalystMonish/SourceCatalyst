package com.catalystmedia.sourcecatalyst

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_start_intern.*
import kotlinx.android.synthetic.main.loading_dialog.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class LoginActivity : AppCompatActivity() {
    //request code deceleration
    lateinit var loadDialog: Dialog

    companion object{
        private const val RC_SIGN_IN = 120
    }
    //for authentication
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loadDialog = Dialog(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        btn_google.setOnClickListener {
//            signIn()
            showAlertDialog()
        }
    }
    //function sign in google
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun showAlertDialog() {
        val alertDialog = Dialog(this)
        alertDialog.setContentView(R.layout.loading_dialog)
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.setCancelable(false)
        alertDialog.btn_info.setOnClickListener {
            //take user to website
        }
        alertDialog.btn_continue.setOnClickListener {
            alertDialog.dismiss()
        showLoadDialog()
            signIn()
        }
        alertDialog.show()
    }

    private fun showLoadDialog() {
        loadDialog.setContentView(R.layout.wait_dialog)
        loadDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        loadDialog.setCancelable(false)
        loadDialog.show()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful){
                try {
                    loadDialog.dismiss()
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity","firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignInActivity","Google sign in failed", e)
                }
            }else{
                Log.w("SignInActivity", exception.toString())
            }}
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkUserMap()
                } else {
                    loadDialog.dismiss()
                    // If sign in fails, display a message to the user.
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }

    fun checkUserMap(){
        val user = FirebaseAuth.getInstance().currentUser
        val sdf = SimpleDateFormat("dd/M/yyyy")
        if (user != null) {
            val name = user.displayName
            val email = user.email
            val userUid = user.uid
            val todayDate = sdf.format(Date()).toString()
            val userRef = FirebaseDatabase.getInstance().reference
                .child("Users").child(userUid.toString())
            userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    //if user exists no hashmap already created
                    if (snapshot.exists()) {
                        loadDialog.dismiss()
                        Toast.makeText(this@LoginActivity, "Welcome Back!", Toast.LENGTH_SHORT).show()
                        //TODO: Change to IntroductionActivity
                        val intent = Intent(this@LoginActivity, ActivationActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    //First time sign in create hash map
                    else {
                        loadDialog.dismiss()
                        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = name.toString().toLowerCase()
                        userMap["email"] = email.toString()
                        userMap["firstRegisterDate"] = todayDate
                        usersRef.child(userUid.toString()).child("Info").setValue(userMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    isFirstTime()

                                }

                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        }

        else {
            Toast.makeText(this@LoginActivity, "Unknown Error", Toast.LENGTH_LONG).show()
        }

    }


    private fun isFirstTime(){
        Toast.makeText(this@LoginActivity, "Account Has Been Created Successfully", Toast.LENGTH_LONG).show()
        //TODO: Change to Introduction
        val intent = Intent(this@LoginActivity, ActivationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, ActivationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}