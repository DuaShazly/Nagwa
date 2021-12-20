package com.example.nagwaassignment.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso


open class AppConstants {

    companion object {
        val LOADING = "Loading"
        val LOADED = "Loaded"
        val URL: String = "url"


        const val APP_NAME: String = "app_name"
        const val Action: String = "com.example.nagwaassignment.myBroadcastReceiver"
        const val ItemPostion: String = "itemPostion"
        const val attachObj: String = "attachObj"
        const val Progress: String = "Progress"
        const val Complete: String = "Complete"
        const val Nagwa: String = "Nagwa"
        fun checkInternetConnection(context: Context): Boolean {
            val connectivity = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.allNetworkInfo
            for (anInfo in info) {
                if (anInfo.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
            return false
        }

        @JvmStatic
        @BindingAdapter("bind:imageUrl")
        fun ImageView.loadImage(url: String) {
            Picasso.with(this.context)
                .load(url)
                .error(android.R.drawable.ic_menu_report_image).into(this)
        }
    }
}