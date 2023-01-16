package pt.ipca.pa.Payment

import pt.ipca.pa.Revervation.Reservation
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PaymentService {
    @POST("payment")
    fun addPayment(@Body payment: Payment, @Header("Authorization") token: String): Call<Payment>
    }