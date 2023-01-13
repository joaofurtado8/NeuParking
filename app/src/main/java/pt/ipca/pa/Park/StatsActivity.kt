package pt.ipca.pa.Park

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.OkHttpClient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipca.pa.R
import pt.ipca.pa.SQLite.DataBaseHandler
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StatsActivity : AppCompatActivity() {
    lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        listView = findViewById<ListView>(R.id.list_view)
        val token = intent.getStringExtra("TOKEN")

        val httpClient = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://smart-api.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val parkService = retrofit.create(ParkService::class.java)
        val btn = findViewById<Button>(R.id.btn)

        btn.setOnClickListener {
            Toast.makeText(
                this@StatsActivity,
                "You clicked on me",
                Toast.LENGTH_SHORT
            ).show()
        }

        GlobalScope.launch {
            val response = parkService.getAllParks(token = "Bearer $token")
            if (response.isSuccessful) {
                println("Parks received")
                response.body()?.let { parks ->
                    withContext(Dispatchers.Main) {
                        listView.adapter = ParksAdapter(parks)
                        val db = DataBaseHandler(this@StatsActivity)
                        for (park in parks) {
                            db.addPark(park)
                            println("park added: $park.name")
                        }
                    }
                }
            } else {
                val error = response.errorBody()?.string()
                println(error)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@StatsActivity, error, Toast.LENGTH_LONG).show()
                }
            }
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as Park
            println(item.name)
            runOnUiThread {
                Toast.makeText(
                    this@StatsActivity,
                    "You clicked on ${item.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }
}
class ParksAdapter(private val parks: List<Park>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.park_item, parent, false)
        val park = parks[position]
        view.findViewById<TextView>(R.id.park_name_tv).text = park.name
        view.findViewById<TextView>(R.id.park_free_spots_tv).text =
            park.availableSpots.toString()
        return view
    }

    override fun getItem(position: Int) = parks[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = parks.size


}

