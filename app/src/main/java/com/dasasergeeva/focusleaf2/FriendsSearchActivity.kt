package com.dasasergeeva.focusleaf2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dasasergeeva.focusleaf2.databinding.ActivityFriendsSearchBinding

class FriendsSearchActivity : BaseActivity() {

    private lateinit var binding: ActivityFriendsSearchBinding
    private lateinit var friendsAdapter: FriendsAdapter
    private val allFriends = mutableListOf<Friend>()
    private val filteredFriends = mutableListOf<Friend>()

    data class Friend(
        val name: String,
        val age: Int,
        val gender: String,
        val isOnline: Boolean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupWindow()
        
        binding = ActivityFriendsSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        setupFriendsList()
        setupSearch()
        updateFocusSession()
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

        // Кнопка домой (переход на главный экран)
        binding.ibHome.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    /**
     * Настройка списка друзей
     */
    private fun setupFriendsList() {
        // Инициализация данных
        allFriends.clear()
        allFriends.addAll(getFriendsData())
        filteredFriends.clear()
        filteredFriends.addAll(allFriends)

        // Настройка RecyclerView
        friendsAdapter = FriendsAdapter(filteredFriends) { friend ->
            // Переход к совместной сессии
            if (friend.isOnline) {
                val intent = android.content.Intent(this, GroupSessionActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Пользователь не в сети", Toast.LENGTH_SHORT).show()
            }
        }

        binding.friendsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FriendsSearchActivity)
            adapter = friendsAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@FriendsSearchActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    /**
     * Настройка поиска
     */
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterFriends(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Фильтрация друзей по запросу
     */
    private fun filterFriends(query: String) {
        filteredFriends.clear()
        if (query.isBlank()) {
            filteredFriends.addAll(allFriends)
        } else {
            val lowerQuery = query.lowercase()
            filteredFriends.addAll(
                allFriends.filter { friend ->
                    friend.name.lowercase().contains(lowerQuery) ||
                            friend.age.toString().contains(lowerQuery)
                }
            )
        }
        friendsAdapter.notifyDataSetChanged()
    }

    /**
     * Обновление информации о текущей сессии фокуса
     */
    private fun updateFocusSession() {
        // TODO: Получить реальное время сессии из MainActivity или PreferencesManager
        // Пока используем тестовое значение
        binding.fabFocusSession.text = getString(R.string.in_focus_15_minutes)
        
        // Обработчик клика на FAB - переход к совместной сессии
        binding.fabFocusSession.setOnClickListener {
            val intent = android.content.Intent(this, GroupSessionActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Получение тестовых данных друзей
     */
    private fun getFriendsData(): List<Friend> {
        return listOf(
            Friend("Alice", 18, "Female", true),
            Friend("Male", 21, "Male", false),
            Friend("Craig", 22, "Male", true),
            Friend("Jonelle", 19, "Female", false),
            Friend("Madelyn", 25, "Female", false),
            Friend("Female", 14, "Female", false),
            Friend("Kitty", 20, "Male", false),
            Friend("Male", 16, "Male", true),
            Friend("Female", 18, "Female", false)
        )
    }
}

