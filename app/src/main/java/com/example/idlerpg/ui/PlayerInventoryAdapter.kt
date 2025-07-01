package com.example.idlerpg.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.idlerpg.R
import com.example.idlerpg.models.GearItem

class PlayerInventoryAdapter(
    private var items: List<GearItem>,
    private val onSellClicked: (GearItem) -> Unit,
    private val onCompareClicked: (GearItem) -> Unit, // New callback
    private val sellPricePercentage: Float = 0.5f // Default sell price percentage
) : RecyclerView.Adapter<PlayerInventoryAdapter.PlayerInventoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerInventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_shop_item, parent, false) // Reusing layout_shop_item
        return PlayerInventoryViewHolder(view) // Callback passed via bind
    }

    override fun onBindViewHolder(holder: PlayerInventoryViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onSellClicked, onCompareClicked, sellPricePercentage) // Pass compare callback
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<GearItem>) {
        items = newItems
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }

    class PlayerInventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.tvItemName)
        private val itemCost: TextView = itemView.findViewById(R.id.tvItemCost) // Will display sell price
        private val actionButton: Button = itemView.findViewById(R.id.btnBuyItem) // Will be "Sell"

        // Stat TextViews from layout_shop_item.xml
        private val tvItemAttackBonus: TextView = itemView.findViewById(R.id.tvItemAttackBonus)
        private val tvItemDefenseBonus: TextView = itemView.findViewById(R.id.tvItemDefenseBonus)
        private val tvItemAttackSpeed: TextView = itemView.findViewById(R.id.tvItemAttackSpeed)
        private val tvItemCritRate: TextView = itemView.findViewById(R.id.tvItemCritRate)
        private val tvItemCritDamage: TextView = itemView.findViewById(R.id.tvItemCritDamage)
        private val tvItemDodge: TextView = itemView.findViewById(R.id.tvItemDodge)
        private val tvItemHit: TextView = itemView.findViewById(R.id.tvItemHit)

        fun bind(
            gearItem: GearItem,
            onSellClicked: (GearItem) -> Unit,
            onCompareClicked: (GearItem) -> Unit, // Receive callback
            sellPricePercentage: Float
        ) {
            itemName.text = gearItem.name

            val sellPrice = (gearItem.cost * sellPricePercentage).toInt()
            itemCost.text = "Sell Price: $sellPrice Coins"

            actionButton.text = "Sell"
            actionButton.setOnClickListener { onSellClicked(gearItem) }

            itemView.setOnClickListener {
                onCompareClicked(gearItem)
            }
            // Ensure the sell button click doesn't also trigger the itemView click listener.

            // Helper function to set stat text and visibility
            fun setStatText(textView: TextView, prefix: String, value: Number, unit: String = "", positiveSign: Boolean = true) {
                val isZero = when (value) {
                    is Int -> value == 0
                    is Float -> value == 0f
                    else -> false
                }
                if (!isZero) {
                    val sign = if (positiveSign && value.toFloat() > 0) "+" else ""
                    textView.text = "$prefix $sign$value$unit"
                    textView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.GONE
                }
            }

            setStatText(tvItemAttackBonus, "ATK:", gearItem.attackBonus)
            setStatText(tvItemDefenseBonus, "DEF:", gearItem.defenseBonus)
            setStatText(tvItemAttackSpeed, "Speed:", gearItem.attackSpeedBonus, "ms", positiveSign = false)
            setStatText(tvItemCritRate, "Crit Rate:", gearItem.critRateBonus, "%")
            setStatText(tvItemCritDamage, "Crit DMG:", gearItem.critDamageBonus, "%")
            setStatText(tvItemDodge, "Dodge:", gearItem.dodgeBonus, "%")
            setStatText(tvItemHit, "Hit:", gearItem.hitBonus, "%")

            // Optional: Manage visibility of stats container (llItemStatsContainer) if needed
            val statsContainer: ViewGroup? = itemView.findViewById(R.id.llItemStatsContainer)
            statsContainer?.let {
                var anyStatVisible = false
                for (i in 0 until it.childCount) {
                    if (it.getChildAt(i).visibility == View.VISIBLE) {
                        anyStatVisible = true
                        break
                    }
                }
                 // If you want to hide the container or show "No stat bonuses", add logic here
            }
        }
    }
}
