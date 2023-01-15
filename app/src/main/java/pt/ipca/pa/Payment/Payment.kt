package pt.ipca.pa.Payment

data class Payment(
    val id: String,
    var userId: String,
    val reservationId: String,
    val amount: String,
    val date: String
)



