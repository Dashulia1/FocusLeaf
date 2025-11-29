package com.dasasergeeva.focusleaf2

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityLoginBinding
import com.dasasergeeva.focusleaf2.utils.PreferencesManager
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Регулярное выражение для валидации email
    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Проверяем, авторизован ли пользователь
        if (PreferencesManager.isUserAuthorized(this)) {
            // Если пользователь уже авторизован, переходим на MainActivity
            navigateToMain()
            return
        }
        
        setupWindow()
        
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupValidation()
    }
    
    /**
     * Настройка окна: светлый статус бар
     */
    private fun setupWindow() {
        window?.let { window ->
            // Устанавливаем светлый статус бар
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = true
            }
            // Устанавливаем цвет фона статус бара
            window.statusBarColor = ContextCompat.getColor(this, R.color.auth_background_gradient_start)
        }
    }

    /**
     * Настройка обработчиков событий для элементов интерфейса
     */
    private fun setupListeners() {
        // Обработчик кнопки Sign Up
        binding.btnSignUp.setOnClickListener {
            hideKeyboard()
            if (validateAllFields()) {
                handleSignUp()
            }
        }

        // Обработчик ссылки Login
        binding.tvLoginClickable.setOnClickListener {
            // TODO: Переход на экран входа
            Toast.makeText(this, "Переход на экран входа", Toast.LENGTH_SHORT).show()
        }

        // Обработчик Forgot Password
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Восстановление пароля", Toast.LENGTH_SHORT).show()
        }

        // Обработчики социальных сетей
        binding.btnFacebook.setOnClickListener {
            Toast.makeText(this, "Вход через Facebook", Toast.LENGTH_SHORT).show()
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Вход через Google", Toast.LENGTH_SHORT).show()
        }

        binding.btnApple.setOnClickListener {
            Toast.makeText(this, "Вход через Apple", Toast.LENGTH_SHORT).show()
        }

        // Скрытие клавиатуры при клике вне полей ввода
        binding.root.setOnClickListener {
            hideKeyboard()
        }
    }

    /**
     * Настройка валидации полей в реальном времени
     */
    private fun setupValidation() {
        // Валидация Email при потере фокуса
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }

        // Валидация Password при потере фокуса
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword()
            }
        }

        // Валидация Confirm Password при потере фокуса
        binding.etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateConfirmPassword()
            }
        }

        // Валидация при вводе текста
        binding.etEmail.addTextChangedListener(createTextWatcher { validateEmail() })
        binding.etPassword.addTextChangedListener(createTextWatcher { validatePassword() })
        binding.etConfirmPassword.addTextChangedListener(createTextWatcher { validateConfirmPassword() })
    }

    /**
     * Обработка регистрации при успешной валидации
     */
    private fun handleSignUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Анимация кнопки
        binding.btnSignUp.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                binding.btnSignUp.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()

        // TODO: Реализовать фактическую регистрацию через Firebase или другой сервис
        // Для демонстрации сохраняем авторизацию и показываем успешное сообщение
        PreferencesManager.setUserAuthorized(this, true, email)
        Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()

        // Переход на главный экран (MainActivity)
        navigateToMain()
    }

    /**
     * Переход на главный экран (MainActivity)
     */
    private fun navigateToMain() {
        val intent = android.content.Intent(this, MainActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Валидация всех полей
     */
    private fun validateAllFields(): Boolean {
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isConfirmPasswordValid = validateConfirmPassword()

        return isEmailValid && isPasswordValid && isConfirmPasswordValid
    }

    /**
     * Валидация email
     */
    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString().trim()

        return when {
            email.isEmpty() -> {
                showError(binding.tilEmail, getString(R.string.error_email_required))
                false
            }
            !isValidEmail(email) -> {
                showError(binding.tilEmail, getString(R.string.error_email_invalid))
                false
            }
            else -> {
                clearError(binding.tilEmail)
                true
            }
        }
    }

    /**
     * Валидация пароля
     */
    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString()

        return when {
            password.isEmpty() -> {
                showError(binding.tilPassword, getString(R.string.error_password_required))
                false
            }
            password.length < 6 -> {
                showError(binding.tilPassword, getString(R.string.error_password_too_short))
                false
            }
            else -> {
                clearError(binding.tilPassword)
                // Если пароль валиден, проверяем подтверждение
                if (binding.etConfirmPassword.text?.isNotEmpty() == true) {
                    validateConfirmPassword()
                }
                true
            }
        }
    }

    /**
     * Валидация подтверждения пароля
     */
    private fun validateConfirmPassword(): Boolean {
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        return when {
            confirmPassword.isEmpty() -> {
                showError(binding.tilConfirmPassword, getString(R.string.error_password_required))
                false
            }
            password != confirmPassword -> {
                showError(binding.tilConfirmPassword, getString(R.string.error_passwords_not_match))
                false
            }
            else -> {
                clearError(binding.tilConfirmPassword)
                true
            }
        }
    }

    /**
     * Проверка формата email
     */
    private fun isValidEmail(email: String): Boolean {
        return EMAIL_PATTERN.matcher(email).matches()
    }

    /**
     * Показать ошибку в поле ввода
     */
    private fun showError(textInputLayout: TextInputLayout, errorMessage: String) {
        textInputLayout.error = errorMessage
        textInputLayout.isErrorEnabled = true
    }

    /**
     * Убрать ошибку из поля ввода
     */
    private fun clearError(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
    }

    /**
     * Скрыть клавиатуру
     */
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Создать TextWatcher для валидации при вводе
     */
    private fun createTextWatcher(onTextChanged: () -> Unit): android.text.TextWatcher {
        return object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                onTextChanged()
            }
        }
    }
}

