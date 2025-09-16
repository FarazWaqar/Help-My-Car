package com.example.helpmycar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.round

data class MechanicItem(val mechanic: Mechanic, val distanceKm: Double)

class MechanicsAdapter(
    private var items: List<MechanicItem>,
    private val onClick: (MechanicItem) -> Unit
) : RecyclerView.Adapter<MechanicsAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val imgAvatar: ImageView = v.findViewById(R.id.imgAvatar)
        val txtName: TextView = v.findViewById(R.id.txtName)
        val txtLevel: TextView = v.findViewById(R.id.txtLevel)
        val ratingBar: RatingBar = v.findViewById(R.id.ratingBar)
        val txtRatingCount: TextView = v.findViewById(R.id.txtRatingCount)
        val txtDistance: TextView = v.findViewById(R.id.txtDistance)
        val txtExpertise: TextView = v.findViewById(R.id.txtExpertise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mechanic, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val row = items[pos]
        val m = row.mechanic

        h.txtName.text = m.name ?: "Mechanic"
        h.txtLevel.text = m.level ?: "Expert"
        h.txtExpertise.text = m.expertise ?: ""
        h.ratingBar.rating = (m.ratingAvg ?: 4.5).toFloat()
        h.txtRatingCount.text = "(${m.ratingCount ?: 1})"
        val km = kotlin.math.round(row.distanceKm * 10.0) / 10.0
        h.txtDistance.text = "$km km away"

        h.itemView.setOnClickListener { onClick(row) }
    }

    fun submit(list: List<MechanicItem>) {
        items = list
        notifyDataSetChanged()
    }
}
