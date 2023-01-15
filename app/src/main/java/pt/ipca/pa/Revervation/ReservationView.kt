package pt.ipca.pa.Revervation

import android.content.Context
import pt.ipca.pa.Park.Park
import retrofit2.Response

interface ReservationView {

//   fun addReservation(reservation: Reservation, token: String, context: Context)
//   fun getReservationsByUser(reservation: Reservation, token: String, context: Context)



   fun onAllReservationsSuccess(response: Response<List<Reservation>>)
   fun onAllReservationsError(string: String)
   fun onReservationClick(reservation: Reservation)
}