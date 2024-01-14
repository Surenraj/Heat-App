package com.thinkgas.heatapp.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object AppUtils{
     fun distanceInKms(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Float {
        var results = FloatArray(1)
        Location.distanceBetween(startLat,startLon,endLat,endLon,results)
        return results[0]/1000
    }

    fun getDate(dateString: String): String? {
        val dateFormat = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        )
        val timeFormat = SimpleDateFormat("dd-MM-yyyy")
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        try {
            return timeFormat.format(dateFormat.parse(dateString)!!)
        } catch (e: ParseException) {
            return null
        }
    }

    fun getFollowUpDateTime(dateString: String): String? {
        val dateFormat = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        )
        val timeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
//        dateFormat.timeZone = TimeZone.getTimeZone("IN")
        try {
            return timeFormat.format(dateFormat.parse(dateString)!!)
        } catch (e: ParseException) {
            return null
        }
    }

     fun addIntentsToList(
        context: Context,
        list: MutableList<Intent>,
        intent: Intent
    ): MutableList<Intent> {
        val resInfo = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.setPackage(packageName)
            list.add(targetedIntent)
        }
        return list
    }

    fun getAddress(lat: Double, lng: Double,context: Context):String?{
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1) as List<Address>
            if(addresses.size>0) {
                val obj: Address = addresses[0]
                var add: String = obj.getAddressLine(0)
                return obj.getAddressLine(0)
            }else{
                return "location not found"
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun getCityName(lat: Double, lng: Double,context: Context):String?{
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1) as List<Address>
            val obj: Address = addresses[0]
            var add: String = obj.getAddressLine(0)
            return obj.locality

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

}


@SuppressLint("Range")
fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return name
}

@SuppressLint("Range")
fun ContentResolver.getFileSize(uri: Uri): String {
    var size = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        size = "" + cursor.getString(it.getColumnIndex(OpenableColumns.SIZE))
    }
    return size
}
fun setProgressDialog(context: Context) {
    val llPadding = 30
    val ll = LinearLayout(context)
    ll.orientation = LinearLayout.HORIZONTAL
    ll.setPadding(llPadding, llPadding, llPadding, llPadding)
    ll.gravity = Gravity.CENTER
    var llParam = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    llParam.gravity = Gravity.CENTER
    ll.layoutParams = llParam
    val progressBar = ProgressBar(context)
    progressBar.isIndeterminate = true
    progressBar.setPadding(0, 0, llPadding, 0)
    progressBar.layoutParams = llParam
    llParam = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    llParam.gravity = Gravity.CENTER
    val tvText = TextView(context)
    tvText.text = "Loading ..."
    tvText.setTextColor(Color.parseColor("#000000"))
    tvText.textSize = 20f
    tvText.layoutParams = llParam
    ll.addView(progressBar)
    ll.addView(tvText)
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder.setCancelable(true)
    builder.setView(ll)
    val dialog: AlertDialog = builder.create()
    dialog.show()
    val window: Window = dialog.getWindow()!!
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(dialog.getWindow()!!.getAttributes())
    layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
    dialog.getWindow()!!.setAttributes(layoutParams)
}