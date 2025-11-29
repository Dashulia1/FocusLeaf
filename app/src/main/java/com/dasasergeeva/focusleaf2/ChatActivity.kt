package com.dasasergeeva.focusleaf2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dasasergeeva.focusleaf2.databinding.ActivityChatBinding
import com.dasasergeeva.focusleaf2.databinding.ItemMessageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : BaseActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private val messages = mutableListOf<ChatMessage>()

    data class ChatMessage(
        val text: String,
        val isUser: Boolean, // true - от пользователя, false - от поддержки
        val timestamp: Long = System.currentTimeMillis()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupWindow()
        
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        setupMessages()
        setupRecyclerView()
    }

    /**
     * Настройка окна: светлый статус бар
     */
    private fun setupWindow() {
        window?.let { window ->
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = true
            }
        }
    }

    /**
     * Настройка статус бара
     */
    private fun setupStatusBar() {
        window?.statusBarColor = ContextCompat.getColor(this, R.color.pink_gradient_start)
    }

    /**
     * Настройка слушателей
     */
    private fun setupListeners() {
        // Кнопка назад
        binding.ibBack.setOnClickListener {
            finish()
        }

        // Кнопка отправки сообщения
        binding.fabSend.setOnClickListener {
            sendMessage()
        }
    }

    /**
     * Настройка списка сообщений
     */
    private fun setupMessages() {
        // Добавляем приветственное сообщение от поддержки
        messages.add(
            ChatMessage(
                text = "Здравствуйте! Я готов помочь вам с любыми вопросами.",
                isUser = false,
                timestamp = System.currentTimeMillis() - 60000 // минуту назад
            )
        )
    }

    /**
     * Настройка RecyclerView
     */
    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter(messages)
        
        val layoutManager = LinearLayoutManager(this@ChatActivity).apply {
            stackFromEnd = true // Прокрутка снизу вверх
        }
        
        binding.rvMessages.layoutManager = layoutManager
        binding.rvMessages.adapter = messagesAdapter
        
        // Прокрутка к последнему сообщению
        scrollToBottom()
    }

    /**
     * Отправка сообщения
     */
    private fun sendMessage() {
        val messageText = binding.etMessage.text?.toString()?.trim()
        
        if (messageText.isNullOrEmpty()) {
            Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show()
            return
        }

        // Добавляем сообщение от пользователя
        val userMessage = ChatMessage(
            text = messageText,
            isUser = true
        )
        
        messages.add(userMessage)
        messagesAdapter.notifyDataSetChanged()
        
        // Очищаем поле ввода
        binding.etMessage.text?.clear()
        
        // Прокрутка к последнему сообщению
        binding.rvMessages.post {
            if (messages.isNotEmpty()) {
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
        }

        // Имитация ответа от поддержки (можно заменить на реальный API)
        binding.root.postDelayed({
            val supportMessage = ChatMessage(
                text = "Спасибо за ваше сообщение! Обрабатываю ваш запрос...",
                isUser = false
            )
            messages.add(supportMessage)
            messagesAdapter.notifyDataSetChanged()
            
            // Прокрутка к последнему сообщению
            binding.rvMessages.post {
                if (messages.isNotEmpty()) {
                    binding.rvMessages.scrollToPosition(messages.size - 1)
                }
            }
        }, 1000)
    }

    /**
     * Прокрутка к последнему сообщению
     */
    private fun scrollToBottom() {
        binding.rvMessages.post {
            if (messages.isNotEmpty()) {
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
        }
    }

    /**
     * Адаптер для списка сообщений
     */
    inner class MessagesAdapter(private val messagesList: List<ChatMessage>) :
        RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

        inner class MessageViewHolder(private val binding: ItemMessageBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(message: ChatMessage) {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val timeText = timeFormat.format(Date(message.timestamp))

                if (message.isUser) {
                    // Сообщение от пользователя
                    binding.llUserMessage.visibility = View.VISIBLE
                    binding.llSupportMessage.visibility = View.GONE
                    binding.tvUserMessageText.text = message.text
                    binding.tvUserMessageTime.text = timeText
                } else {
                    // Сообщение от поддержки
                    binding.llUserMessage.visibility = View.GONE
                    binding.llSupportMessage.visibility = View.VISIBLE
                    binding.tvSupportMessageText.text = message.text
                    binding.tvSupportMessageTime.text = timeText
                }
            }
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): MessageViewHolder {
            val binding = ItemMessageBinding.inflate(
                android.view.LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MessageViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.bind(messagesList[position])
        }

        override fun getItemCount(): Int = messagesList.size
    }
}

