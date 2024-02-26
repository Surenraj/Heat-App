package com.thinkgas.heatapp.supportActivities

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.service.LocationMonitorService
import com.thinkgas.heatapp.service.NetworkMonitorService

class EnableLocationActivity : AppCompatActivity() {

    private lateinit var enableBtn: Button
    override fun onResume() {
        super.onResume()
        if (LocationMonitorService.isLocationEnabled(this)) {
            LocationMonitorService.startService(this)
            finish()
        } else {
            Toast.makeText(this, "Enable location permission", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_enable_location)

        enableBtn = findViewById(R.id.btn_enable)

        enableBtn.setOnClickListener {
            if (LocationMonitorService.isLocationEnabled(this)) {
                NetworkMonitorService.startService(this)
                finish()
            } else {
                Toast.makeText(this, "Enable location permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        LocationMonitorService.startService(this)
    }
}