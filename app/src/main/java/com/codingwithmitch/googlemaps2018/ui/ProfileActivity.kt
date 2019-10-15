package apps.com.codingwithmitch.googlemaps2018.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.UserClient
import com.codingwithmitch.googlemaps2018.ui.IProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.reflect.Array.set


class ProfileActivity : AppCompatActivity(), View.OnClickListener, IProfile {


    //widgets
    private var mAvatarImage: CircleImageView? = null

    //vars
    private lateinit var  mImageListFragment: IProfile.ImageListFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setHomeButtonEnabled(true)
        mAvatarImage = findViewById(R.id.image_choose_avatar)
        val avatar : ImageView =  findViewById(R.id.image_choose_avatar)
            avatar.setOnClickListener(this)

        val tChoose : TextView = findViewById(R.id.text_choose_avatar)
            tChoose.setOnClickListener(this)

        retrieveProfileImage()
    }

    private fun retrieveProfileImage() {
        val requestOptions = RequestOptions()
            .error(R.drawable.cwm_logo)
            .placeholder(R.drawable.cwm_logo)

        var avatar = 0
        try {
            avatar = Integer.parseInt((applicationContext as UserClient).user!!.avatar!!)
        } catch (e: NumberFormatException) {
            Log.e(TAG, "retrieveProfileImage: no avatar image. Setting default. " + e.message)
        }

        Glide.with(this@ProfileActivity)
            .setDefaultRequestOptions(requestOptions)
            .load(avatar)
            .into(mAvatarImage!!)
    }

    override fun onClick(v: View) {
        mImageListFragment = IProfile.ImageListFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.slide_in_down,
                R.anim.slide_out_down,
                R.anim.slide_out_up
            )
            .replace(
                R.id.fragment_container,
                mImageListFragment,
                getString(R.string.fragment_image_list)
            )
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onImageSelected(resource: Int) {

        // remove the image selector fragment
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.slide_in_down,
                R.anim.slide_out_down,
                R.anim.slide_out_up
            )
            .remove(mImageListFragment)
            .commit()

        // display the image
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.cwm_logo)
            .error(R.drawable.cwm_logo)

        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(resource)
            .into(mAvatarImage!!)

        // update the client and database
        val user = (applicationContext as UserClient).user
        user?.avatar = resource.toString()

        user?.let {
            FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().uid!!)
                .set(it)
        }
    }

    companion object {

        private val TAG = "ProfileActivity"
    }

}
