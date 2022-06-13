package com.buja.map

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.buja.map.databinding.FragmentMapBinding
import com.buja.map.list.ListFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.Utmk
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMapBinding
    private val markers = mutableListOf<Marker>()
    private var job: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment?.getMapAsync(this)


        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        context?.let {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
        }

        binding.ok.setOnClickListener {
            markers[lastIndex].iconTintColor = Color.GRAY
            lastIndex++
            binding.ok.isEnabled = false
            if (lastIndex < markers.size) {
                markers[lastIndex].iconTintColor = Color.rgb(255,215,0)
            }
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        naverMap.addOnCameraChangeListener { i, b ->
            job?.cancel()
            job = GlobalScope.launch {
                delay(1500L)
                withContext(Dispatchers.Main) {
                    naverMap.locationTrackingMode = LocationTrackingMode.Face
                }
            }
        }

        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Face
        val coords = ListFragment.coordinatesList
            .filter {
                it.x != ""
            }.map {
            LatLng(it.x.toDouble(), it.y.toDouble())
        }.toMutableList()
        val polyline = PolylineOverlay()
        val passedpolyline = PolylineOverlay()

        polyline.setPattern(10, 5)
        polyline.color = Color.BLUE
        passedpolyline.setPattern(10, 5)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                location?.let {
                    val cameraUpdate = CameraUpdate.zoomTo(17.0)
                    naverMap.moveCamera(cameraUpdate)
                    coords.forEach {
                        val marker = Marker()
                        marker.position = it
                        marker.map = naverMap
                        marker.icon = MarkerIcons.BLACK
                        marker.iconTintColor = Color.rgb(147,112,219)
                        markers.add(marker)
                    }
                    if (coords.size > 0) {
                        markers[0].iconTintColor = Color.rgb(255, 215, 0)
                        coords.add(lastIndex, LatLng(it.latitude, it.longitude))
                        polyline.coords = coords.slice(IntRange(lastIndex, coords.size - 1))
                    }
                    polyline.map = naverMap
                    passedpolyline.map = naverMap
                }

                naverMap.addOnLocationChangeListener {
                    if (lastIndex < coords.size - 1) {
                        coords[lastIndex] = LatLng(it.latitude, it.longitude)
                        val current = Utmk.valueOf(LatLng(it.latitude, it.longitude))
                        val destination = coords[lastIndex + 1].let {
                            Utmk.valueOf(LatLng(it.latitude, it.longitude))
                        }
                        val distance = (sqrt(
                            abs((current.x - destination.x)).pow(2) + abs((current.y - destination.y)).pow(2)
                        )).roundToInt()
                        binding.ok.isEnabled = distance <= 10
                        binding.distance.text = "오차 : ${distance}m"
                        polyline.coords = coords.slice(IntRange(lastIndex, coords.size - 1))
                        if (lastIndex > 0)
                            passedpolyline.coords = coords.slice(IntRange(0, lastIndex))
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private var lastIndex = 0
    }
}