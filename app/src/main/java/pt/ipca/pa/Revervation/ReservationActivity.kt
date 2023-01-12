package pt.ipca.pa.Revervation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import pt.ipca.pa.R
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




        fun addUser(userData: ReserveService, onResult: (ReserveService?) -> Unit) {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                        return chain.proceed(newRequest)
                    }
                }).build()



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