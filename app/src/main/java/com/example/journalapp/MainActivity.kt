package com.example.journalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var loginBtn : Button
    private lateinit var create_acc_btn : Button
    private lateinit var email : EditText
    private lateinit var password : EditText

    private lateinit var v : View.OnClickListener

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: AuthStateListener
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginBtn  = findViewById(R.id.email_signin)
        create_acc_btn  = findViewById(R.id.create_account)
        email  = findViewById(R.id.email)
        password  = findViewById(R.id.password)

        create_acc_btn.setOnClickListener{ v ->
            var emailId = email.text
            var i : Intent = Intent(this , SignUpActivity::class.java)
//            i.putExtra("email",emailId)
            startActivity(i)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        loginBtn.setOnClickListener{ v ->
            login_user(email.text.toString() , password.text.toString())
        }
    }
    private fun login_user(email: String, password: String) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val user: FirebaseUser = firebaseAuth.currentUser!!
                    val intent = Intent(this, JournalListActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    // Handle login failure (e.g., show a toast message)
                    Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle case where email or password is empty (e.g., show a toast message)
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
        }
    }

}