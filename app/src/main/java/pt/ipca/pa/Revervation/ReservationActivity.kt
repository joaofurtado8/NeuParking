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
    lateinit var editEndTime : EditText
    lateinit var editDay : EditText
    lateinit var reservationBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        editStartTime = findViewById(R.id.start_et)
        editEndTime = findViewById(R.id.end_et)
        editDay = findViewById(R.id.day_et)
        reservationBtn = findViewById(R.id.reservation_bt)

        val token = intent.getStringExtra("TOKEN")



        val retrofit = Retrofit.Builder()
            .baseUrl("https://smart-api.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val reservationService = retrofit.create(ReserveService::class.java)

        GlobalScope.launch {
            reservationService.addReservation(reservation, "Bearer $token")
                .enqueue(object : Callback<Reservation> {
                    override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {
                        if (response.isSuccessful) {
                            println("Reservation added successfully")
                        } else {
                            val error = response.errorBody()?.string()
                            println(error)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@ReservationActivity, error, Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Reservation>, t: Throwable) {
                        println("Error: $t")
                    }
                })
        }



        val retrofit = ReserveService.buildService(RestApi::class.java, okHttpClient)
            retrofit.addUser(userData).enqueue(
                object : Callback<UserInfo> {
                    override fun onFailure(call: Call<Reservation>, t: Throwable) {
                        onResult(null)
                    }

                    override fun onResponse( call: Call<Reservation>, response: Response<Reservation>) {
                        val addedUser = response.body()
                        onResult(addedUser)
                    }
                }
            )
        }

}



fun addReservation(reservation: Reservation, token: String, context: Context) {
    GlobalScope.launch {
        reservationService.addReservation(reservation, "Bearer $token")
            .enqueue(object : Callback<Reservation> {
                override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {
                    if (response.isSuccessful) {
                        println("Reservation added successfully")
                    } else {
                        val error = response.errorBody()?.string()
                        println(error)
                        withContext(Dispatchers.Main) {
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



