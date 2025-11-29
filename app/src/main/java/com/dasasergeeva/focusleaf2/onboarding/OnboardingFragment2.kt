package com.dasasergeeva.focusleaf2.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dasasergeeva.focusleaf2.databinding.FragmentOnboarding2Binding

/**
 * Фрагмент третьего экрана онбординга
 * Рассказывает о технике Pomodoro
 */
class OnboardingFragment2 : Fragment() {

    private var _binding: FragmentOnboarding2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Все данные уже установлены через XML
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


