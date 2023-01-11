package pt.ipca.pa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import android.content.Context

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        GlobalScope.launch {
            val response = parkService.getAllParks(token = "Bearer $token")
            if (response.isSuccessful) {
                println("Parks received")
                response.body()?.let { parks ->
                    withContext(Dispatchers.Main) {
                        listView.adapter = ParksAdapter(parks)
                    }
                }
            }else{
                val error = response.errorBody()?.string()
                println(error)
                withContext(Dispatchers.Main){
                    Toast.makeText(this@StatsActivity, error, Toast.LENGTH_LONG).show()
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
            view.findViewById<TextView>(R.id.park_free_spots_tv).text = park.availableSpots.toString()
            return view
        }

        override fun getItem(position: Int) = parks[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = parks.size
    }
}
