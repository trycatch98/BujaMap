package com.buja.map.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buja.map.databinding.ItemCoordinatesBinding

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val coordinatesList = mutableListOf<Coordinates>()
    private var selectPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCoordinatesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = coordinatesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun addCoordinates() {
        coordinatesList.add(Coordinates())
        notifyItemInserted(coordinatesList.size)
    }

    fun setCoordinatesList(coordinatesList: List<Coordinates>) {
        this.coordinatesList.addAll(coordinatesList)
    }

    fun deleteItem() {
        if (selectPosition != -1) {
            this.coordinatesList.removeAt(selectPosition)
            notifyDataSetChanged()
            selectPosition = -1
        }
    }

    fun getItems() = coordinatesList

    inner class ViewHolder(private val binding: ItemCoordinatesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val coordinates: Coordinates = coordinatesList[position]
            binding.number = "${position + 1}."
            binding.coordinates = coordinates
//            binding.x.apply {
//                setText(coordinates.x)
//                addTextChangedListener {
//                    coordinates.x = "$it"
//                }
//            }
//            binding.y.apply {
//                setText(coordinates.y)
//                addTextChangedListener {
//                    coordinates.y = "$it"
//                }
//            }
            if (position == selectPosition)
                itemView.setBackgroundColor(Color.LTGRAY)
            else
                itemView.setBackgroundColor(Color.WHITE)

            itemView.setOnClickListener {
                notifyItemChanged(selectPosition)
                selectPosition = position
                notifyItemChanged(selectPosition)
            }
        }
    }
}