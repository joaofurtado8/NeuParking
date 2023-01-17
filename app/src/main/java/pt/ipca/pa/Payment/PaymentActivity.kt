package pt.ipca.pa.Payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import pt.ipca.pa.R
import pt.ipca.pa.Revervation.Reservation
import pt.ipca.pa.data.User
import pt.ipca.pa.utils.ConstantsUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStreamReader
import java.util.*

class PaymentActivity : AppCompatActivity() {

    lateinit var paymentAmount : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val user: User = intent.getSerializableExtra(ConstantsUtils.TOKEN) as User
        val reservationId = intent.getStringExtra("RES")
        val userId = intent.getStringExtra(ConstantsUtils.USER_ID)
        val btn_pay = findViewById<Button>(R.id.btn_pay)
        val amount = intent.getDoubleExtra(ConstantsUtils.AMOUNT,0.0)
        paymentAmount = findViewById<TextView>(R.id.textView3)
        paymentAmount.text = amount.toString() +"€"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val payment = Payment(
            userId = userId.toString(),
            reservationId = reservationId.toString(),
            amount = amount.toString(),
            date = currentDate.toString()
        )
        btn_pay.setOnClickListener {
            addPayment(payment, user.token.toString(), this@PaymentActivity)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true;
    }

    fun addPayment(payment: Payment, token: String, context: Context) {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://smart-api.onrender.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val user: User = intent.getSerializableExtra(ConstantsUtils.TOKEN) as User
        val paymentService = retrofit.create(PaymentService::class.java)
        val json = Gson().toJson(payment)
        val requestBody = RequestBody.create("application/json".toMediaType(), json)
        Log.d("requestBody", json)
        paymentService.addPayment(payment, "Bearer $token")
            .enqueue(object : Callback<Payment> {
                override fun onResponse(call: Call<Payment>, response: Response<Payment>) {

                    if (response.isSuccessful) {
                        println("Payment added successfully")
                        val intent = Intent(this@PaymentActivity, ListPaymentActivity::class.java)
                        intent.putExtra(ConstantsUtils.TOKEN, user)
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()
                        val jsonReader = JsonReader(InputStreamReader(errorBody!!.byteStream()))
                        jsonReader.isLenient = true
                        val errorJson = JsonParser().parse(jsonReader).asJsonObject
                        val errorMessage = errorJson.get("message").asString
                        println("Error: $errorMessage")
                        val intent = Intent(this@PaymentActivity, ListPaymentActivity::class.java)
                        intent.putExtra("TOKEN", token)
                        startActivity(intent)
                        //intent
                    }
                }

                override fun onFailure(call: Call<Payment>, t: Throwable) {
                    println("Error: $t")
                }
            })

    }

   
}



