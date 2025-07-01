package com.example.idlerpg.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
// Removed RecyclerView imports as they are now in child fragments
import com.example.idlerpg.R
// Removed GearItem import as it's not directly used here anymore
import com.example.idlerpg.viewmodels.MainViewModel
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ShopDialogFragment : DialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var tvPlayerCoinsShop: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        const val TAG = "ShopDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPlayerCoinsShop = view.findViewById(R.id.tvPlayerCoinsShop)
        viewPager = view.findViewById(R.id.shopViewPager)
        tabLayout = view.findViewById(R.id.shopTabLayout)
        val btnCloseShop: Button = view.findViewById(R.id.btnCloseShop)

        setupViewPager()
        setupTabLayout()
        setupObservers()

        btnCloseShop.setOnClickListener {
            dismiss()
        }
    }

    private fun setupViewPager() {
        val adapter = ShopViewPagerAdapter(this)
        viewPager.adapter = adapter
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Buy"
                1 -> "Sell"
                else -> null
            }
        }.attach()
    }

    private fun setupObservers() {
        viewModel.playerData.observe(viewLifecycleOwner) { player ->
            player?.let {
                tvPlayerCoinsShop.text = "Your Coins: ${it.coins}"
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        dialog?.let {
            // Make dialog width match parent but not exceed a certain reasonable max
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            it.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}
