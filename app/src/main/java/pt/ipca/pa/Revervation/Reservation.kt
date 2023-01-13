package pt.ipca.pa.Revervation

import com.google.gson.annotations.SerializedName


data class Reservation (
    var slotId : String,
    val userId : String,
    val startTime: String,
    val endTime: String,
    val day: String
    )


