package pt.ipca.pa.Payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import pt.ipca.pa.Park.Park
import pt.ipca.pa.Park.ParkService
import pt.ipca.pa.Park.StatsActivity
import pt.ipca.pa.R
import pt.ipca.pa.Revervation.ReserveService
import pt.ipca.pa.SQLite.DataBaseHandler
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListPaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_payment)


        val token = intent.getStringExtra("TOKEN")

        val httpClient = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://smart-api.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val reserveService = retrofit.create(ReserveService::class.java)

        GlobalScope.launch {
            val response = reserveService.getPaymentsByUser(token = "Bearer $token")
            if (response.isSuccessful) {
                println("Reserves received")
                response.body()?.let { reserve ->
                    withContext(Dispatchers.Main) {
                        //listView.adapter = StatsActivity.ParksAdapter(parks)
                       // val db = DataBaseHandler(this@ListPaymentActivity)
                        for (res in reserve) {
                            println("olaaaa: $res")
                        }

                    }
                }
            }
            }
        }
    }

