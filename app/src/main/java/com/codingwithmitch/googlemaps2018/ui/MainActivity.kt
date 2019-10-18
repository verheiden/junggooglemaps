package apps.com.codingwithmitch.googlemaps2018.ui

import android.Manifest.permission
import android.R.id
import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.adapters.ChatroomRecyclerAdapter
import com.codingwithmitch.googlemaps2018.models.Chatroom
import com.codingwithmitch.googlemaps2018.models.User
import com.codingwithmitch.googlemaps2018.models.UserLocation
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import apps.com.codingwithmitch.googlemaps2018.Constants.Companion
import apps.com.codingwithmitch.googlemaps2018.Constants.Companion.ERROR_DIALOG_REQUEST
import apps.com.codingwithmitch.googlemaps2018.Constants.Companion.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import apps.com.codingwithmitch.googlemaps2018.Constants.Companion.PERMISSIONS_REQUEST_ENABLE_GPS


public class MainActivity : AppCompatActivity(), View.OnClickListener,ChatroomRecyclerAdapter.ChatroomRecyclerClickListener {

    private val TAG = "MainActivity"

    //widgets
    private var mProgressBar: ProgressBar? = null

    //vars
    private val mChatrooms = ArrayList<Chatroom>()
    private val mChatroomIds = HashSet<String>()
    private var mChatroomRecyclerAdapter: ChatroomRecyclerAdapter? = null
    private var mChatroomRecyclerView: RecyclerView? = null
    private var mChatroomEventListener: ListenerRegistration? = null
    private var mDb: FirebaseFirestore? = null
    private var mLocationPermissionGranted : Boolean = false
    private var mFusedLocationClient : FusedLocationProviderClient? = null
    private var mUserLocation : UserLocation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mProgressBar = findViewById(R.id.progressBar)
        mChatroomRecyclerView = findViewById(R.id.chatrooms_recycler_view)
        fab_create_chatroom.setOnClickListener(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mDb = FirebaseFirestore.getInstance()
        initSupportActionBar()
        initChatroomRecyclerView()
    }

    private fun initChatroomRecyclerView() {
        mChatroomRecyclerAdapter = ChatroomRecyclerAdapter(mChatrooms, this)
        mChatroomRecyclerView?.adapter = mChatroomRecyclerAdapter
        mChatroomRecyclerView?.layoutManager = LinearLayoutManager(this)
    }
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fab_create_chatroom -> {
                newChatroomDialog()
            }
        }
    }
    private fun checkMapServices(): Boolean {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true
            }
        }
        return false
    }

    private fun buildAlertMessageNoGps() {
        val builder = Builder(this)
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val enableGpsIntent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS)
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    fun isMapsEnabled(): Boolean {
        val manager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
            return false
        }
        return true
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                permission.ACCESS_FINE_LOCATION
            )
            === PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
            getChatrooms()
            getUserDetails()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf<String?>(permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    fun isServicesOK(): Boolean {
        Log.d(
            TAG,
            "isServicesOK: checking google services version"
        )
        val available =
            GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this@MainActivity)
        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests

            Log.d(
                TAG,
                "isServicesOK: Google Play Services is working"
            )
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(
                available
            )
        ) {
            //an error occured but we can resolve it

            Log.d(
                TAG,
                "isServicesOK: an error occured but we can fix it"
            )
            val dialog: Dialog =
                GoogleApiAvailability.getInstance()
                    .getErrorDialog(this@MainActivity, available, ERROR_DIALOG_REQUEST)
            dialog.show()
        } else {
            Toast.makeText(
                this,
                "You can't make map requests",
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {// If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            TAG,
            "onActivityResult: called."
        )
        when (requestCode) {
            PERMISSIONS_REQUEST_ENABLE_GPS -> {
                if (mLocationPermissionGranted) {
                    getChatrooms()
                    getUserDetails()
                } else {
                    getLocationPermission()
                }
            }
        }
    }

    private fun initSupportActionBar() {
        title = "Chatrooms"
    }


    private fun getLastKnownLocation() {
        Log.d(TAG,
            "getLastKnownLocation: called."
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED) {
            return
        }

        mFusedLocationClient!!.lastLocation.addOnCompleteListener{ object : OnCompleteListener<Location>{
              override fun onComplete(@NonNull task: Task<Location>) {
                  if (task.isSuccessful) {
                      val location: Location = task.result
                      val geoPoint =
                          GeoPoint(
                              location.latitude,
                              location.longitude
                          )
                      mUserLocation?.geo_point = geoPoint
                      mUserLocation?.timestamp = null
                      saveUserLocation()
                  }
              }
            }
        }
    }
    fun saveUserLocation() {
        if (mUserLocation != null) {
            val locationRef = mDb!!
                .collection(getString(R.string.collection_user_locations))
                .document(FirebaseAuth.getInstance().uid!!)
            locationRef.set(mUserLocation!!).addOnCompleteListener(object :
                OnCompleteListener<Void> {
                override fun onComplete(@NonNull task: Task<Void>) {
                    if (task.isSuccessful) {
                        Log.d(
                            TAG,
                            "saveUserLocation: \ninserted user location into database." +
                                    "\n latitude: " + mUserLocation!!.geo_point!!.latitude.toString() +
                                    "\n longitude: " + mUserLocation!!.geo_point!!.longitude
                        )
                    }
                }
            })
        }
    }
    override fun onChatroomSelected(position: Int) {
        navChatroomActivity(mChatrooms.get(position))
    }

    private fun buildNewChatRoomDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter a chatroom name:")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT;
        builder.setView(input);
    }
    private fun navChatroomActivity(chatroom: Chatroom){
        val intent = Intent(this@MainActivity, ChatroomActivity::class.java)
        intent.putExtra(getString(R.string.intent_chatroom), chatroom)
        startActivity(intent)
    }
    override fun onCreateOptionsMenu( menu: Menu):Boolean{
        getMenuInflater().inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    private fun getUserDetails(){
        if ( mUserLocation == null ){
            mUserLocation = UserLocation()
            val userRef : DocumentReference = mDb!!.collection("Users")
                .document(FirebaseAuth.getInstance().uid!!)

            userRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                override fun onComplete(@NonNull task: Task<DocumentSnapshot>) {
                    if (task.isSuccessful) {
                        Log.d(
                            TAG, "onComplete: successfully set the user client."
                        )
                        val user: User? = task.result.toObject<User>(User::class.java)
                        mUserLocation!!.user = user
                        getLastKnownLocation()
                    }
                }
            })

        }
        else {
            getLastKnownLocation();
        }
    }
    private fun getChatrooms() {
        val settings =
            FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb!!.firestoreSettings = settings
        val chatroomsCollection = mDb!!
            .collection(getString(R.string.collection_chatrooms))
        mChatroomEventListener =
            chatroomsCollection.addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                Log.d(
                    TAG,
                    "onEvent: called."
                )
                if (e != null) {
                    Log.e(
                        TAG,
                        "onEvent: Listen failed.",
                        e
                    )
                    return@EventListener
                }
                if (queryDocumentSnapshots != null) {
                    for (doc in queryDocumentSnapshots) {
                        val chatroom =
                            doc.toObject(
                                Chatroom::class.java
                            )
                        if (!mChatroomIds.contains(chatroom.chatroom_id)) {
                            mChatroomIds.add(chatroom.chatroom_id!!)
                            mChatrooms.add(chatroom)
                        }
                    }
                    Log.d(
                        TAG,
                        "onEvent: number of chatrooms: " + mChatrooms.size
                    )
                    mChatroomRecyclerAdapter!!.notifyDataSetChanged()
                }
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mChatroomEventListener != null) {
            mChatroomEventListener!!.remove()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                getChatrooms()
                getUserDetails()
            } else {
                getLocationPermission()
            }
        }
    }


    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    fun onOptionItemSelected(item: MenuItem):Boolean {
       when(item.itemId)   {
           R.id.action_sign_out -> {
               signOut()
               return true
           }
           R.id.action_profile -> {
               startActivity(Intent(this, ProfileActivity:: class.java ))
               return true
           }
           else -> { return super.onContextItemSelected(item) }
       }
    }

    private fun newChatroomDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter a chatroom name")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("CREATE") { dialog, which ->
            if (input.text.toString() != "") {
                buildNewChatroom(input.text.toString())
            } else {
                Toast.makeText(this@MainActivity, "Enter a chatroom name", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun buildNewChatroom(chatroomName: String) {

        val chatroom = Chatroom()
        chatroom.title = chatroomName

        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        mDb?.setFirestoreSettings(settings)

        val newChatroomRef = mDb?.collection(getString(R.string.collection_chatrooms))?.document()

        chatroom.chatroom_id = newChatroomRef?.id

        newChatroomRef?.set(chatroom)?.addOnCompleteListener(OnCompleteListener<Void> { task ->
            hideDialog()

            if (task.isSuccessful) {
                navChatroomActivity(chatroom)
            } else {
                val parentLayout = android.R.id.content
                Snackbar.make(parentLayout as View, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
            }
        })
    }
    private fun hideDialog() { mProgressBar?.setVisibility(View.GONE) }

    private fun showDialog() {
        mProgressBar?.setVisibility(View.VISIBLE)
    }

}