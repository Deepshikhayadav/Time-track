package com.deepshikhayadav.timetrack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.deepshikhayadav.timetrack.Dashboard.Companion.currentUser
import com.deepshikhayadav.timetrack.Dashboard.Companion.docRef
import com.deepshikhayadav.timetrack.Dashboard.Companion.s
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class NewActivity : AppCompatActivity() {
    var l1:Int=0
    var l2:Int=0
    var l3:Int=0

    private val TAG: String = Dashboard::class.java.simpleName
    var rBreak= ArrayList<Long>()
    var totalTime:Long=0
    val db = Firebase.firestore

    // companion objects are same as static var in java
    companion object{
        var start2 by Delegates.notNull<Long>()
        var end2 by Delegates.notNull<Long>()
         var gn : String="0"
        var fLTime="0"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        start2 = Calendar.getInstance().timeInMillis
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG,"  New onStart")
        start2=Calendar.getInstance().timeInMillis

    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG," New onResume")


    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG,"  New onRestart")

    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG," New onPause")

    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG,"  New onStop")
        end2 = Calendar.getInstance().timeInMillis
        val rTime= end2 - start2
        rBreak.add(rTime)
        for(item in rBreak){

            totalTime+=item
            Log.i("Real Time","    $totalTime")
        }


        val date= Date(totalTime)
        var dateFormat= SimpleDateFormat("HH:mm:ss")
        dateFormat.timeZone= TimeZone.getTimeZone("UTC")
        gn=dateFormat.format(date)
        Toast.makeText(this,"Second Activity time used $gn", Toast.LENGTH_SHORT).show()

        Dashboard.gEnd=Calendar.getInstance().timeInMillis
        val gTime=Dashboard.gEnd-Dashboard.gStart
        val gDate= Date(gTime)
        var gDateFormat= SimpleDateFormat("HH:mm:ss")
        gDateFormat.timeZone= TimeZone.getTimeZone("UTC")
         val g=gDateFormat.format(gDate)

        val user = hashMapOf(
            "Email" to "$currentUser",
            "First activity" to s,
            "Last activity" to gn,
            "Total time" to g
        )


        if(docRef!=null){
            db.collection("$currentUser")
                .document(docRef!!)
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    val lTime = queryDocumentSnapshots.data!!["Last activity"]
                    if(lTime=="0"){
                        db.collection("$currentUser")
                            .document(docRef!!)
                            .set(user)
                            .addOnSuccessListener { documentRef->
                            }
                            .addOnFailureListener {

                            }
                    }
                    else{
                        val k1=lTime.toString().subSequence(0,2).toString().toInt()
                        val k2=lTime.toString().subSequence(3,5).toString().toInt()
                        val k3=lTime.toString().subSequence(6,8).toString().toInt()

                        val p1=gn.toString().subSequence(0,2).toString().toInt()
                        val p2=NewActivity.gn.subSequence(3,5).toString().toInt()
                        val p3=NewActivity.gn.subSequence(6,8).toString().toInt()
                        l3=p3+k3
                        l2=p2+k2
                        if(l3>=60){
                            l3=(p3+k3)%60
                            l2 += (p3 + k3) / 60
                            if(l2>=60){
                                l2+=(p2+k2)%60
                                l1 += (p2 + k2) / 60
                            }
                        }

                        if(l1/10==0){
                            if(l2/10==0){
                                if(l3/10==0){
                                    fLTime="0$l1:0$l2:0$l3"
                                }
                                else{
                                    fLTime="0$l1:0$l2:$l3"
                                }
                            }
                            else{
                                fLTime="0$l1:$l2:$l3"
                            }
                        }
                        else{
                            fLTime="$l1:$l2:$l3"
                        }

                        var user2=HashMap<String,String>()

                        user2["Email"] = "$currentUser"
                        user2["First activity"] = s
                        user2["Last activity"] = fLTime
                        user2["Total time"] = g

                        db.collection("$currentUser")
                            .document(docRef!!)
                            .set(user2)
                            .addOnSuccessListener { documentRef->

                            }
                            .addOnFailureListener {

                            }
                    }



                }
                .addOnFailureListener {

                }
        }
        Toast.makeText(this,"Overall app time used $g", Toast.LENGTH_SHORT).show()


    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG,"  New onDestroy")

    }
}