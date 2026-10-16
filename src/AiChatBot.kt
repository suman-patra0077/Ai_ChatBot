package com.example.day2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AiChatBot : AppCompatActivity() {

    private val chatList = mutableListOf<M>()
    private lateinit var chatAdapter: Adp
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ai_chat_bot)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupSendListener()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rvMessages)
        inputEditText = findViewById(R.id.etInput)
        sendButton = findViewById(R.id.btnSend)
    }

    private fun setupRecyclerView() {
        chatAdapter = Adp(chatList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter
    }

    private fun setupSendListener() {
        val aiModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "YOUR_API_KEY"
        )

        sendButton.setOnClickListener {
            val userText = inputEditText.text.toString().trim()
            if (userText.isNotEmpty()) {
                addMessageToList(M(userText, isU = true))
                inputEditText.text.clear()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = aiModel.generateContent(userText)
                        val aiResponseText = response.text ?: "No response"
                        withContext(Dispatchers.Main) {
                            addMessageToList(M(aiResponseText, isU = false))
                        }
                    } catch (e: Exception) {
                        val errorMessage = e.localizedMessage ?: "An error occurred"
                        withContext(Dispatchers.Main) {
                            addMessageToList(M(errorMessage, isU = false))
                        }
                    }
                }
            }
        }
    }

    private fun addMessageToList(message: M) {
        chatList.add(message)
        chatAdapter.notifyItemInserted(chatList.size - 1)
        recyclerView.scrollToPosition(chatList.size - 1)
    }
}