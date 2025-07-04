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

class ShopSellFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var playerInventoryAdapter: PlayerInventoryAdapter // To be created
    private lateinit var rvShopSellItems: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_sell_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvShopSellItems = view.findViewById(R.id.rvShopSellItems)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        playerInventoryAdapter = PlayerInventoryAdapter(
            emptyList(),
            onSellClicked = { item ->
                viewModel.sellPlayerItem(item)
            },
            onCompareClicked = { item ->
                handleCompareItem(item)
            }
        )
        rvShopSellItems.adapter = playerInventoryAdapter
        rvShopSellItems.layoutManager = LinearLayoutManager(context)
    }

    private fun setupObservers() {
        viewModel.playerData.observe(viewLifecycleOwner) { player ->
            player?.let {
                // Update the adapter with the player's inventory
                playerInventoryAdapter.updateItems(it.inventory)
            }
        }
        // Player coins are observed by the parent ShopDialogFragment
        // Toast messages for sell confirmation are also handled by the parent
    }

    private fun handleCompareItem(selectedItem: com.example.idlerpg.models.GearItem) {
        val player = viewModel.playerData.value ?: return
        val equippedItem = when (selectedItem.type) {
            com.example.idlerpg.models.ItemType.WEAPON -> player.equippedWeapon
            com.example.idlerpg.models.ItemType.ARMOR -> player.equippedArmor
            else -> null // Should not happen for typical inventory items if they are equippable
        }
        val dialog = ItemComparisonDialogFragment.newInstance(selectedItem, equippedItem)
        dialog.show(parentFragmentManager, ItemComparisonDialogFragment.TAG)
    }
}
