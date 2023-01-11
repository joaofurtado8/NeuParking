package pt.ipca.pa
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ParkService {
    @GET("/park")
    suspend fun getAllParks(@Header("Authorization") token: String): Response<List<Park>>
}