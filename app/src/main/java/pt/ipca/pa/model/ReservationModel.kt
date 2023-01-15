package pt.ipca.pa.model

import pt.ipca.pa.Park.Park
import pt.ipca.pa.Revervation.Reservation
import pt.ipca.pa.services.RetrofitAPI
import retrofit2.Response

class ReservationModel {

    suspend  fun getReservationsByUser(token: String?): Response<List<Reservation>> {

        return RetrofitAPI.getReservationService.getPaymentsByUser(token = "Bearer $token")
    }
}