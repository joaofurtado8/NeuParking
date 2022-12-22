package pt.ipca.pa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import com.google.gson.Gson


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        register("asasdasd","olsaa@gmail.vtf",":(",":(")

println("olaaaa")


    }

}
fun Any.toJson(): String = Gson().toJson(this)
inline fun <reified T> String.fromJson(): T = Gson().fromJson(this, T::class.java)


fun register(plate: String, email: String, password: String, confirmpassword: String) {
    val userData = mapOf(
        "plate" to plate,
        "email" to email,
        "password" to password,
        "confirmpassword" to confirmpassword
    )

    val request = Request.Builder()
        .url("http://localhost:8080/register")
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
                    println("Registration successful: $message")
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