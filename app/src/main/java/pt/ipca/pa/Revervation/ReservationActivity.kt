package pt.ipca.pa.Revervation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import pt.ipca.pa.Park.Park
import pt.ipca.pa.Park.ParkService
import pt.ipca.pa.Park.StatsActivity
import pt.ipca.pa.R
import pt.ipca.pa.SQLite.DataBaseHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ReservationActivity : AppCompatActivity() {
    lateinit var editStartTime: EditText
    lateinit var editEndTime: EditText
    lateinit var editDay: EditText
    lateinit var reservationBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        editStartTime = findViewById(R.id.start_et)
        editEndTime = findViewById(R.id.end_et)
        editDay = findViewById(R.id.day_et)
        reservationBtn = findViewById(R.id.reservation_bt)

        val token = intent.getStringExtra("TOKEN")

        val reservation = Reservation(
            "Ze5cr892j9A9PGXluVtR",
            "63a1caec7bd2350a9d54c515",
            "17:00",
            "18:00",
            "16-06-2022"
        )

        addReservation(reservation, token!!, this@ReservationActivity)

    }


    fun addReservation(reservation: Reservation, token: String, context: Context) {
        println("in Function")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://smart-api.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val reservationService = retrofit.create(ReserveService::class.java)

        reservationService.addReservation(reservation, "Bearer $token")
            .enqueue(object : Callback<Reservation> {
                override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {

                    if (response.isSuccessful) {
                        println("Reservation added successfully")
                    } else {
                        val error = response.errorBody()?.string()
                        print(error)
                        println("in here")
                        GlobalScope.launch(Dispatchers.Main) {
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Reservation>, t: Throwable) {
                    println("Error: $t")
                }
            })
    }
}

