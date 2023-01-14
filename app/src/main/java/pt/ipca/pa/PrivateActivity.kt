package pt.ipca.pa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

 abstract class PrivateActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

     override fun onDestroy() {
         super.onDestroy()

     }
 }