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
import pt.ipca.pa.data.User
import pt.ipca.pa.utils.ConstantsUtils
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
        val user: User = intent.getSerializableExtra(ConstantsUtils.TOKEN) as User
        val slotId = intent.getStringExtra(ConstantsUtils.SLOT_ID)
        val userId = intent.getStringExtra(ConstantsUtils.USER_ID)

        editStartTime = findViewById(R.id.start_et)
        editEndTime = findViewById(R.id.end_et)
        editDay = findViewById(R.id.day_et)
        reservationBtn = findViewById(R.id.reservation_bt)

        reservationBtn.setOnClickListener {
            if (editStartTime.text.toString().isEmpty() || editEndTime.text.toString().isEmpty() || editDay.text.toString().isEmpty()){
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!editStartTime.text.toString().matches(Regex("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$"))){
                Toast.makeText(this, "Invalid start time format!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!editEndTime.text.toString().matches(Regex("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$"))){
                Toast.makeText(this, "Invalid end time format!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!editDay.text.toString().matches(Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/(19|20)[0-9][0-9]$"))){
                Toast.makeText(this, "Invalid day format!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val reservation = Reservation(
                slotId= slotId.toString(),
                userId = userId.toString(),
                startTime = editStartTime.text.toString()+":00",
                endTime = editEndTime.text.toString()+":00",
                day = editDay.text.toString()
            )
            println(userId.toString())
            addReservation(reservation, user.token.toString()!!, this@ReservationActivity)
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
        val user: User = intent.getSerializableExtra(ConstantsUtils.TOKEN) as User
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
                        intent.putExtra(ConstantsUtils.TOKEN, user)
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
