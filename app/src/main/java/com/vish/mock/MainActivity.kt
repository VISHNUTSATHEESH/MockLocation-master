package com.vish.mock

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.mockButton
import java.util.Random

class MainActivity : AppCompatActivity() {

  companion object {
    const val REQUEST_CODE = 1244
    const val centerLatitude = 25.049169
    const val centerLongitude = 121.545440
    const val randomEnable = true
    const val extraDistance = 501
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    mockButton.setOnClickListener {
      if (ActivityCompat.checkSelfPermission(this,
                                             Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
      ) {

        ActivityCompat.requestPermissions(this,
                                          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                                  Manifest.permission.ACCESS_COARSE_LOCATION),
                                          REQUEST_CODE)
      } else {
        mockLocation()
      }
    }
  }

  private fun mockLocation() {
    val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val criteria = Criteria()
    criteria.accuracy = Criteria.ACCURACY_FINE

    val mocLocationProvider = lm.getBestProvider(criteria, true)

    if (mocLocationProvider == null) {
      Toast.makeText(applicationContext, "No location provider found!", Toast.LENGTH_SHORT).show()
      return
    }
    lm.addTestProvider(mocLocationProvider, false, false,
                       false, false, true,
                       true, true, 0, 5)
    lm.setTestProviderEnabled(mocLocationProvider, true)

    val loc = Location(mocLocationProvider)
    val mockLocation = Location(mocLocationProvider) // a string
    val random = Random()
    val extraLatitude = if (randomEnable) randomExtraDistance(random) else 0.0
    val extraLongitude = if (randomEnable) randomExtraDistance(random) else 0.0
    mockLocation.latitude = centerLatitude + extraLatitude
    mockLocation.longitude = centerLongitude + extraLongitude
    mockLocation.altitude = loc.altitude
    mockLocation.time = System.currentTimeMillis()
    mockLocation.accuracy = 1.0f
    mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
    lm.setTestProviderLocation(mocLocationProvider, mockLocation)
    Toast.makeText(applicationContext, "Working", Toast.LENGTH_SHORT).show()
  }

  private fun randomExtraDistance(random: Random): Double {
    return random.nextBoolean().let {
      if (it) {
        random.nextInt(extraDistance) * 0.000001
      } else {
        -random.nextInt(extraDistance) * 0.000001
      }
    }
  }
}
