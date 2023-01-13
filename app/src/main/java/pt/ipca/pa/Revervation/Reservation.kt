package pt.ipca.pa.Revervation

import com.google.gson.annotations.SerializedName
import java.util.Date


data class Reservation (
    val slotId : String,
    val userId : String,
    val startTime: Date,
    val endTime: Date,
    val day: Date
    )


