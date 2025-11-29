package com.dasasergeeva.focusleaf2.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dasasergeeva.focusleaf2.databinding.FragmentAnalyticsTodayBinding

/**
 * Фрагмент аналитики за сегодня
 */
class AnalyticsTodayFragment : Fragment() {

    private var _binding: FragmentAnalyticsTodayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: Загрузить и отобразить данные аналитики за сегодня
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


