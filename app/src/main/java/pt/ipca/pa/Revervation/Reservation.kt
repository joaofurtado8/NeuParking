package pt.ipca.pa.Revervation

import com.google.gson.annotations.SerializedName


data class Reservation (
    val userId : String,
    val slotId : String,
    val starTime: String,
    val endTime: String,
    val day: String
    )


