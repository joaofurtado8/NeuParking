package pt.ipca.pa.Payment

data class Payment(
    var userId: String,
    val reservationId: String,
    val amount: String,
    val date: String
)



