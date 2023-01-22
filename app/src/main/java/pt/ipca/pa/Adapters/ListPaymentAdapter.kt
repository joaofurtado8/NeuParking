package pt.ipca.pa.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import pt.ipca.pa.R
import pt.ipca.pa.Revervation.Reservation
import pt.ipca.pa.Revervation.ReservationView
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ListPaymentAdapter(private val reservations: List<Reservation>, var reservationView: ReservationView) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.reservation_item, parent, false)
        val reservation = reservations[position]

        view.findViewById<TextView>(R.id.date).text = reservation.day
        view.findViewById<TextView>(R.id.amount).text = reservation.amount +"€"

        return view
    }
    override fun getItem(position: Int) = reservations[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = reservations.size
}


