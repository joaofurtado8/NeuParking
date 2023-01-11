package pt.ipca.pa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
/*
class PaymentActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var reservations = listOf<Reservation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservations)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ReservationsAdapter(reservations)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        }

        // Replace "user123" with the actual user ID
        val userId = "user123"
        getReservations(userId)
    }


}

private fun getReservations(userId: String) {
    // Make an HTTP GET request to the API using the user ID
    val url = "https://smart-api.onrender.com/reservationUser/$userId"
    val request = Request.Builder().url(url).build()
    val client = OkHttpClient()
    client.newCall(request).enqueue(object : Callback {
        fun onFailure(call: Call, e: IOException) {
            // Handle the error
        }

        fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            // Convert the JSON string to a list of Reservation objects
            val reservations = Gson().fromJson(body, Array<Reservation>::class.java).toList()
            runOnUiThread {
                // Update the RecyclerView with the new list of reservations
                viewAdapter = ReservationsAdapter(reservations)
                recyclerView.adapter = viewAdapter
            }
        }
    })
}

class ReservationsAdapter(private val reservations: List<Reservation>) :
    RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder>() {

    class ReservationViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.reservation_item, parent, false) as TextView
        return ReservationViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.textView.text = reservation.toString()
    }

    override fun getItemCount() = reservations.size
}
}
*/
