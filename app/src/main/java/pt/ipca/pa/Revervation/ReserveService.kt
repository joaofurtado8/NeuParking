package pt.ipca.pa.Revervation

import okhttp3.RequestBody
import pt.ipca.pa.Park.Park
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ReserveService {
    @POST("reservation")
    fun addReservation(@Body reservation: RequestBody, @Header("Authorization") token: String): Call<Reservation>
    @GET("reservationUser")
    suspend fun getPaymentsByUser(@Header("Authorization") token: String):  Response<List<Reservation>>
}//enviar id