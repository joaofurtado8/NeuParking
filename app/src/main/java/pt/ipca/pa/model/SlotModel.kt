package pt.ipca.pa.model

import pt.ipca.pa.Park.Park
import pt.ipca.pa.Park.Slot
import pt.ipca.pa.services.RetrofitAPI
import retrofit2.Response

class SlotModel {
    suspend  fun getAllSlots(token: String?): Response<List<Slot>> {

        return RetrofitAPI.getSlotService.getAllSlots(token = "Bearer $token")
    }
}