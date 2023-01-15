package pt.ipca.pa.Slots

import android.content.Intent
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

import pt.ipca.pa.Park.Slot
import pt.ipca.pa.Park.StatsActivity
import pt.ipca.pa.Park.StatsView
import pt.ipca.pa.R
import pt.ipca.pa.Revervation.ReservationActivity
import pt.ipca.pa.SQLite.DataBaseHandler
import pt.ipca.pa.Slot.SlotView
import pt.ipca.pa.controller.SlotController
import pt.ipca.pa.controller.StatsController
import pt.ipca.pa.data.User
import pt.ipca.pa.model.SlotModel
import pt.ipca.pa.model.StatsModel
import pt.ipca.pa.utils.ConstantsUtils
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SlotActivity : SlotView, AppCompatActivity() {
    lateinit var listView: ListView
    private val viewModel = SlotModel()
    private val controller = SlotController(viewModel)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slot)
        listView = findViewById<ListView>(R.id.list_view)
        val user: User = intent.getSerializableExtra(ConstantsUtils.TOKEN) as User

        controller.bind(this)
        GlobalScope.launch {
            controller.getAllSlots(user.token)
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as Slot
            println(item.id)
            val intent = Intent(this@SlotActivity, ReservationActivity::class.java)
            intent.putExtra(ConstantsUtils.TOKEN, user)
            intent.putExtra(ConstantsUtils.SLOT_ID, item.id)
            intent.putExtra(ConstantsUtils.USER_ID, user.userID)
            this@SlotActivity.startActivity(intent)
            runOnUiThread {

                //  val intent = Intent(context, ListPaymentActivity::class.java)

                this@SlotActivity.startActivity(intent)
                Toast.makeText(
                    this@SlotActivity,
                    "You clicked on ${item.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onAllSlotsSuccess(response: Response<List<Slot>>) {
        println("Slots received")
        response.body()?.let { slots ->
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    listView.adapter = SlotsAdapter(slots, this@SlotActivity)
                    /*val db = DataBaseHandler(this@SlotActivity)
                    for (park in parks) {
                        db.addPark(park)
                        println("park added: $park.name")
                    }


                    val dbt: List<Park> = db.getParksList();
                    for (park in dbt) {
                        db.addPark(park)
                        println("lindo: $park.name")
                    }*/
                }
            }

        }

    }

    override fun onAllSlotsError(error: String) {

        Toast.makeText(this@SlotActivity, error, Toast.LENGTH_LONG).show()

    }

    override fun onSlotClick(slot: Slot) {
        Toast.makeText(this@SlotActivity, slot.name, Toast.LENGTH_SHORT).show()
    }
}

class SlotsAdapter(private val slots: List<Slot>,  var slotsView: SlotView) : BaseAdapter() {
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

