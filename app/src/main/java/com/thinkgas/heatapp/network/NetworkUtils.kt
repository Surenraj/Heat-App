import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.widget.Toast

object NetworkUtils {

    fun getNetworkType(context: Context): Int {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        return networkInfo?.type ?: -1
    }

    fun getNetworkSubtype(context: Context): Int {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        return networkInfo?.subtype ?: -1
    }

    fun getWifiSpeed(context: Context): Int {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo

        return wifiInfo?.linkSpeed ?: -1
    }

    fun handleNetworkType(context: Context) {
//        when (getNetworkType(context)) {
//            ConnectivityManager.TYPE_WIFI -> {
//                handleWifiNetwork(context)
//            }
//            ConnectivityManager.TYPE_MOBILE -> {
////                handleMobileNetworkType(context)
//            }
//            else -> {
//                // Handle other network types
//            }
//        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleWifiNetwork(context: Context) {
        val wifiSpeed = getWifiSpeed(context)

        if (wifiSpeed != -1) {
            // Assuming a threshold of 10 Mbps for slow WiFi, you can adjust as needed
            if (wifiSpeed < 1000) {
                // WiFi is slow, show the no network screen with an image
                showNoNetworkScreen(context)
            } else {
                // WiFi is fast enough, continue with normal processing
            }
        } else {
            // WiFi speed information not available, handle accordingly
        }
    }

    private fun showNoNetworkScreen(context: Context) {
        showToast(context, "No Internet Connection")
    }
}
