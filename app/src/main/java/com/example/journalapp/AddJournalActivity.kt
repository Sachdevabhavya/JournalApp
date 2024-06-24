package com.example.journalapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.journalapp.database.Journal
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class AddJournalActivity : AppCompatActivity() {
    //Widgets
    private lateinit var saveButton: Button
    private lateinit var addPhotoBtn: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var titleEditText: EditText
    private lateinit var thoughtsEditText: EditText
    private lateinit var imageView: ImageView

    // Firebase (Firestore)
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Journal")

    // Firebase (Storage)
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("journal_images")

    // Firebase Auth : UserId and UserName
    private lateinit var currentUserId: String
    private lateinit var currentUserName: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: FirebaseUser

    // Activity Result Launcher for selecting an image
    private var mTakePhoto: ActivityResultLauncher<String>? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_journal)

        progressBar = findViewById(R.id.post_progressBar)
        titleEditText = findViewById(R.id.post_title_et)
        thoughtsEditText = findViewById(R.id.post_description_et)
        imageView = findViewById(R.id.post_imageView)
        saveButton = findViewById(R.id.post_save_journal_button)
        addPhotoBtn = findViewById(R.id.postCameraButton)

        progressBar.visibility = View.INVISIBLE

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!

        currentUserId = user.uid
        currentUserName = user.displayName ?: ""

        saveButton.setOnClickListener {
            saveJournal()
        }

        mTakePhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            // Showing the image
            result?.let {
                imageView.setImageURI(result)

                // Get the image URI
                imageUri = result
            }
        }

        addPhotoBtn.setOnClickListener {
            mTakePhoto?.launch("image/*")
        }
    }

    private fun saveJournal() {
        val title: String = titleEditText.text.toString().trim()
        val thoughts: String = thoughtsEditText.text.toString().trim()

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(thoughts) || imageUri == null) {
            Toast.makeText(this@AddJournalActivity, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        val filePath: StorageReference = storageReference.child("my_image_${Timestamp.now().seconds}")

        filePath.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                filePath.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        // Creating a Journal Object
                        val journal = Journal(
                            title = title,
                            thoughts = thoughts,
                            imageUrl = imageUrl,
                            timeAdded = Timestamp(Date()),
                            userName = currentUserName,
                            userId = currentUserId
                        )

                        collectionReference.add(journal)
                            .addOnSuccessListener { documentReference ->
                                progressBar.visibility = View.INVISIBLE

                                val intent = Intent(this@AddJournalActivity, JournalListActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                progressBar.visibility = View.INVISIBLE
                                Toast.makeText(this@AddJournalActivity, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(this@AddJournalActivity, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
