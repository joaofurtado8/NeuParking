package pt.ipca.pa.Payment

interface PaymentService {
        @GET("payments")
        suspend fun getAllParks(@Header("Authorization") token: String): Response<List<Park>>
    }