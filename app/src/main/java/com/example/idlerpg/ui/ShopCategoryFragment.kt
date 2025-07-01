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
import com.example.idlerpg.models.ItemType
import com.example.idlerpg.viewmodels.MainViewModel

class ShopCategoryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var shopItemAdapter: ShopItemAdapter
    private lateinit var rvShopCategoryItems: RecyclerView
    private lateinit var itemType: ItemType

    companion object {
        private const val ARG_ITEM_TYPE = "item_type"

        fun newInstance(itemType: ItemType): ShopCategoryFragment {
            val fragment = ShopCategoryFragment()
            val args = Bundle()
            args.putSerializable(ARG_ITEM_TYPE, itemType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemType = arguments?.getSerializable(ARG_ITEM_TYPE) as? ItemType ?: ItemType.WEAPON
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_category_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvShopCategoryItems = view.findViewById(R.id.rvShopCategoryItems)
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
        rvShopCategoryItems.adapter = shopItemAdapter
        rvShopCategoryItems.layoutManager = LinearLayoutManager(context)
    }

    private fun setupObservers() {
        viewModel.shopItems.observe(viewLifecycleOwner) { items ->
            val filteredItems = items.filter { it.type == itemType }
            shopItemAdapter.updateItems(filteredItems)
        }
        
        viewModel.playerData.observe(viewLifecycleOwner) { player ->
            player?.let {
                val oldCoins = shopItemAdapter.playerCoins
                shopItemAdapter.playerCoins = it.coins
                if (oldCoins != it.coins) {
                    shopItemAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun handleCompareItem(selectedItem: com.example.idlerpg.models.GearItem) {
        val player = viewModel.playerData.value ?: return
        val equippedItem = when (selectedItem.type) {
            ItemType.WEAPON -> player.equippedWeapon
            ItemType.ARMOR -> player.equippedArmor
            ItemType.SHIELD -> player.equippedShield
            ItemType.AMULET -> player.equippedAmulet
            ItemType.RING -> player.equippedRing
        }
        val dialog = ItemComparisonDialogFragment.newInstance(selectedItem, equippedItem)
        dialog.show(parentFragmentManager, ItemComparisonDialogFragment.TAG)
    }
}