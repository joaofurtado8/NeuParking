package pt.ipca.pa.Slots

import pt.ipca.pa.Park.Slot
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface SlotService {
    @GET("slot")
    suspend fun getAllSlots(@Header("Authorization") token: String): Response<List<Slot>>
}