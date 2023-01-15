package pt.ipca.pa.services

import pt.ipca.pa.Park.ParkService
import pt.ipca.pa.Revervation.ReserveService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAPI {

    private const val BASE_URL = "https://smart-api.onrender.com"


    private fun retrofit(): Retrofit {
        return  Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getParkService: ParkService by lazy {
        retrofit().create(ParkService::class.java)
    }

    val getReservationService: ReserveService by lazy {
        retrofit().create(ReserveService::class.java)
    }

}