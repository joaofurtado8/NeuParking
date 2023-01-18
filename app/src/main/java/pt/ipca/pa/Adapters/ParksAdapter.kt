package pt.ipca.pa.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import pt.ipca.pa.Park.Park
import pt.ipca.pa.Park.StatsView
import pt.ipca.pa.R

class ParksAdapter(private val parks: List<Park>, var statsView: StatsView) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.park_item, parent, false)
        val park = parks[position]
        view.findViewById<TextView>(R.id.park_name_tv).text = park.name
        view.findViewById<TextView>(R.id.park_free_spots_tv).text =
            park.availableSpots.toString() + " free spaces"
        view.setOnClickListener {
            statsView.onParkClick(park)
        }
        return view
    }

    override fun getItem(position: Int) = parks[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = parks.size


}