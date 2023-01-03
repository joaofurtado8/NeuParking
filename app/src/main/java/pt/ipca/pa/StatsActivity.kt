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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.Gson

class StatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getAllParks(
            callback = { parks ->
                val adapter = ParkAdapter(parks)
                recyclerView.adapter = adapter
            },
            errorCallback = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }
}

class ParkAdapter(private val parks: List<Park>) : RecyclerView.Adapter<ParkAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parkNameTextView: TextView = itemView.findViewById(R.id.park_name_text_view)
        val parkLocationTextView: TextView = itemView.findViewById(R.id.park_location_text_view)
        val parkFreeSpotsTextView: TextView = itemView.findViewById(R.id.park_free_spots_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.park_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val park = parks[position]
        holder.parkNameTextView.text = park.name
        holder.parkLocationTextView.text = park.location
        holder.parkFreeSpotsTextView.text = park.freeSpots.toString()
    }

    override fun getItemCount(): Int {
        return parks.size
    }
}

class ParkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val parkNameTextView: TextView = itemView.findViewById(R.id.park_name_text_view)
    val parkLocationTextView: TextView = itemView.findViewById(R.id.park_location_text_view)
    val parkFreeSpotsTextView: TextView = itemView.findViewById(R.id.park_free_spots_text_view)
}

data class ParkListWrapper(val parks: List<Park>)


fun getAllParks(callback: (List<Park>) -> Unit, errorCallback: (String) -> Unit) {
    val url = "https://smart-api.onrender.com/park"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            errorCallback(e.message ?: "Unknown error")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body?.string()
            val parkListWrapper: ParkListWrapper = Gson().fromJson(responseString, ParkListWrapper::class.java)
            val parks = parkListWrapper.parks
            callback(parks)
        }

    })
}


