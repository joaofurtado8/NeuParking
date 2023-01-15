package pt.ipca.pa.controller

import pt.ipca.pa.Park.StatsView
import pt.ipca.pa.model.StatsModel

class StatsController(private val model: StatsModel) {
    private lateinit var view: StatsView

    fun bind(statsView: StatsView) {
        view = statsView
    }

    suspend fun getAllParks(id: String?) {
        val response = model.getAllParks(id)

        if (response.isSuccessful) {
            println("Parks received")
            response.body()?.let { parks ->
                view.onAllParksSuccess(response)

            }
        } else {
            view.onAllParksError(response.errorBody()?.string()!!)
        }
    }


}