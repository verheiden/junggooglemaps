package com.codingwithmitch.googlemaps2018.util

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.models.ClusterMarker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import org.jetbrains.annotations.NotNull

class MyClusterManagerRenderer(context : Context, googleMap: GoogleMap,
                               clusterManager : ClusterManager<ClusterMarker>
) : DefaultClusterRenderer<ClusterMarker>(context, googleMap, clusterManager)
{
    private val iconGenerator: IconGenerator
    private val imageView: ImageView
    private var markerWidth: Int
    private val markerHeight: Int
    init{
        iconGenerator = IconGenerator(context.applicationContext)
        imageView = ImageView(context.applicationContext)
        markerWidth = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
        markerHeight = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
        imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight);
        val padding : Int = context.resources.getDimension(R.dimen.custom_marker_padding).toInt()
        imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)
    }

    /**
     * Rendering of the individual ClusterItems
     * @param item
     * @param markerOptions
     */
    override fun onBeforeClusterItemRendered(item: ClusterMarker, markerOptions: MarkerOptions) {

        imageView.setImageResource(item.iconPicture)
        val icon = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.title)
    }

    override fun shouldRenderAsCluster(@NotNull cluster: Cluster<ClusterMarker>): Boolean {
        return false
    }

    /**
     * Update the GPS coordinate of a ClusterItem
     * @param clusterMarker
     */
    fun setUpdateMarker(clusterMarker: ClusterMarker) {
        val marker = getMarker(clusterMarker)
        if (marker != null) {
            marker.position = clusterMarker.position!!
        }
    }
}






