package pt.ipca.pa.Payment

import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("id") var id: String? = null,
    var userId: String,
    var reservationId: String,
    var amount: String,
    var date: String
)



