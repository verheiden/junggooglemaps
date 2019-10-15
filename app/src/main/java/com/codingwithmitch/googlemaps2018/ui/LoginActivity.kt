package apps.com.codingwithmitch.googlemaps2018.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import apps.com.codingwithmitch.googlemaps2018.models.User
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.UserClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    //Firebase
    private lateinit var  mAuthListener: FirebaseAuth.AuthStateListener

    // widgets
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mProgressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        mProgressBar = findViewById(R.id.progressBar)

        setupFirebaseAuth()
        val in_button : Button = findViewById(R.id.email_sign_in_button)
            in_button.setOnClickListener(this)
        val vRegister : TextView = findViewById(R.id.link_register)
            vRegister.setOnClickListener(this)

        hideSoftKeyboard()
    }


    private fun showDialog() {
        mProgressBar!!.visibility = View.VISIBLE

    }

    private fun hideDialog() {
        if (mProgressBar!!.visibility == View.VISIBLE) {
            mProgressBar!!.visibility = View.INVISIBLE
        }
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    /*
        ----------------------------- Firebase setup ---------------------------------
     */
    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.")

        mAuthListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(@NonNull firebaseAuth: FirebaseAuth) {
                val user = firebaseAuth.getCurrentUser()
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user!!.getUid())
                    Toast.makeText(
                        this@LoginActivity,
                        "Authenticated with: " + user!!.getEmail(),
                        Toast.LENGTH_SHORT
                    ).show()

                    val db = FirebaseFirestore.getInstance()
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build()
                    db.setFirestoreSettings(settings)

                    val userRef = db.collection(getString(R.string.collection_users))
                        .document(user!!.getUid())

                    userRef.get()
                        .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                            override fun onComplete(@NonNull task: Task<DocumentSnapshot>) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: successfully set the user client.")
                                    val user = task.getResult().toObject(User::class.java)
                                    ( applicationContext as UserClient).user = user
                                }
                            }
                        })

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out")
                }
                // ...
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener)
        }
    }

    private fun signIn() {
        //check if the fields are filled out
        if (!isEmpty(mEmail!!.text.toString()) && !isEmpty(mPassword!!.text.toString())) {
            Log.d(TAG, "onClick: attempting to authenticate.")

            showDialog()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                mEmail!!.text.toString(),
                mPassword!!.text.toString()
            )
                .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(@NonNull task: Task<AuthResult>) {

                        hideDialog()

                    }
                }).addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(@NonNull e: Exception) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Authentication Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        hideDialog()
                    }
                })
        } else {
            Toast.makeText(
                this@LoginActivity,
                "You didn't fill in all the fields.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.link_register -> {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            R.id.email_sign_in_button -> {
                signIn()
            }
        }
    }

    companion object {

        private val TAG = "LoginActivity"
    }
}