package pt.ipca.pa

data class Payment(
    val userID: String,
    val reservationID: String,
    val amount: Float,
    val date: String
)