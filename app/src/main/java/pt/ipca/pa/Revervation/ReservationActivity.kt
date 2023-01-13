package pt.ipca.pa.Revervation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
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
import java.io.InputStreamReader


class ReservationActivity : AppCompatActivity() {
    lateinit var editStartTime: EditText
    lateinit var editEndTime: EditText
    lateinit var editDay: EditText
    lateinit var reservationBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)
        val token = intent.getStringExtra("TOKEN")
        editStartTime = findViewById(R.id.start_et)
        editEndTime = findViewById(R.id.end_et)
        editDay = findViewById(R.id.day_et)
        reservationBtn = findViewById(R.id.reservation_bt)

        reservationBtn.setOnClickListener {

            val reservation = Reservation(
                "Ze5cr892j9A9PGXluVtR",
                "63a1caec7bd2350a9d54c515",
                editStartTime.text.toString(),
                editEndTime.text.toString(),
                editDay.text.toString()
            )
            /*
            val response = ServiceBuilder.buildService(ReserveService::class.java)
            response.addReservation(reservation, "Bearer $token").enqueue(
                object : Callback<Reservation> {
                    override fun onResponse(
                        call: Call<Reservation>,
                        response: Response<Reservation>
                    ) {
                        if (response.isSuccessful) {
                            // Change activities if the post is successful
                            println("added")
                            val intent = Intent(this@ReservationActivity, StatsActivity::class.java)
                            intent.putExtra("TOKEN", token)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@ReservationActivity,response.message().toString(),Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<Reservation>, t: Throwable) {
                        Toast.makeText(this@ReservationActivity,t.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            )
            */
             addReservation(reservation, token!!, this@ReservationActivity)
        }
    }

    fun addReservation(reservation: Reservation, token: String, context: Context) {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://smart-api.onrender.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


        val reservationService = retrofit.create(ReserveService::class.java)
        val json = Gson().toJson(reservation)
        val requestBody = RequestBody.create("application/json".toMediaType(), json)
        Log.d("requestBody", json)
        reservationService.addReservation(reservation, "Bearer $token")
            .enqueue(object : Callback<Reservation> {
                override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {

                    if (response.isSuccessful) {
                        println("Reservation added successfully")
                        val intent = Intent(this@ReservationActivity, StatsActivity::class.java)
                        intent.putExtra("TOKEN", token)
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()
                        val jsonReader = JsonReader(InputStreamReader(errorBody!!.byteStream()))
                        jsonReader.isLenient = true
                        val errorJson = JsonParser().parse(jsonReader).asJsonObject
                        val errorMessage = errorJson.get("message").asString
                        println("Error: $errorMessage")
                        val intent = Intent(this@ReservationActivity, StatsActivity::class.java)
                        intent.putExtra("TOKEN", token)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<Reservation>, t: Throwable) {
                    println("Error: $t")
                }
            })

    }
}


