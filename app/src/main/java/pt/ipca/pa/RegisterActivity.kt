package pt.ipca.pa

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import com.google.gson.Gson

private fun isPlateValid(plate: String): Boolean {
    val pattern = Regex("^[A-Za-z\\d]{2}-[A-Za-z\\d]{2}-[A-Za-z\\d]{2}$")
    return pattern.matches(plate)
}

private fun isEmailValid(email: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

private fun isFieldEmpty(text: String): Boolean {
    return text.trim().isEmpty()
}

class RegisterActivity : AppCompatActivity() {
    lateinit var  editPlate: EditText
    lateinit var editEmail: EditText
    lateinit var editSenha : EditText
    lateinit var confirmSenha: EditText
    lateinit var btn_login: Button
//ds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editPlate = findViewById(R.id.main_plate_et)
        editEmail= findViewById(R.id.main_email_et)
        editSenha= findViewById(R.id.password)
        confirmSenha= findViewById(R.id.confirm_password)
        btn_login = findViewById(R.id.login)

        findViewById<View>(R.id.register).setOnClickListener {
            val plate = editPlate.text.toString()
            val email = editEmail.text.toString()
            val password = editSenha.text.toString()
            val confirmPassword = confirmSenha.text.toString()

            if (isFieldEmpty(plate) || isFieldEmpty(email) || isFieldEmpty(password) || isFieldEmpty(confirmPassword)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (!isPlateValid(plate)) {
                Toast.makeText(this, "Invalid license plate format, must be like '00-00-00'", Toast.LENGTH_SHORT).show()
            } else if (!isEmailValid(email)) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                register(plate, email, password, confirmPassword, this@RegisterActivity)
            }
        }
        btn_login.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)

            this@RegisterActivity.startActivity(intent)
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