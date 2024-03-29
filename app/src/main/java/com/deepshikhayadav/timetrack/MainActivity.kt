package com.deepshikhayadav.timetrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private val RC_SIGN_IN=123
    private lateinit var button: Button
    private val TAG: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG,"  onCreate")

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        button=findViewById(R.id.button)
        button.setOnClickListener {
            signIn()
        }

    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG,"  onStop")
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                Toast.makeText(this, "firebaseAuthWithGoogle successful  ${account.displayName}", Toast.LENGTH_SHORT).show()
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    startActivity(Intent(applicationContext,Dashboard::class.java))
                    finish()
                }
            }
    }


    override fun onStart() {
        super.onStart()
        Log.i(TAG,"  onStart")
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //  updateUI(currentUser);
        if(currentUser!=null){
            startActivity(Intent(applicationContext,Dashboard::class.java))
            finish()
        }


    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG,"  onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG,"  onPause")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        // Firebase.auth.signOut()
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG,"  onRestart")
    }
    override fun onDestroy() {
        super.onDestroy()
        // Firebase.auth.signOut()
        Log.i(TAG,"  onDestroy")

    }
}
