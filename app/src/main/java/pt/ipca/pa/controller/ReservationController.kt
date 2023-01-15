package pt.ipca.pa.controller

import pt.ipca.pa.Payment.ListPaymentActivity
import pt.ipca.pa.Revervation.Reservation
import pt.ipca.pa.Revervation.ReservationView
import pt.ipca.pa.model.ReservationModel
import pt.ipca.pa.model.StatsModel

class ReservationController(private val model: ReservationModel){
    private lateinit var view: ReservationView

    fun bind(reservationView: ReservationView) {
        view = reservationView
    }
    suspend fun getReservationsByUser(id: String?, userId: String?) {
        val response = model.getReservationsByUser(id, userId)

        if (response.isSuccessful) {
            println("Reservations Receives")
            response.body()?.let { reservations ->
                view.onAllReservationsSuccess(response)

            }
        } else {
            view.onAllReservationsError(response.errorBody()?.string()!!)
        }
    }
}