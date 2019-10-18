package com.codingwithmitch.googlemaps2018.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.adapters.UserRecyclerAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import apps.com.codingwithmitch.googlemaps2018.Constants.Companion.MAPVIEW_BUNDLE_KEY


class UserListFragment : Fragment(), OnMapReadyCallback {

    //widgets
    private var mUserListRecyclerView: RecyclerView? = null
    private var mMapView: MapView? = null


    //vars
    private var mUserList: ArrayList<User>? = ArrayList<User>()
    private var mUserRecyclerAdapter: UserRecyclerAdapter? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserList = arguments?.getParcelableArrayList(getString(R.string.intent_user_list))
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)
        mUserListRecyclerView = view.findViewById(R.id.user_list_recycler_view)
        mMapView = view.findViewById(R.id.user_list_map)

        initUserListRecyclerView()
        initGoogleMap(savedInstanceState)

        return view
    }


    private fun initGoogleMap(savedInstanceState: Bundle?) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
        mMapView!!.onCreate(mapViewBundle)
        mMapView!!.getMapAsync(this)
    }

    private fun initUserListRecyclerView() {
        mUserRecyclerAdapter = mUserList?.let { UserRecyclerAdapter(it) }
        mUserListRecyclerView!!.setAdapter(mUserRecyclerAdapter)
        mUserListRecyclerView!!.layoutManager = LinearLayoutManager(getActivity())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mMapView!!.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView!!.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(activity as Context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity as Context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        map.isMyLocationEnabled = true
    }

    override fun onPause() {
        mMapView!!.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView!!.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    companion object {

        private val TAG = "UserListFragment"


        fun newInstance(): UserListFragment {
            return UserListFragment()
        }
    }
}


/*
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import apps.com.codingwithmitch.googlemaps2018.models.User
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.adapters.UserRecyclerAdapter
import com.codingwithmitch.googlemaps2018.models.ClusterMarker
import com.codingwithmitch.googlemaps2018.models.UserLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import java.util.ArrayList


class UserListFragment : Fragment(), OnMapReadyCallback, View.OnClickListener,
    GoogleMap.OnInfoWindowClickListener {


    //widgets
    private var mUserListRecyclerView: RecyclerView? = null
    private var mMapView: MapView? = null
    private var mMapContainer: RelativeLayout? = null


    //vars
    private val mUserList = ArrayList<User>()
    private val mUserLocations = ArrayList<UserLocation>()
    private var mUserRecyclerAdapter: UserRecyclerAdapter? = null
    private var mGoogleMap: GoogleMap? = null
    private var mUserPosition: UserLocation? = null
    private var mMapBoundary: LatLngBounds? = null
    private var mClusterManager: ClusterManager<ClusterMarker>? = null
    private var mClusterManagerRenderer: MyClusterManagerRenderer? = null
    private val mClusterMarkers = ArrayList<ClusterMarker>()
    private var mMapLayoutState = 0
    private var mGeoApiContext: GeoApiContext? = null

    private val mHandler = Handler()
    private var mRunnable: Runnable? = null

    fun onCreate(@Nullable savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        if (mUserLocations.size == 0) { // make sure the list doesn't duplicate by navigating back
            if (arguments != null) {
                val users = arguments.getParcelableArrayList(getString(R.string.intent_user_list))
                mUserList.addAll(users)

                val locations =
                    arguments.getParcelableArrayList(getString(R.string.intent_user_locations))
                mUserLocations.addAll(locations)
            }
        }
    }

    @Nullable
    fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup, @Nullable savedInstanceState: Bundle): View {
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)
        mUserListRecyclerView = view.findViewById(R.id.user_list_recycler_view)
        mMapView = view.findViewById(R.id.user_list_map)
        view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this)
        mMapContainer = view.findViewById(R.id.map_container)

        initUserListRecyclerView()
        initGoogleMap(savedInstanceState)

        setUserPosition()

        return view
    }

    private fun startUserLocationsRunnable() {
        Log.d(
            TAG,
            "startUserLocationsRunnable: starting runnable for retrieving updated locations."
        )
        mHandler.postDelayed(mRunnable = Runnable {
            retrieveUserLocations()
            mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL.toLong())
        }, LOCATION_UPDATE_INTERVAL.toLong())
    }

    private fun stopLocationUpdates() {
        mHandler.removeCallbacks(mRunnable)
    }

    private fun retrieveUserLocations() {
        Log.d(TAG, "retrieveUserLocations: retrieving location of all users in the chatroom.")

        try {
            for (clusterMarker in mClusterMarkers) {

                val userLocationRef = FirebaseFirestore.getInstance()
                    .collection(getString(R.string.collection_user_locations))
                    .document(clusterMarker.user!!.user_id!!)

                userLocationRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val updatedUserLocation = task.result.toObject(UserLocation::class.java)

                        // update the location
                        for (i in mClusterMarkers.indices) {
                            try {
                                if (mClusterMarkers[i].user!!.user_id == updatedUserLocation!!.user!!.user_id) {

                                    val updatedLatLng = LatLng(
                                        updatedUserLocation.geo_point!!.latitude,
                                        updatedUserLocation.geo_point!!.longitude
                                    )

                                    mClusterMarkers[i].setPosition(updatedLatLng)
                                    mClusterManagerRenderer!!.setUpdateMarker(mClusterMarkers[i])
                                }


                            } catch (e: NullPointerException) {
                                Log.e(
                                    TAG,
                                    "retrieveUserLocations: NullPointerException: " + e.message
                                )
                            }

                        }
                    }
                }
            }
        } catch (e: IllegalStateException) {
            Log.e(
                TAG,
                "retrieveUserLocations: Fragment was destroyed during Firestore query. Ending query." + e.message
            )
        }

    }

    private fun addMapMarkers() {

        if (mGoogleMap != null) {

            if (mClusterManager == null) {
                mClusterManager = ClusterManager(activity.getApplicationContext(), mGoogleMap)
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = MyClusterManagerRenderer(
                    activity,
                    mGoogleMap,
                    mClusterManager
                )
                mClusterManager!!.setRenderer(mClusterManagerRenderer)
            }
            mGoogleMap!!.setOnInfoWindowClickListener(this)

            for (userLocation in mUserLocations) {

                Log.d(TAG, "addMapMarkers: location: " + userLocation.geo_point!!.toString())
                try {
                    var snippet = ""
                    if (userLocation.user!!.user_id == FirebaseAuth.getInstance().uid) {
                        snippet = "This is you"
                    } else {
                        snippet = "Determine route to " + userLocation.user!!.username + "?"
                    }

                    var avatar = R.drawable.cartman_cop // set the default avatar
                    try {
                        avatar = Integer.parseInt(userLocation.user!!.avatar!!)
                    } catch (e: NumberFormatException) {
                        Log.d(
                            TAG,
                            "addMapMarkers: no avatar for " + userLocation.user!!.username + ", setting default."
                        )
                    }

                    val newClusterMarker = ClusterMarker(
                        LatLng(
                            userLocation.geo_point!!.latitude,
                            userLocation.geo_point!!.longitude
                        ),
                        userLocation.user!!.username,
                        snippet,
                        avatar,
                        userLocation.user
                    )
                    mClusterManager!!.addItem(newClusterMarker)
                    mClusterMarkers.add(newClusterMarker)

                } catch (e: NullPointerException) {
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.message)
                }

            }
            mClusterManager!!.cluster()

            setCameraView()
        }
    }

    /**
     * Determines the view boundary then sets the camera
     * Sets the view
     */
    private fun setCameraView() {

        // Set a boundary to start
        val bottomBoundary = mUserPosition!!.geo_point!!.latitude - .1
        val leftBoundary = mUserPosition!!.geo_point!!.longitude - .1
        val topBoundary = mUserPosition!!.geo_point!!.latitude + .1
        val rightBoundary = mUserPosition!!.geo_point!!.longitude + .1

        mMapBoundary = LatLngBounds(
            LatLng(bottomBoundary, leftBoundary),
            LatLng(topBoundary, rightBoundary)
        )

        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0))
    }

    private fun setUserPosition() {
        for (userLocation in mUserLocations) {
            if (userLocation.user!!.user_id == FirebaseAuth.getInstance().uid) {
                mUserPosition = userLocation
            }
        }
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mMapView!!.onCreate(mapViewBundle)

        mMapView!!.getMapAsync(this)

        if (mGeoApiContext == null) {
            mGeoApiContext = GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_api_key))
                .build()
        }
    }

    private fun initUserListRecyclerView() {
        mUserRecyclerAdapter = UserRecyclerAdapter(mUserList)
        mUserListRecyclerView!!.setAdapter(mUserRecyclerAdapter)
        mUserListRecyclerView!!.layoutManager = LinearLayoutManager(activity)
    }

    fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mMapView!!.onSaveInstanceState(mapViewBundle)
    }

    fun onResume() {
        super.onResume()
        mMapView!!.onResume()
        startUserLocationsRunnable() // update user locations every 'LOCATION_UPDATE_INTERVAL'
    }

    fun onStart() {
        super.onStart()
        mMapView!!.onStart()
    }

    fun onStop() {
        super.onStop()
        mMapView!!.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        //        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        //                != PackageManager.PERMISSION_GRANTED
        //                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
        //                != PackageManager.PERMISSION_GRANTED) {
        //            return;
        //        }
        //        map.setMyLocationEnabled(true);
        //        mGoogleMap = map;
        //        setCameraView();

        mGoogleMap = map
        addMapMarkers()
    }

    fun onPause() {
        mMapView!!.onPause()
        stopLocationUpdates() // stop updating user locations
        super.onPause()
    }

    fun onDestroy() {
        mMapView!!.onDestroy()
        super.onDestroy()
    }

    fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }


    private fun expandMapAnimation() {
        val mapAnimationWrapper = ViewWeightAnimationWrapper(mMapContainer)
        val mapAnimation = ObjectAnimator.ofFloat(
            mapAnimationWrapper,
            "weight",
            50,
            100
        )
        mapAnimation.setDuration(800)

        val recyclerAnimationWrapper = ViewWeightAnimationWrapper(mUserListRecyclerView)
        val recyclerAnimation = ObjectAnimator.ofFloat(
            recyclerAnimationWrapper,
            "weight",
            50,
            0
        )
        recyclerAnimation.setDuration(800)

        recyclerAnimation.start()
        mapAnimation.start()
    }

    private fun contractMapAnimation() {
        val mapAnimationWrapper = ViewWeightAnimationWrapper(mMapContainer)
        val mapAnimation = ObjectAnimator.ofFloat(
            mapAnimationWrapper,
            "weight",
            100,
            50
        )
        mapAnimation.setDuration(800)

        val recyclerAnimationWrapper = ViewWeightAnimationWrapper(mUserListRecyclerView)
        val recyclerAnimation = ObjectAnimator.ofFloat(
            recyclerAnimationWrapper,
            "weight",
            0,
            50
        )
        recyclerAnimation.setDuration(800)

        recyclerAnimation.start()
        mapAnimation.start()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_full_screen_map -> {

                if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED
                    expandMapAnimation()
                } else if (mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED
                    contractMapAnimation()
                }
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        if (marker.snippet == "This is you") {
            marker.hideInfoWindow()
        } else {

            val builder = AlertDialog.Builder(activity)
            builder.setMessage(marker.snippet)
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    calculateDirections(marker)
                    dialog.dismiss()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, id -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun calculateDirections(marker: Marker) {
        Log.d(TAG, "calculateDirections: calculating directions.")

        val destination = com.google.maps.model.LatLng(
            marker.position.latitude,
            marker.position.longitude
        )
        val directions = DirectionsApiRequest(mGeoApiContext)

        directions.alternatives(true)
        directions.origin(
            com.google.maps.model.LatLng(
                mUserPosition!!.geo_point!!.latitude,
                mUserPosition!!.geo_point!!.longitude
            )
        )
        Log.d(TAG, "calculateDirections: destination: $destination")
        directions.destination(destination)
            .setCallback(object : PendingResult.Callback<DirectionsResult> {
                override fun onResult(result: DirectionsResult) {
                    //                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                    //                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                    //                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                    //                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                    Log.d(TAG, "onResult: successfully retrieved directions.")
                    addPolylinesToMap(result)
                }

                override fun onFailure(e: Throwable) {
                    Log.e(TAG, "calculateDirections: Failed to get directions: " + e.message)

                }
            })
    }


    private fun addPolylinesToMap(result: DirectionsResult) {
        Handler(Looper.getMainLooper()).post {
            Log.d(TAG, "run: result routes: " + result.routes.size)

            for (route in result.routes) {
                Log.d(TAG, "run: leg: " + route.legs[0].toString())
                val decodedPath = PolylineEncoding.decode(route.overviewPolyline.encodedPath)

                val newDecodedPath = ArrayList<LatLng>()

                // This loops through all the LatLng coordinates of ONE polyline.
                for (latLng in decodedPath) {

                    //                        Log.d(TAG, "run: latlng: " + latLng.toString());

                    newDecodedPath.add(
                        LatLng(
                            latLng.lat,
                            latLng.lng
                        )
                    )
                }
                val polyline = mGoogleMap!!.addPolyline(PolylineOptions().addAll(newDecodedPath))
                polyline.color = ContextCompat.getColor(activity, R.color.darkGrey)
                polyline.isClickable = true

            }
        }
    }

    companion object {

        private val TAG = "UserListFragment"
        private val MAP_LAYOUT_STATE_CONTRACTED = 0
        private val MAP_LAYOUT_STATE_EXPANDED = 1


        fun newInstance(): UserListFragment {
            return UserListFragment()
        }

        private val LOCATION_UPDATE_INTERVAL = 3000
    }

*/














