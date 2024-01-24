package com.thinkgas.heatapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.messaging.FirebaseMessaging
import com.thinkgas.heatapp.base.BaseActivity
import com.thinkgas.heatapp.service.NetworkMonitorService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    var keepSplashOnScreen = true
    val TOPIC = "all_user"
    val delay = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen().setKeepOnScreenCondition { keepSplashOnScreen }
        //FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
//        Handler(Looper.getMainLooper()).postDelayed({ keepSplashOnScreen = false }, delay)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()
        NetworkMonitorService.startService(this)
    }

    override fun onStop() {
        super.onStop()
        NetworkMonitorService.stopService(this)
    }
}