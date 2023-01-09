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
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.Gson

class StatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        val recView = findViewById<RecyclerView>(R.id.recycler_view)
        val jwtToken = intent.getStringExtra("TOKEN")
        getParks(this, recView, jwtToken!!)

    }
}

fun getParks(activity: StatsActivity, recView: RecyclerView, jwtToken: String) {

    val parks = mutableListOf<Park>()
    // Make an HTTP GET request to your API to retrieve the list of park objects
    val request = Request.Builder()
        .url("https://smart-api.onrender.com/parks")
        .header("Authorization", "Bearer $jwtToken")
        .get()
        .build()



    val client = OkHttpClient()
    client.newCall(request).enqueue(object: Callback {
        override fun onResponse(call: Call, response: Response) {
            // Parse the JSON response
            val responseString = response.body!!.string()
            parks.addAll(Gson().fromJson(responseString, Array<Park>::class.java))

            // Update the ListView on the main thread
            activity.runOnUiThread() {

                recView.adapter = MyAdapterRec(parks)
                val linearLayoutManager = LinearLayoutManager(activity)
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                recView.layoutManager = linearLayoutManager

            }
        }

        override fun onFailure(call: Call, e: IOException) {
            // Handle failure
            activity.runOnUiThread {
                Toast.makeText(activity, "Failed to retrieve parks", Toast.LENGTH_SHORT).show()
            }
        }
    })
}

class MyAdapterRecViewHolder(inflater: LayoutInflater, val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.park_item, parent, false)) {
    private var tv: TextView? = itemView.findViewById(R.id.park_name_text_view)
    private var tv1: TextView? = itemView.findViewById(R.id.park_free_spots_text_view)
    fun bindData(text: String, colorResource: Int) {
        tv?.text = text
        tv1?.text = text
        itemView.setOnClickListener {
            Toast.makeText(parent.context,text,Toast.LENGTH_LONG).show()
        }
    }
}

class MyAdapterRec(private val mList: List<Park>) : RecyclerView.Adapter<MyAdapterRecViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapterRecViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyAdapterRecViewHolder(inflater, parent)
    }
    override fun onBindViewHolder(holder: MyAdapterRecViewHolder, position: Int) {
        val color = when(position % 2) {
            0 -> android.R.color.holo_red_dark
            1 -> android.R.color.holo_blue_dark
            else -> android.R.color.holo_orange_dark
        }
        val text = mList.get(position).name
        val text1 = mList.get(position).freeSpots

        holder.bindData(text + "\n" + text1.toString(),color)
    }
    override fun getItemCount(): Int {
        return mList.size
    }
}