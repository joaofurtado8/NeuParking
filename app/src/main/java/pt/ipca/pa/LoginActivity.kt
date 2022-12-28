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

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login("myemail@hotmail.com", "password", this)
    }
}

fun login(email: String, password: String, context: Context) {
    val userData = mapOf(
        "email" to email,
        "password" to password
    )

    val request = Request.Builder()
        .url("http://192.168.1.214:8080/login/")
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
                val token = response.header("auth-token")
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("TOKEN", token)
                context.startActivity(intent)
            } else {
                // handle error

                val responseJson = response.body?.string()
                if (responseJson != null) {
                    val responseData = responseJson.fromJson<Map<String, Any>>()
                    val message = responseData["msg"]
                    println("Login failed: $message")
                } else {
                    println("Error parsing response body")
                }

            }
        }
    })
}
