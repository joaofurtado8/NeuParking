package pt.ipca.pa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.auth0.jwt.JWT
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import pt.ipca.pa.Park.StatsActivity
import pt.ipca.pa.Payment.ListPaymentActivity
import pt.ipca.pa.data.User
import pt.ipca.pa.utils.ConstantsUtils.Companion.TOKEN
import java.io.IOException


class LoginActivity : AppCompatActivity() {
    lateinit var editEmail: EditText
    lateinit var editPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editEmail = findViewById(R.id.main_email_et)
        editPassword = findViewById(R.id.main_password_opt)

        findViewById<View>(R.id.login).setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            login(email, password, this@LoginActivity)
        }
    }
}

fun login(email: String, password: String, context: Context) {
    val userData = mapOf(
        "email" to email,
        "password" to password
    )

    val request = Request.Builder()
        .url("https://smart-api.onrender.com/login/")
        .post(
            RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                userData.toJson()
            )
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

                val decodedJWT = JWT.decode(token)

                val subject = decodedJWT.claims.entries
                var userID:String =""
                subject.forEach {

                    if (it.key == "__id") {
                        userID= it.value.toString()
                    }

                }
                val intent = Intent(context, ListPaymentActivity::class.java)
                intent.putExtra(TOKEN, User(token, userID))
                //  val intent = Intent(context, ListPaymentActivity::class.java)

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