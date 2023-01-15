package pt.ipca.pa.Park

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import pt.ipca.pa.PrivateActivity
import pt.ipca.pa.R
import pt.ipca.pa.SQLite.DataBaseHandler
import pt.ipca.pa.controller.StatsController
import pt.ipca.pa.data.User
import pt.ipca.pa.model.StatsModel
import pt.ipca.pa.utils.ConstantsUtils.Companion.TOKEN
import retrofit2.Response

class StatsActivity : StatsView, PrivateActivity() {
    lateinit var listView: ListView
    private val viewModel = StatsModel()
    private val controller = StatsController(viewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        listView = findViewById<ListView>(R.id.list_view)
        val user:User = intent.getSerializableExtra(TOKEN) as User

        controller.bind(this)
        GlobalScope.launch {
            controller.getAllParks(user.token)
        }
    }



    override fun onAllParksSuccess(response: Response<List<Park>>) {
        println("Parks received")
        response.body()?.let { parks ->
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    listView.adapter = ParksAdapter(parks,this@StatsActivity)
                    val db = DataBaseHandler(this@StatsActivity)
//                    for (park in parks) {
//                        db.addPark(park)
//                        println("park added: $park.name")
//                    }
//
//
//                    val dbt: List<Park> = db.getParksList();
//                    for (park in dbt) {
//                        db.addPark(park)
//                        println("lindo: $park.name")
//                    }
                }
            }

        }

    }

    override fun onAllParksError(error: String) {

        Toast.makeText(this@StatsActivity, error, Toast.LENGTH_LONG).show()

    }

    override fun onParkClick(park: Park) {
        Toast.makeText(this@StatsActivity, park.name, Toast.LENGTH_SHORT).show()
    }


    class ParksAdapter(private val parks: List<Park>, var statsView: StatsView) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.park_item, parent, false)
            val park = parks[position]
            view.findViewById<TextView>(R.id.park_name_tv).text = park.name
            view.findViewById<TextView>(R.id.park_free_spots_tv).text =
                park.availableSpots.toString()
            view.setOnClickListener {
                statsView.onParkClick(park)
            }
            return view
        }

        override fun getItem(position: Int) = parks[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = parks.size


    }
}
