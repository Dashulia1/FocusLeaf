package com.dasasergeeva.focusleaf2.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Адаптер для ViewPager2 онбординга
 * Управляет отображением 4 экранов онбординга
 */
class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment0()
            1 -> OnboardingFragment1()
            2 -> OnboardingFragment2()
            3 -> OnboardingFragment3()
            else -> OnboardingFragment0()
        }
    }
}


