package com.dasasergeeva.focusleaf2.analytics

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Адаптер для ViewPager2 аналитики
 * Управляет отображением вкладок "Аналитика за СЕГОДНЯ" и "Аналитика за НЕДЕЛЮ"
 */
class AnalyticsPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AnalyticsTodayFragment()
            1 -> AnalyticsWeekFragment()
            else -> AnalyticsTodayFragment()
        }
    }
}


