package pt.ipca.pa.Park

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipca.pa.Adapters.ParksAdapter
import pt.ipca.pa.LoginActivity
import pt.ipca.pa.Payment.ListPaymentActivity
import pt.ipca.pa.PrivateActivity
import pt.ipca.pa.R
import pt.ipca.pa.SQLite.DataBaseHandlerPark
import pt.ipca.pa.Slots.SlotActivity
import pt.ipca.pa.controller.StatsController
import pt.ipca.pa.data.User
import pt.ipca.pa.model.StatsModel
import pt.ipca.pa.utils.ConstantsUtils
import pt.ipca.pa.utils.ConstantsUtils.Companion.TOKEN
import retrofit2.Response

class StatsActivity : StatsView, PrivateActivity() {
    lateinit var listView: ListView
    private val viewModel = StatsModel()
    private val controller = StatsController(viewModel)
    lateinit var btn_slot_page: Button
    lateinit var btn_payment_page: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        listView = findViewById<ListView>(R.id.list_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_slot_page = findViewById(R.id.slot_page)
        btn_payment_page = findViewById(R.id.payment_page)

        if (!isConnected(this@StatsActivity)) {
            val db = DataBaseHandlerPark(this@StatsActivity)
            val parks = db.getParksList()
            listView.adapter = ParksAdapter(parks, this@StatsActivity, this@StatsActivity)
            btn_payment_page.setOnClickListener {
                val intent = Intent(this@StatsActivity, ListPaymentActivity::class.java)
                this@StatsActivity.startActivity(intent)
            }
            btn_slot_page.setOnClickListener {
                Toast.makeText(this@StatsActivity, "No Connection", Toast.LENGTH_LONG).show()
            }
            return
        }else{
            val user: User = intent.getSerializableExtra(TOKEN) as User

            btn_slot_page.setOnClickListener {
                val intent = Intent(this@StatsActivity, SlotActivity::class.java)
                intent.putExtra(ConstantsUtils.TOKEN, user)
                intent.putExtra(ConstantsUtils.USER_ID, user.userID)
                this@StatsActivity.startActivity(intent)
            }

            btn_payment_page.setOnClickListener {
                val intent = Intent(this@StatsActivity, ListPaymentActivity::class.java)
                intent.putExtra(ConstantsUtils.TOKEN, user)
                intent.putExtra(ConstantsUtils.USER_ID, user.userID)
                this@StatsActivity.startActivity(intent)
            }

            controller.bind(this)
            GlobalScope.launch {
                controller.getAllParks(user.token)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        addDialogLogout()
        return true;
    }

    private fun addDialogLogout() {
        val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
        builder1.setMessage("Are you sure?")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Yes"
        ) { dialog, id ->
            // Clear user's credentials
            val sharedPref = getSharedPreferences("user_credentials", Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                clear()
                apply()
            }
            // Redirect to login page
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        builder1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()
    }



    override fun onAllParksSuccess(response: Response<List<Park>>) {
        println("Parks received")
        response.body()?.let { parks ->
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    listView.adapter = ParksAdapter(parks, this@StatsActivity, this@StatsActivity)
                    val db = DataBaseHandlerPark(this@StatsActivity)
                    for (park in parks) {
                        db.addPark(park)
                    }
                    val dbt: List<Park> = db.getParksList();
                    for (dbt_ in dbt) {
                        println("teste $dbt_")
                    }
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
}

fun isConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}