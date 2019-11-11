package com.rtccaller.data

import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Singleton
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import android.content.Intent
import com.rtccaller.call.CallActivity
import com.rtccaller.utils.ContactsLifecycleDelegate.Companion.getRoomConnectionIntent
import com.rtccaller.utils.NotificationFactory.Companion.getStandardNotification


class UsersRepository: JobService() {
    override fun onStopJob(p0: JobParameters?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mFirebaseUser: FirebaseUser
    //todo think about injecting DatabaseReference
    private lateinit var mFirebaseDatabaseReference: DatabaseReference
    private lateinit var userRef: DatabaseReference

    init {
        initValues()
    }

    private fun initValues(){
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.getCurrentUser()!!
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
        userRef = mFirebaseDatabaseReference.child("users").child("my_mail@gmail.com") //todo provide mail with injection from shared preferences
        startObservation()
    }

    private fun startObservation(){
        userRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val post = dataSnapshot.getValue(Call::class.java)
                    System.out.println(post)

                    createNotification()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                }
            }
        )
    }

    inner class Call(var caller: String, var roomNumber: Int)

    private fun createNotification(){
        val intent = getRoomConnectionIntent("0", false, false, false, 0, this)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        getStandardNotification(this, "title", "message", pendingIntent, "", 0)
    }



}