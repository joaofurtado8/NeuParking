package pt.ipca.pa.Revervation

import pt.ipca.pa.Park.Park
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ReserveService {
    @GET("Reservations")
    fun getUser(@Path("id") id: Int, @Header("Authorization") token: String): Call<Reservation>
}