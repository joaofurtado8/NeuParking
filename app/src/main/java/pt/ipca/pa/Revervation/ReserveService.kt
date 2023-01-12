package pt.ipca.pa.Revervation

import pt.ipca.pa.Park.Park
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ReserveService {
    @POST("reservation")
    fun addReservation(@Body reservation: Reservation, @Header("Authorization") token: String): Call<Reservation>
}