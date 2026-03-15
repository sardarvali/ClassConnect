package com.example.classconnect.livechat

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.classconnect.R

class LiveChatRoom : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var etMessage: EditText
    lateinit var btnSend: MaterialButton
    lateinit var adapter: ChatAdapter
    val messageList = ArrayList<ChatModel>()

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_chat_room)

        val toolbar: Toolbar = findViewById(R.id.liveChatToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.chatRecycler)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("LiveChat")

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(messageList)
        recyclerView.adapter = adapter

        btnSend.setOnClickListener {

            val msg = etMessage.text.toString().trim()
            val sender = auth.currentUser?.displayName ?: "Student"

            if (msg.isEmpty()) return@setOnClickListener

            val chat = ChatModel(msg, sender)

            database.push().setValue(chat)
                .addOnSuccessListener {
                    etMessage.text.clear()
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }


        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                messageList.clear()

                for (data in snapshot.children) {
                    val chat = data.getValue(ChatModel::class.java)
                    if (chat != null) {
                        messageList.add(chat)
                    }
                }

                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messageList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}