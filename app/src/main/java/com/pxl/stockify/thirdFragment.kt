package com.pxl.stockify

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [thirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//lege buttons, moet aangepast worden
class thirdFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fragmentview: View
    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var  mDatabaseReference: DatabaseReference
    private lateinit var mChildEventListener: ChildEventListener
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var loginOutButton: Button
    lateinit var darkmodeBtn: Button
    //key opslaan in algemene files

    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build())

    // See: https://developer.android.com/training/basics/intents/result alles verplaatsen naar first fragment denk ik
    private val signInLauncher = registerForActivityResult( //dees combineren met de static gedoe in deze klassen en hopenlijk zo te fixe
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        fragmentview = inflater.inflate(R.layout.fragment_third, container, false)

        //Database settings
        FirebaseUtil.openFbReference()
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        mAuthListener = FirebaseAuth.AuthStateListener {
            fun onAuthStateChanged(@NonNull firebaseAuth: FirebaseAuth) {
                signin()
                Toast.makeText(context, "logged in", Toast.LENGTH_SHORT).show()

            }
        }

        val preferences = this.requireActivity()
            .getSharedPreferences("PREFERENCE_NAME", 0)
        var editor = preferences.edit()

        loginOutButton= fragmentview.findViewById(R.id.login_out_btn) as Button
        darkmodeBtn= fragmentview.findViewById(R.id.dark_lightmode_btn) as Button

        if(preferences.getBoolean("darkmode",false)){
            darkmodeBtn.text = "Lightmode"


        }else{
            darkmodeBtn.text = "Darkmode"
        }



        var user = FirebaseUtil.mFirebaseAuth.currentUser

        if (user != null) {
            // User is signed in.
            loginOutButton.text = "Logout"
        } else {
            // No user is signed in.
            loginOutButton.text = "Login"
        }



        loginOutButton.setOnClickListener {
            if(loginOutButton.text == "Login") {
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .build()
                signInLauncher.launch(signInIntent)
            }else{
                loginOutButton.text = "Login"
                FirebaseAuth.getInstance().signOut()
            }

        }




        darkmodeBtn.setOnClickListener {

            if(darkmodeBtn.text == "Darkmode") {
                //darkmodeBtn.text = "Lightmode"

                editor.putBoolean("darkmode",true)
                editor.commit()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            }else{
                //darkmodeBtn.text = "Darkmode"
                editor.putBoolean("darkmode",false)
                editor.commit()
                //val hulp:Boolean = preferences.getBoolean("darkmode",false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }


        return fragmentview
    }





    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            loginOutButton.text = "Logout"
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(context, "login failed", Toast.LENGTH_SHORT).show()
        }
    }
/*
    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_signout]
    }
*/

    fun signin() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    override fun onPause() {
        super.onPause()
        detachListener()
    }


    override fun onResume() {
        super.onResume()
        attachListener()
    }

    fun attachListener(){
        FirebaseUtil.mFirebaseAuth.addAuthStateListener(mAuthListener)
    }


    fun detachListener(){
        FirebaseUtil.mFirebaseAuth.removeAuthStateListener(mAuthListener)
    }


    //dees s mess interresant
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment thirdFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            thirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}