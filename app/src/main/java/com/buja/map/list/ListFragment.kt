package com.buja.map.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.buja.map.R
import com.buja.map.databinding.FragmentListBinding

class ListFragment : Fragment() {
    companion object {
        var coordinatesList: List<Coordinates> = listOf(Coordinates())
        // Coordinates("37.5374", "127.0982"), Coordinates("37.5367", "127.0946"), Coordinates("37.5387", "127.0986")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentListBinding.inflate(inflater, container, false)
        val listAdapter = ListAdapter()
        coordinatesList.let {
            listAdapter.setCoordinatesList(it)
        }
        binding.list.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.add.setOnClickListener {
            listAdapter.addCoordinates()
        }
        binding.delete.setOnClickListener {
            listAdapter.deleteItem()
        }
        binding.finish.setOnClickListener {
            coordinatesList = listAdapter.getItems()
            findNavController().navigate(R.id.action_global_mapFragment)
        }
        return binding.root
    }
}