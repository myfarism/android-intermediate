package com.dicoding.picodiploma.loginwithanimation.view.maps

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.api.Story
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)
        val factory = ViewModelFactory.getInstance(this)
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fetchStories()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Set default location if no markers are added
        val defaultLocation = LatLng(-6.200000, 106.816666) // Jakarta
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))
    }

    private fun fetchStories() {
        lifecycleScope.launch(Dispatchers.IO) {  // Move to IO thread for network call
            userPreference.getSession().collect { user ->
                if (user.token.isNotEmpty()) {
                    val token = "Bearer ${user.token}"
                    Log.d("MapsActivity", "Using token: $token")
                    // Fetch stories with location in background thread
                    mapsViewModel.fetchStoriesWithLocation(token)
                } else {
                    Log.e("MapsActivity", "Token is empty")
                }
            }
        }

        // Observe data from ViewModel on main thread to update UI
        mapsViewModel.stories.observe(this) { stories ->
            if (stories != null && stories.isNotEmpty()) {
                addMarkers(stories)  // This is UI manipulation, which must be on main thread
            } else {
                Log.e("MapsActivity", "No stories available")
            }
        }
    }

    private fun addMarkers(stories: List<Story>) {
        val boundsBuilder = LatLngBounds.Builder()
        var hasValidMarker = false

        // Adding markers efficiently in a background thread to avoid blocking UI thread
        lifecycleScope.launch(Dispatchers.Main) {
            stories.forEach { story ->
                val lat = story.lat
                val lon = story.lon
                if (lat != null && lon != null) {
                    val latLng = LatLng(lat, lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(story.name)
                            .snippet(story.description)
                    )
                    boundsBuilder.include(latLng)
                    hasValidMarker = true
                }
            }

            // Update map bounds once all markers are added
            if (hasValidMarker) {
                val bounds = boundsBuilder.build()
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            } else {
                Log.e("MapsActivity", "No valid markers to display")
            }
        }
    }
}
