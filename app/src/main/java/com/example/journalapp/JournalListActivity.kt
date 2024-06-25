package com.example.journalapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.journalapp.database.Journal
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class JournalListActivity : AppCompatActivity() {

    // FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    // Firebase Firestore
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Journal")

    // List of Journals
    private var journalList: MutableList<Journal> = mutableListOf()

    // RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyRecyclerViewAdapter

    // FloatingActionButton
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_list)

        // Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize FloatingActionButton
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this@JournalListActivity, AddJournalActivity::class.java))
        }
    }

    // Adding Menu Options
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                if (user != null) {
                    startActivity(Intent(this@JournalListActivity, AddJournalActivity::class.java))
                }
                return true
            }
            R.id.action_signout -> {
                if (user != null) {
                    firebaseAuth.signOut()
                    startActivity(Intent(this@JournalListActivity, MainActivity::class.java))
                    finish()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        // Clear previous data from journalList
        journalList.clear()

        // Retrieve journals from Firestore
        collectionReference.get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                for (documentSnapshot in queryDocumentSnapshots) {
                    // Convert each document to a Journal object
                    val journal = documentSnapshot.toObject(Journal::class.java)
                    journalList.add(0,journal)
                }

                // Initialize adapter and set it to RecyclerView
                myAdapter = MyRecyclerViewAdapter(this@JournalListActivity, journalList) { journal ->
                    // Handle click action here if needed
                    Toast.makeText(this@JournalListActivity, "Clicked: ${journal.title}", Toast.LENGTH_SHORT).show()
                }

                recyclerView.adapter = myAdapter // Set adapter to RecyclerView
                myAdapter.notifyDataSetChanged()

            }
            .addOnFailureListener { e ->
                Toast.makeText(this@JournalListActivity, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show()
            }
    }
}
