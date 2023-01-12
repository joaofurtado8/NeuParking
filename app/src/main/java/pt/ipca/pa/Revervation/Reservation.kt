package pt.ipca.pa.Revervation

import com.google.gson.annotations.SerializedName


data class Reservation (
    val slotId : String,
    val userId : String,
    val starTime: String,
    val endTime: String,
    val day: String
    )


