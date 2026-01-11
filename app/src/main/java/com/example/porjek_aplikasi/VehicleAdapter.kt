package com.example.porjek_aplikasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VehicleAdapter(
    private var vehicleList: List<Vehicle>,
    private val onClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>(), Filterable {

    private var vehicleListFiltered: List<Vehicle> = vehicleList
    private var originalList: List<Vehicle> = vehicleList
    private var currentTypeFilter: VehicleType? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicleListFiltered[position]
        holder.bind(vehicle, onClick)
    }

    override fun getItemCount(): Int = vehicleListFiltered.size

    fun filterByType(type: VehicleType?) {
        currentTypeFilter = type
        vehicleListFiltered = if (type == null) {
            originalList
        } else {
            originalList.filter { it.type == type }
        }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                val listToFilter = if (currentTypeFilter == null) {
                    originalList
                } else {
                    originalList.filter { it.type == currentTypeFilter }
                }
                
                vehicleListFiltered = if (charString.isEmpty()) {
                    listToFilter
                } else {
                    val filteredList = ArrayList<Vehicle>()
                    for (row in listToFilter) {
                        if (row.name.lowercase().contains(charString.lowercase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = vehicleListFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                vehicleListFiltered = (results?.values as? List<Vehicle>) ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivVehicle: ImageView = itemView.findViewById(R.id.iv_vehicle)
        private val tvVehicleName: TextView = itemView.findViewById(R.id.tv_vehicle_name)
        private val tvVehicleDescription: TextView = itemView.findViewById(R.id.tv_vehicle_description)

        fun bind(vehicle: Vehicle, onClick: (Vehicle) -> Unit) {
            ivVehicle.setImageResource(vehicle.image)
            tvVehicleName.text = vehicle.name
            tvVehicleDescription.text = vehicle.description
            itemView.setOnClickListener { onClick(vehicle) }
        }
    }
}