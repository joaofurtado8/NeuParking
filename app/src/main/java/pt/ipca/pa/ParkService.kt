package pt.ipca.pa
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ParkService {
    @GET("/parks")
    suspend fun getAllParks(): Response<List<Park>>
}