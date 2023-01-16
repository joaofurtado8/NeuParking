
package pt.ipca.pa.Payment

import android.content.Intent
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
import pt.ipca.pa.Park.Slot
import pt.ipca.pa.R
import pt.ipca.pa.Revervation.Reservation
import pt.ipca.pa.Revervation.ReservationActivity
import pt.ipca.pa.Revervation.ReservationView
import pt.ipca.pa.SQLite.DataBaseHandler
import pt.ipca.pa.controller.ReservationController
import pt.ipca.pa.data.User
import pt.ipca.pa.model.ReservationModel
import pt.ipca.pa.utils.ConstantsUtils
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ListPaymentActivity :ReservationView, AppCompatActivity() {

    lateinit var paymentsList: ListView
    private val viewModel = ReservationModel()
    private val controller = ReservationController(viewModel)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_payment)
        val user: User = intent.getSerializableExtra(ConstantsUtils.TOKEN) as User

        paymentsList = findViewById<ListView>(R.id.list_view)

        paymentsList.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as Reservation
            val intent = Intent(this@ListPaymentActivity, PaymentActivity::class.java)
            intent.putExtra(ConstantsUtils.TOKEN, user)
            intent.putExtra(ConstantsUtils.SLOT_ID, item.id)
            intent.putExtra(ConstantsUtils.USER_ID, user.userID)
            intent.putExtra(ConstantsUtils.AMOUNT, item.amount)


            this@ListPaymentActivity.startActivity(intent)

            Toast.makeText(this@ListPaymentActivity, "You clicked on ${item.id}", Toast.LENGTH_SHORT).show()
        }

        controller.bind(this@ListPaymentActivity)
        GlobalScope.launch {
            controller.getReservationsByUser(user.token, user.userID.toString())
        }
    }

    class ListPaymentAdapter(private val reservations: List<Reservation>,var reservationView: ReservationView) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.reservation_item, parent, false)
            val reservation = reservations[position]

            val dateFormat = SimpleDateFormat("HH:mm:ss")
            val startTime = dateFormat.parse(reservation.startTime)
            val endTime = dateFormat.parse(reservation.endTime)
            val diffInMillisec = endTime.time - startTime.time
            val diffInMinutes = diffInMillisec / (60 * 1000) % 60
            val diffInHours = diffInMillisec / (60 * 60 * 1000) + diffInMinutes / 60.0
            val decimalFormat = DecimalFormat("#.##")
            val amount = String.format("%.2f", diffInHours * 1.25).toDouble()

            view.findViewById<TextView>(R.id.date).text = reservation.day.toString()
            view.findViewById<TextView>(R.id.amount).text = amount.toString()+"€"
            reservation.amount = amount

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
