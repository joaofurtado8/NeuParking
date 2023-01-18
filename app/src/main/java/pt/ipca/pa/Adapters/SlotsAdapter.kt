package pt.ipca.pa.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import pt.ipca.pa.Park.Slot
import pt.ipca.pa.R
import pt.ipca.pa.Slot.SlotView


class SlotsAdapter(private val slots: List<Slot>, var slotsView: SlotView) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.slot_item, parent, false)
        val slot = slots[position]
        view.findViewById<TextView>(R.id.slot_name_tv).text = slot.name
        if (slot.ocupied == "False"){
            view.findViewById<TextView>(R.id.ocupied_spots_tv).text = "Free"
        }
        else {
            view.findViewById<TextView>(R.id.ocupied_spots_tv).text = "Occupied"
        }
        return view
    }

    override fun getItem(position: Int) = slots[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = slots.size


}