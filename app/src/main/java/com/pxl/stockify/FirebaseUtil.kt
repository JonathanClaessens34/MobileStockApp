package com.pxl.stockify



import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pxl.stockify.domain.StockListDisplay

class FirebaseUtil  {

    companion object {

        @JvmStatic
        lateinit var mFirebaseDatabase: FirebaseDatabase

        @JvmStatic
        lateinit var mDatabaseReference: DatabaseReference

        @JvmStatic
        private lateinit var firebaseUtil: FirebaseUtil

        @JvmStatic
        lateinit var mFirebaseAuth: FirebaseAuth


        @JvmStatic
        lateinit var mStockListDisplays: List<StockListDisplay>




        @JvmStatic
        fun openFbReference() {

            if (true) {//firebaseUtil == null
                firebaseUtil = FirebaseUtil()
                mFirebaseDatabase = FirebaseDatabase.getInstance()
                mFirebaseAuth = FirebaseAuth.getInstance()

            }

            mStockListDisplays = emptyList()
            mDatabaseReference = mFirebaseDatabase.getReference().child("users/${mFirebaseAuth.currentUser?.uid.toString()}")


            }
        }




    private constructor(){

    }





}

