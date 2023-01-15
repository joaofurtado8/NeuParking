package pt.ipca.pa.Slot

import pt.ipca.pa.Park.Park
import pt.ipca.pa.Park.Slot
import retrofit2.Response

interface SlotView {
    fun onAllSlotsSuccess(response: Response<List<Slot>>)
    fun onAllSlotsError(string: String)
    fun onSlotClick(slot: Slot)
}