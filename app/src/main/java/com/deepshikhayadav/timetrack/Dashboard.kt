package com.deepshikhayadav.timetrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.deepshikhayadav.timetrack.databinding.ActivityDashboardBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.Delegates

class Dashboard : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding
    private val TAG: String = Dashboard::class.java.simpleName
    var  diff :Long=0
    var  gDiff :Long=0
    var rBreak= ArrayList<Long>()
    var totalTime:Long=0
    val db = Firebase.firestore

    private lateinit var auth: FirebaseAuth
    // companion objects are same as static var in java
    companion object{
        lateinit var s:String
        var docRef:String?=null
        var currentUser: String?=null
         var start by Delegates.notNull<Long>()
         var end by Delegates.notNull<Long>()
        var gStart by Delegates.notNull<Long>()
        var gEnd by Delegates.notNull<Long>()
        lateinit var gd : String
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG,"Dash onCreate")
        start= Calendar.getInstance().timeInMillis
        gStart=Calendar.getInstance().timeInMillis
         binding = ActivityDashboardBinding.inflate(layoutInflater)
         setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        auth = Firebase.auth
        currentUser = auth.currentUser!!.email
        binding.logout.setOnClickListener {
            AuthUI.getInstance().signOut(this).addOnSuccessListener {
                Toast.makeText(this,"Log Out",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
            }
        }
        binding.fab.setOnClickListener { view ->

            startActivity(Intent(this,NewActivity::class.java))
        }


        val hashMap = hashMapOf(
            "Email" to "$currentUser",
            "First activity" to "0",
            "Last activity" to "0",
            "Total time" to "0"
        )
        db.collection("$currentUser")
            .add(hashMap)
            .addOnSuccessListener { documentReference ->
                docRef=documentReference.id
              //  Toast.makeText(this,"Data submitted", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG,"  Dash onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG," Dash onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG,"  Dash onRestart")
        totalTime=0
        start=Calendar.getInstance().timeInMillis
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG," Dash onPause")

    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "  Dash onStop")
        end = Calendar.getInstance().timeInMillis
        diff = end - start
        rBreak.add(diff)
        for (item in rBreak) {
            totalTime += item
            Log.i("Real Time", "    $totalTime")
        }

        val date = Date(totalTime)
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        s = dateFormat.format(date)

          Toast.makeText(this, "First Activity time used $s", Toast.LENGTH_LONG).show()

        gEnd = Calendar.getInstance().timeInMillis
        gDiff = gEnd - gStart
        val gDate = Date(gDiff)
        val gDateFormat = SimpleDateFormat("HH:mm:ss")
        gDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        gd = gDateFormat.format(gDate)

          Toast.makeText(this, "Overall app time used $gd", Toast.LENGTH_LONG).show()

        var user = HashMap<String, String>()
        if (docRef != null) {
            db.collection("$currentUser")
                .document(docRef!!)
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    val lTime = queryDocumentSnapshots.data!!["Last activity"]
                    user["Email"] = "$currentUser"
                    user["First activity"] = s
                    user["Last activity"] = lTime.toString()
                    user["Total time"] = gd
                    db.collection("$currentUser")
                        .document(Dashboard.docRef!!)
                        .set(user)
                        .addOnSuccessListener { documentRef ->
                        }
                        .addOnFailureListener {

                        }
                }
                .addOnFailureListener {

                }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG,"Dash onDestroy")

    }
    override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
    return navController.navigateUp(appBarConfiguration)
            || super.onSupportNavigateUp()
    }
}