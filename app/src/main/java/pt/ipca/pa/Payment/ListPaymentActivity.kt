package pt.ipca.pa.Payment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipca.pa.Adapters.ListPaymentAdapter
import pt.ipca.pa.Park.Park
import pt.ipca.pa.Park.isConnected
import pt.ipca.pa.R
import pt.ipca.pa.Revervation.Reservation
import pt.ipca.pa.Revervation.ReservationView
import pt.ipca.pa.SQLite.DataBaseHandlerPark
import pt.ipca.pa.SQLite.DataBaseHandlerReservation
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
        paymentsList = findViewById<ListView>(R.id.list_view)

        if (!isConnected(this@ListPaymentActivity)) {
            val db = DataBaseHandlerReservation(this@ListPaymentActivity)
            val reservations = db.getReservationList()
            for (res in reservations) {
                println("teste $res")
            }
            println(reservations)
            paymentsList.adapter = ListPaymentAdapter(reservations, this@ListPaymentActivity)
            return
        }else{
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            val user: User = intent.getSerializableExtra(ConstantsUtils.TOKEN) as User


            paymentsList.setOnItemClickListener { parent, view, position, id ->

                val item = parent.getItemAtPosition(position) as Reservation
                val intent = Intent(this@ListPaymentActivity, PaymentActivity::class.java)
                intent.putExtra(ConstantsUtils.TOKEN, user)
                intent.putExtra("RES", item.id)
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

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true;
    }

    override fun onAllReservationsSuccess(response: Response<List<Reservation>>) {
        println("Reservations received")
        response.body()?.let { reservations ->
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    paymentsList.adapter = ListPaymentAdapter(reservations, this@ListPaymentActivity)
                    val db = DataBaseHandlerReservation(this@ListPaymentActivity)
                    for (reservation in reservations) {
                        val dateFormat = SimpleDateFormat("HH:mm:ss")
                        val startTime = dateFormat.parse(reservation.startTime)
                        val endTime = dateFormat.parse(reservation.endTime)
                        val diffInMillisec = endTime.time - startTime.time
                        val diffInMinutes = diffInMillisec / (60 * 1000) % 60
                        val diffInHours = diffInMillisec / (60 * 60 * 1000) + diffInMinutes / 60.0
                        val amount = String.format("%.2f", diffInHours * 1.25)
                        reservation.amount= amount
                        println(reservation.day)
                        db.addReservation(reservation)
                    }
                    val reservations = db.getReservationList()
                    val dbt: List<Reservation> = db.getReservationList();
                    for (res in reservations) {
                        println("teste $res")
                    }
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
    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}
