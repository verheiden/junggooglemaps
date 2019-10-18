package com.codingwithmitch.googlemaps2018.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.adapters.ImageListRecyclerAdapter

import java.util.ArrayList

interface IProfile {

    fun onImageSelected(resource: Int)

    /**
     * A simple [Fragment] subclass.
     * Use the [ImageListFragment.newInstance] factory method to
     * create an instance of this fragment.
     */
    class ImageListFragment : Fragment(), ImageListRecyclerAdapter.ImageListRecyclerClickListener {

        //widgets
        private var mRecyclerView: RecyclerView? = null


        //vars
        private val mImageResources = ArrayList<Int>()
        private var mIProfile: IProfile? = null

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_image_list, container, false)
            mRecyclerView = view.findViewById(R.id.image_list_recyclerview)

            getImageResouces()
            initRecyclerview()

            return view
        }

        private fun getImageResouces() {
            mImageResources.add(R.drawable.cwm_logo)
            mImageResources.add(R.drawable.cartman_cop)
            mImageResources.add(R.drawable.eric_cartman)
            mImageResources.add(R.drawable.ike)
            mImageResources.add(R.drawable.kyle)
            mImageResources.add(R.drawable.satan)
            mImageResources.add(R.drawable.chef)
            mImageResources.add(R.drawable.tweek)
        }

        private fun initRecyclerview() {
            val mAdapter = ImageListRecyclerAdapter(activity!!, mImageResources, this)
            val staggeredGridLayoutManager =
                StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL)
            mRecyclerView!!.layoutManager = staggeredGridLayoutManager
            mRecyclerView!!.adapter = mAdapter
        }

        override fun onAttach(context: Context?) {
            super.onAttach(context)
            mIProfile = activity as IProfile?
        }

        override fun onImageSelected(position: Int) {
            mIProfile!!.onImageSelected(mImageResources[position])
        }

        companion object {

            private val TAG = "ImageListFragment"
            private val NUM_COLUMNS = 2


            fun newInstance(): ImageListFragment {
                return ImageListFragment()
            }
        }
    }// Required empty public constructor
}
