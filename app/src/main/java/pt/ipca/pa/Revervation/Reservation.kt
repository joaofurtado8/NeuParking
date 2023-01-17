package pt.ipca.pa.Revervation

import com.google.gson.annotations.SerializedName
import java.util.*


data class Reservation (
    @SerializedName("id") var id: String? = null,
    @SerializedName("amount") var amount: Double? = null,
    var slotId : String,
    val userId : String,
    var startTime: String,
    var endTime: String,
    var day:String
    )


