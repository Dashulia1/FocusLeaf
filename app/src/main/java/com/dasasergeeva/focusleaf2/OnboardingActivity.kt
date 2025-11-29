package com.dasasergeeva.focusleaf2

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.dasasergeeva.focusleaf2.databinding.ActivityOnboardingBinding
import com.dasasergeeva.focusleaf2.onboarding.OnboardingPagerAdapter
import com.dasasergeeva.focusleaf2.utils.PreferencesManager
import com.dasasergeeva.focusleaf2.LoginActivity

class OnboardingActivity : BaseActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingPagerAdapter
    private val indicators = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Проверяем, был ли уже показан онбординг
        if (PreferencesManager.isOnboardingCompleted(this)) {
            // Если онбординг уже пройден, проверяем авторизацию
            if (PreferencesManager.isUserAuthorized(this)) {
                // Если пользователь авторизован, переходим на MainActivity
                navigateToMain()
            } else {
                // Если не авторизован, переходим на LoginActivity
                navigateToLogin()
            }
            return
        }
        
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupIndicators()
        setupButton()
        updateUI()
    }

    /**
     * Настройка ViewPager2 с адаптером
     */
    private fun setupViewPager() {
        adapter = OnboardingPagerAdapter(this)
        binding.viewPagerOnboarding.adapter = adapter

        // Отключение кликабельности при свайпе для лучшего UX
        // (или можно оставить для удобства)
        
        // Слушатель изменения страницы для обновления индикаторов и кнопки
        binding.viewPagerOnboarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
                updateButton(position)
            }
        })
    }

    /**
     * Создание индикаторов прогресса (точек)
     */
    private fun setupIndicators() {
        indicators.clear()
        binding.llIndicators.removeAllViews()

        // Размер точки в dp
        val dotSizePx = (12 * resources.displayMetrics.density).toInt()
        val marginPx = (8 * resources.displayMetrics.density).toInt()

        for (i in 0 until adapter.itemCount) {
            // Создаем кастомные точки через View
            val dotView = View(this)
            val params = ViewGroup.MarginLayoutParams(dotSizePx, dotSizePx)
            params.setMargins(marginPx, 0, marginPx, 0)
            dotView.layoutParams = params
            
            // Создаем круглый drawable для точки
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setSize(dotSizePx, dotSizePx)
            
            // Устанавливаем цвет в зависимости от позиции
            if (i == 0) {
                drawable.setColor(ContextCompat.getColor(this, R.color.onboarding_pink))
                dotView.alpha = 1.0f
            } else {
                drawable.setColor(ContextCompat.getColor(this, R.color.onboarding_text_secondary))
                dotView.alpha = 0.5f
            }
            
            dotView.background = drawable
            
            // Добавляем точку в список для управления
            indicators.add(dotView)
            binding.llIndicators.addView(dotView)
        }
    }

    /**
     * Обновление индикаторов при изменении страницы
     */
    private fun updateIndicators(position: Int) {
        indicators.forEachIndexed { index, dotView ->
            val drawable = dotView.background as? GradientDrawable
            if (index == position) {
                // Активная точка - розовый цвет
                drawable?.setColor(ContextCompat.getColor(this, R.color.onboarding_pink))
                dotView.alpha = 1.0f
            } else {
                // Неактивная точка - серый цвет
                drawable?.setColor(ContextCompat.getColor(this, R.color.onboarding_text_secondary))
                dotView.alpha = 0.5f
            }
        }
    }

    /**
     * Настройка кнопки "Далее" / "Начать"
     */
    private fun setupButton() {
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPagerOnboarding.currentItem
            if (currentItem < adapter.itemCount - 1) {
                // Переход на следующий экран
                binding.viewPagerOnboarding.currentItem = currentItem + 1
            } else {
                // Переход на экран входа
                finishOnboarding()
            }
        }
    }

    /**
     * Обновление текста и состояния кнопки в зависимости от страницы
     */
    private fun updateButton(position: Int) {
        if (position == adapter.itemCount - 1) {
            // Последняя страница - кнопка "Начать фокусироваться" с обводкой (outlined)
            binding.btnNext.text = getString(R.string.onboarding_start)
            // Делаем прозрачный фон для outlined стиля
            binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.transparent)
            binding.btnNext.setTextColor(ContextCompat.getColor(this, R.color.onboarding_pink))
            // Добавляем розовую обводку
            val strokeWidthPx = (2 * resources.displayMetrics.density).toInt()
            binding.btnNext.strokeWidth = strokeWidthPx
            binding.btnNext.strokeColor = ContextCompat.getColorStateList(this, R.color.onboarding_pink)
        } else {
            // Остальные страницы - кнопка "Далее" (solid)
            binding.btnNext.text = getString(R.string.onboarding_next)
            binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.onboarding_pink)
            binding.btnNext.setTextColor(ContextCompat.getColor(this, R.color.white))
            // Убираем обводку
            binding.btnNext.strokeWidth = 0
            binding.btnNext.strokeColor = null
        }
    }

    /**
     * Завершение онбординга и переход на экран входа
     */
    private fun finishOnboarding() {
        // Сохраняем, что онбординг пройден
        PreferencesManager.setOnboardingCompleted(this, true)
        
        // Переход на LoginActivity
        navigateToLogin()
    }

    /**
     * Переход на экран входа (LoginActivity)
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Переход на главный экран (MainActivity)
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Инициализация UI при создании
     */
    private fun updateUI() {
        // Установка начального состояния
        updateIndicators(0)
        updateButton(0)
    }

    override fun onBackPressed() {
        val currentItem = binding.viewPagerOnboarding.currentItem
        if (currentItem > 0) {
            // Возврат на предыдущий экран
            binding.viewPagerOnboarding.currentItem = currentItem - 1
        } else {
            // Если на первом экране, выходим из приложения или возвращаемся
            super.onBackPressed()
        }
    }
}

