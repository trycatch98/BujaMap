package com.buja.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.buja.map.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        binding.list.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_listFragment)
        }
        binding.map.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_mapFragment)
        }

        setContentView(binding.root)
    }
}