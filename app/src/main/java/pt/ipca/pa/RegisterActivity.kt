package pt.ipca.pa

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import com.google.gson.Gson
import pt.ipca.pa.Slots.SlotActivity
import pt.ipca.pa.data.User
import pt.ipca.pa.utils.ConstantsUtils


class RegisterActivity : AppCompatActivity() {
    lateinit var  editPlate: EditText
    lateinit var editEmail: EditText
    lateinit var editSenha : EditText
    lateinit var confirmSenha: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editPlate = findViewById(R.id.main_plate_et)
        editEmail= findViewById(R.id.main_email_et)
        editSenha= findViewById(R.id.password)
        confirmSenha= findViewById(R.id.confirm_password)

        findViewById<View>(R.id.register).setOnClickListener {
            val plate = editPlate.text.toString()
            val email = editEmail.text.toString()
            val password = editSenha.text.toString()
            val confirmPassword = confirmSenha.text.toString()

            register(plate, email, password, confirmPassword, this@RegisterActivity)
        }


    }
}
fun Any.toJson(): String = Gson().toJson(this)
inline fun <reified T> String.fromJson(): T = Gson().fromJson(this, T::class.java)


fun register(plate: String, email: String, password: String, confirmpassword: String, context: Context) {
    val userData = mapOf(
        "plate" to plate,
        "email" to email,
        "password" to password,
        "confirmpassword" to confirmpassword
    )

    val request = Request.Builder()
        .url("https://smart-api.onrender.com/register/")
        .post(RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), userData.toJson())
        )
        .build()

    val client = OkHttpClient()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            // handle failure
            println("Request failed: $e")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                // handle success
                val responseJson = response.body?.string()
                if (responseJson != null) {
                    val responseData = responseJson.fromJson<Map<String, Any>>()
                    val message = responseData["msg"]
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    //Toast.makeText(RegisterActivity@this,"Registration successful: $message",Toast.LENGTH_LONG).show()
                } else {
                    println("Error parsing response body")
                }
            } else {
                // handle error

                val responseJson = response.body?.string()
                if (responseJson != null) {
                    val responseData = responseJson.fromJson<Map<String, Any>>()
                    val message = responseData["msg"]
                    println("Registration failed: $message")
                } else {
                    println("Error parsing response body")
                }

            }
        }
    })
}