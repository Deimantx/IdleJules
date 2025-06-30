package com.example.idlerpg.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.idlerpg.R
import com.example.idlerpg.models.GearItem

class ShopItemAdapter(
    private var items: List<GearItem>,
    private val onBuyClicked: (GearItem) -> Unit
) : RecyclerView.Adapter<ShopItemAdapter.ShopItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_shop_item, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onBuyClicked)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<GearItem>) {
        items = newItems
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }

    class ShopItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.tvItemName)
        private val itemStats: TextView = itemView.findViewById(R.id.tvItemStats)
        private val itemCost: TextView = itemView.findViewById(R.id.tvItemCost)
        private val buyButton: Button = itemView.findViewById(R.id.btnBuyItem)

        fun bind(gearItem: GearItem, onBuyClicked: (GearItem) -> Unit) {
            itemName.text = gearItem.name

            val statsBuilder = StringBuilder("Stats: ")
            if (gearItem.attackBonus > 0) {
                statsBuilder.append("ATK +${gearItem.attackBonus} ")
            }
            if (gearItem.defenseBonus > 0) {
                statsBuilder.append("DEF +${gearItem.defenseBonus}")
            }
            if (statsBuilder.toString() == "Stats: ") { // No stats
                itemStats.text = "No stat bonuses" // Changed text slightly for clarity
                itemStats.visibility = View.VISIBLE // Ensure it's visible
            } else {
                itemStats.text = statsBuilder.toString().trim()
                itemStats.visibility = View.VISIBLE
            }

            itemCost.text = "Cost: ${gearItem.cost} Coins"
            buyButton.setOnClickListener { onBuyClicked(gearItem) }
        }
    }
}
