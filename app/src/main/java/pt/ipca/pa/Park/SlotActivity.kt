package pt.ipca.pa.Slots

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipca.pa.Park.Park
import pt.ipca.pa.Park.ParkService
import pt.ipca.pa.Park.ParksAdapter
import pt.ipca.pa.Park.Slot
import pt.ipca.pa.R
import pt.ipca.pa.SQLite.DataBaseHandler
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SlotActivity : AppCompatActivity() {
    lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slot)
        listView = findViewById<ListView>(R.id.list_view)
        val token = intent.getStringExtra("TOKEN")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://smart-api.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val slotService = retrofit.create(SlotService::class.java)

        GlobalScope.launch {
            val response = slotService.getAllSlots(token = "Bearer $token")
            if (response.isSuccessful) {
                println("Slots received")
                response.body()?.let { slots ->
                    withContext(Dispatchers.Main) {
                        listView.adapter = SlotsAdapter(slots)

                    }
                }
            } else {
                val error = response.errorBody()?.string()
                println(error)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SlotActivity, error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

class SlotsAdapter(private val slots: List<Slot>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.slot_item, parent, false)
        val slot = slots[position]
        view.findViewById<TextView>(R.id.slot_name_tv).text = slot.name
        if (slot.ocupied == "False"){
            view.findViewById<TextView>(R.id.ocupied_spots_tv).text = "Free"
        }
        else {
            view.findViewById<TextView>(R.id.ocupied_spots_tv).text = "Occupied"
        }
        return view
    }

    override fun getItem(position: Int) = slots[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = slots.size


}