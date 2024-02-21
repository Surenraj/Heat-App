package com.thinkgas.heatapp.supportActivities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.service.NetworkMonitorService

class NoInternetActivity : AppCompatActivity() {

    lateinit var retryBtn: Button

    override fun onResume() {
        super.onResume()
        if (isOnline(this)) {
            NetworkMonitorService.startService(this)
            finish()
        } else {
            Toast.makeText(this, "No Network Available", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_no_internet)

        retryBtn = findViewById(R.id.btn_retry)


        retryBtn.setOnClickListener {
            if (isOnline(this)) {
                NetworkMonitorService.startService(this)
                finish()
            } else {
                Toast.makeText(this, "No Network Available", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        NetworkMonitorService.startService(this)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }
}