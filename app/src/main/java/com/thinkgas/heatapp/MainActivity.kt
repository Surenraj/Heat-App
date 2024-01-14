package com.thinkgas.heatapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.messaging.FirebaseMessaging
import com.thinkgas.heatapp.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    var keepSplashOnScreen = true
    val TOPIC = "all_user"
    val delay = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { keepSplashOnScreen }
        //FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        Handler(Looper.getMainLooper()).postDelayed({ keepSplashOnScreen = false }, delay)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}