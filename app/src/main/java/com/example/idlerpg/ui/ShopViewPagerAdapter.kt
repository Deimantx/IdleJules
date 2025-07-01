package com.example.idlerpg.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ShopViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // Buy and Sell tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ShopBuyFragment() // Fragment for buying items
            1 -> ShopSellFragment() // Fragment for selling items
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
