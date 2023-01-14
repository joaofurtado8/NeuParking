package pt.ipca.pa.Park

import retrofit2.Response

interface StatsView {
    fun onAllParksSuccess(response: Response<List<Park>>)
    fun onAllParksError(string: String)
    fun onParkClick(park: Park)
}