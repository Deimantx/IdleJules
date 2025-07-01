package com.example.idlerpg.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.idlerpg.models.ItemType

class ShopViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 6 // Weapons, Armor, Shields, Amulets, Rings, Sell

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ShopCategoryFragment.newInstance(ItemType.WEAPON) // Weapons
            1 -> ShopCategoryFragment.newInstance(ItemType.ARMOR) // Armor
            2 -> ShopCategoryFragment.newInstance(ItemType.SHIELD) // Shields
            3 -> ShopCategoryFragment.newInstance(ItemType.AMULET) // Amulets
            4 -> ShopCategoryFragment.newInstance(ItemType.RING) // Rings
            5 -> ShopSellFragment() // Sell tab
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
