package pt.ipca.pa


data class Reservation (
    val userId : String,
    val slotId : String,
    val starTime: String,
    val endTime: String,
    val day: String
    )
