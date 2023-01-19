package pt.ipca.pa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.auth0.jwt.JWT
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import pt.ipca.pa.Park.StatsActivity
import pt.ipca.pa.data.User
import pt.ipca.pa.utils.ConstantsUtils.Companion.TOKEN
import java.io.IOException


private fun isEmailValid(email: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

private fun isFieldEmpty(text: String): Boolean {
    return text.trim().isEmpty()
}

class LoginActivity : AppCompatActivity() {
    lateinit var editEmail: EditText
    lateinit var editPassword: EditText
    lateinit var btn_register: Button
    lateinit var mainView: ConstraintLayout
    lateinit var loading: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editEmail = findViewById(R.id.main_email_et)
        editPassword = findViewById(R.id.main_password_opt)
        btn_register = findViewById(R.id.register)
        mainView = findViewById(R.id.const_main_view)
        loading = findViewById(R.id.pBar)

        val preferences = getSharedPreferences("user_credentials", MODE_PRIVATE)
        val email = preferences.getString("email", "")
        val password = preferences.getString("password", "")
        if (email != null) {
            if (password != null) {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if (password != null) {
                        login(email, password, this)
                    }
                }
            }
        }

        addClickListener()

    }

    /** Add view clicks */
    private fun addClickListener() {
        findViewById<View>(R.id.login).setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()



            if (isFieldEmpty(email) || isFieldEmpty(password)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (!isEmailValid(email)) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            } else {
                login(email, password, this@LoginActivity)
            }
        }

        btn_register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)

            this@LoginActivity.startActivity(intent)
        }
    }

    private fun showLoading() {
        runOnUiThread( Runnable() {

        mainView.visibility = View.GONE
        loading.visibility = View.VISIBLE
        })
    }

    private fun hideLoading() {
        runOnUiThread( Runnable() {
        mainView.visibility = View.VISIBLE
        loading.visibility = View.GONE
        })
    }

    fun login(email: String, password: String, context: Context) {
        showLoading()
        val userData = mapOf(
            "email" to email, "password" to password
        )

        val request = Request.Builder().url("https://smart-api.onrender.com/login/").post(
                RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(), userData.toJson()
                )
            ).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // handle failure
                hideLoading()
                println("Request failed: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                hideLoading()
                if (response.isSuccessful) {
                    // handle success
                    val token = response.header("auth-token")

                    val decodedJWT = JWT.decode(token)

                    val subject = decodedJWT.claims.entries
                    var userID: String = ""
                    subject.forEach {

                        if (it.key == "__id") {
                            userID = it.value.toString()
                        }

                    }
                    val editor = getSharedPreferences("user_credentials", MODE_PRIVATE).edit()
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.apply()
                    val intent = Intent(context, StatsActivity::class.java)
                    intent.putExtra(TOKEN, User(token, userID))
                    //  val intent = Intent(context, ListPaymentActivity::class.java)

                    context.startActivity(intent)
                } else {
                    // handle error
                    val responseJson = response.body?.string()
                    if (responseJson != null) {
                        val responseData = responseJson.fromJson<Map<String, Any>>()
                        val message = responseData["msg"]
                        runOnUiThread{
                            Toast.makeText(context, "Login Failed: $message", Toast.LENGTH_SHORT).show()
                        }

                        println("Login failed: $message")
                    } else {
                        println("Error parsing response body")
                    }
                }
            }
        })
    }

    private fun addErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Service error!")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}