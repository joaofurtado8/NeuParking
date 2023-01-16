package pt.ipca.pa.controller

import pt.ipca.pa.Park.StatsView
import pt.ipca.pa.Slot.SlotView
import pt.ipca.pa.model.SlotModel
import pt.ipca.pa.model.StatsModel

class SlotController(private val model: SlotModel) {
    private lateinit var view: SlotView

    fun bind(slotView: SlotView) {
        view = slotView
    }

    suspend fun getAllSlots(id: String?) {
        val response = model.getAllSlots(id)

        if (response.isSuccessful) {
            println("Slots received")
            response.body()?.let { slots ->
                view.onAllSlotsSuccess(response)

            }
        } else {
            view.onAllSlotsError(response.errorBody()?.string()!!)
        }
    }

}