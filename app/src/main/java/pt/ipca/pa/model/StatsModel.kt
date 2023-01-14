package pt.ipca.pa.model

import pt.ipca.pa.Park.Park
import pt.ipca.pa.services.RetrofitAPI
import retrofit2.Response

class StatsModel {
    suspend  fun getAllParks(token: String?): Response<List<Park>> {

        return RetrofitAPI.getParkService.getAllParks(token = "Bearer $token")
    }
}