package com.example.journalapp

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class SignUpActivity : AppCompatActivity() {
    private lateinit var password_create : EditText
    private lateinit var email_create : EditText
    private lateinit var username_create :EditText

    private lateinit var create_btn : Button

    private lateinit var fireBaseAuth : FirebaseAuth
    private lateinit var authStateListener : FirebaseAuth.AuthStateListener
    private lateinit var currentUser : FirebaseUser

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        password_create  = findViewById(R.id.password_create)
        email_create = findViewById(R.id.email_create)

        username_create = findViewById(R.id.username_create_ET)

        create_btn = findViewById(R.id.acc_signUp_btn)


        fireBaseAuth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener() {
            currentUser = fireBaseAuth.currentUser!!

            if (currentUser!=null){

            }
            else{

            }
        }

        create_btn.setOnClickListener{
            val email = email_create.text.toString()
            val password = password_create.text.toString()
            val username = username_create.text.toString()

            if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()){
                if (isValidEmail(email)){
                    createUserAccount(email , password , username)
                }
                else {
                    Toast.makeText(this@SignUpActivity, "Invalid email format", Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(this@SignUpActivity, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUserAccount(email: String, password: String, userName: String) {
        fireBaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = fireBaseAuth.currentUser
                val userMap = HashMap<String, String>()
                userMap["username"] = userName

                if (user != null) {
                    collectionReference.document(user.uid).set(userMap).addOnSuccessListener {
                        Toast.makeText(this, "Account created successfully with email id $email", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to create account: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(this, "Failed to create account: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}