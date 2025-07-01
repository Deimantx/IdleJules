package com.example.idlerpg.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.idlerpg.R
import com.example.idlerpg.viewmodels.MainViewModel

class ShopBuyFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var shopItemAdapter: ShopItemAdapter
    private lateinit var rvShopBuyItems: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_buy_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvShopBuyItems = view.findViewById(R.id.rvShopBuyItems)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        shopItemAdapter = ShopItemAdapter(
            emptyList(),
            onBuyClicked = { item ->
                viewModel.buyShopItem(item)
            },
            onCompareClicked = { item ->
                handleCompareItem(item)
            }
        )
        rvShopBuyItems.adapter = shopItemAdapter
        rvShopBuyItems.layoutManager = LinearLayoutManager(context)
    }

    private fun setupObservers() {
        viewModel.shopItems.observe(viewLifecycleOwner) { items ->
            shopItemAdapter.updateItems(items) // This updates the list of items
        }
        // Player coins are observed by the parent ShopDialogFragment,
        // but we also need to react here to update the adapter's knowledge of player coins
        // for enabling/disabling buy buttons.
        viewModel.playerData.observe(viewLifecycleOwner) { player ->
            player?.let {
                val oldCoins = shopItemAdapter.playerCoins
                shopItemAdapter.playerCoins = it.coins
                // Only notify if coin amount actually changed, to avoid unnecessary redraws
                // if other player data changed.
                if (oldCoins != it.coins) {
                    shopItemAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun handleCompareItem(selectedItem: com.example.idlerpg.models.GearItem) {
        val player = viewModel.playerData.value ?: return
        val equippedItem = when (selectedItem.type) {
            com.example.idlerpg.models.ItemType.WEAPON -> player.equippedWeapon
            com.example.idlerpg.models.ItemType.ARMOR -> player.equippedArmor
            else -> null
        }
        val dialog = ItemComparisonDialogFragment.newInstance(selectedItem, equippedItem)
        dialog.show(parentFragmentManager, ItemComparisonDialogFragment.TAG)
    }
}
