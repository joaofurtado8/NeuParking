package pt.ipca.pa.Payment

data class Payment(
    var name: String,
    val location: String,
    val description: String,
    val availableSpots: Number,
    val imageUrl: String
)

