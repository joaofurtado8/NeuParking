package pt.ipca.pa

import android.content.Context
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


class RegisterActivity : AppCompatActivity() {
    lateinit var editEmail: EditText
    lateinit var editSenha : EditText
    lateinit var confirmSenha: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        findViewById<View>(R.id.register).setOnClickListener {
            register("asasdasd","ed@dsadsad.ddd","editSenha","editSenha")
        }

        editEmail=  findViewById(R.id.main_email_et)
        editSenha=  findViewById(R.id.password)
        confirmSenha=  findViewById(R.id.confirm_button)

    }


}
fun Any.toJson(): String = Gson().toJson(this)
inline fun <reified T> String.fromJson(): T = Gson().fromJson(this, T::class.java)


fun register(plate: String, email: String, password: String, confirmpassword: String) {
    val userData = mapOf(
        "plated" to plate,
        "email" to email,
        "password" to password,
        "confirmpassword" to confirmpassword
    )

    val request = Request.Builder()
        .url("http://192.168.1.106:9004/register/")
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