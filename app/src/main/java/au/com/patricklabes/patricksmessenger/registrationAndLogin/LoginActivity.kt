package au.com.patricklabes.patricksmessenger.registrationAndLogin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import au.com.patricklabes.patricksmessenger.messages.LatestMessagesActivity
import au.com.patricklabes.patricksmessenger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)


        login_button_login_view.setOnClickListener {
            login()
        }


        backtoreg_textView_loginscreen.setOnClickListener {
            finish()
        }


    }

    private fun login() {

        val email = email_editText_login.text.toString()
        val password = password_editText_password_login_view.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter an Email and Password", Toast.LENGTH_SHORT).show()
            Log.d("loginActivity", "failed Login")
            return
        }

        Log.d("loginActivity", "Attempt login email/ password: $email / $password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this, "Login was successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)


                }
                .addOnFailureListener {

                    Toast.makeText(this, "Failed to login user ${it.message}", Toast.LENGTH_SHORT).show()

                }
    }


}