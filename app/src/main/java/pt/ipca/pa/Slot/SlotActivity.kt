package pt.ipca.pa.Slots

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
import pt.ipca.pa.Adapters.SlotsAdapter

import pt.ipca.pa.Park.Slot
import pt.ipca.pa.R
import pt.ipca.pa.Revervation.ReservationActivity
import pt.ipca.pa.Slot.SlotView
import pt.ipca.pa.controller.SlotController
import pt.ipca.pa.data.User
import pt.ipca.pa.model.SlotModel
import pt.ipca.pa.utils.ConstantsUtils
import retrofit2.Response

class SlotActivity : SlotView, AppCompatActivity() {
    lateinit var listView: ListView
    private val viewModel = SlotModel()
    private val controller = SlotController(viewModel)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slot)
        listView = findViewById<ListView>(R.id.list_view)
        val user: User = intent.getSerializableExtra(ConstantsUtils.TOKEN) as User
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        controller.bind(this)
        GlobalScope.launch {
            controller.getAllSlots(user.token)
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as Slot

            if (item.ocupied.toLowerCase() == "false") {
                val intent = Intent(this@SlotActivity, ReservationActivity::class.java)
                intent.putExtra(ConstantsUtils.TOKEN, user)
                intent.putExtra(ConstantsUtils.SLOT_ID, item.id)
                intent.putExtra(ConstantsUtils.USER_ID, user.userID)

                this@SlotActivity.startActivity(intent)

                Toast.makeText(this@SlotActivity, "You clicked on ${item.id}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@SlotActivity, "Slot is occupied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true;
    }

    override fun onAllSlotsSuccess(response: Response<List<Slot>>) {
        println("Slots received")
        response.body()?.let { slots ->
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    listView.adapter = SlotsAdapter(slots, this@SlotActivity)

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


