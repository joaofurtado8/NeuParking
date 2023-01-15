
package pt.ipca.pa.Payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipca.pa.R
import pt.ipca.pa.Revervation.Reservation
import pt.ipca.pa.Revervation.ReservationView
import pt.ipca.pa.SQLite.DataBaseHandler
import pt.ipca.pa.controller.ReservationController
import pt.ipca.pa.data.User
import pt.ipca.pa.model.ReservationModel
import pt.ipca.pa.utils.ConstantsUtils
import retrofit2.Response

class ListPaymentActivity :ReservationView, AppCompatActivity() {

    lateinit var paymentsList: ListView
    private val viewModel = ReservationModel()
    private val controller = ReservationController(viewModel)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_payment)

        paymentsList = findViewById<ListView>(R.id.list_view)


        val user: User = intent.getSerializableExtra(ConstantsUtils.TOKEN) as User

        controller.bind(this@ListPaymentActivity)
        GlobalScope.launch {
            controller.getReservationsByUser(user.token)
        }

    }

    class ListPaymentAdapter(private val reservations: List<Reservation>,var reservationView: ReservationView) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.reservation_item, parent, false)
            val reservation = reservations[position]

            reservation.endTime

            //val amount = (reservation.endTime.time - reservation.startTime.time) * 1

            view.findViewById<TextView>(R.id.date).text = reservation.day.toString()
            // view.findViewById<TextView>(R.id.amount).text = amount.toString()




            return view
        }
        override fun getItem(position: Int) = reservations[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = reservations.size
    }


    override fun onAllReservationsSuccess(response: Response<List<Reservation>>) {
        println("Reservations received")
        response.body()?.let { reservations ->
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    paymentsList.adapter = ListPaymentAdapter(reservations, this@ListPaymentActivity)
                    val db = DataBaseHandler(this@ListPaymentActivity)
//                    for (park in parks) {
//                        db.addPark(park)
//                        println("park added: $park.name")
//                    }
//
//
//                    val dbt: List<Park> = db.getParksList();
//                    for (park in dbt) {
//                        db.addPark(park)
//                        println("lindo: $park.name")
//                    }
                }
            }

        }

    }



    override fun onAllReservationsError(error: String) {
        println("onAllReservationsError $error")
        // Toast.makeText(this@ListPaymentActivity, error, Toast.LENGTH_LONG).show()

    }

    override fun onReservationClick(reservation: Reservation) {

        // Toast.makeText(this@ListPaymentActivity, reservation.slotId, Toast.LENGTH_SHORT).show()
    }
}
