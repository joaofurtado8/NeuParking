package pt.ipca.pa.Park
import pt.ipca.pa.Park.Park
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ParkService {
    @POST("reservations")
    suspend fun getAllParks(@Header("Authorization") token: String): Response<List<Park>>
}