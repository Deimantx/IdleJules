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
    private val onBuyClicked: (GearItem) -> Unit,
    private val onCompareClicked: (GearItem) -> Unit // New callback
) : RecyclerView.Adapter<ShopItemAdapter.ShopItemViewHolder>() {

    var playerCoins: Int = 0 // Property to hold current player coins

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_shop_item, parent, false)
        return ShopItemViewHolder(view) // Pass callback via bind method
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onBuyClicked, onCompareClicked, playerCoins) // Pass playerCoins
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<GearItem>) {
        items = newItems
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }

    class ShopItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.tvItemName)
        private val itemCost: TextView = itemView.findViewById(R.id.tvItemCost)
        private val buyButton: Button = itemView.findViewById(R.id.btnBuyItem)

        // Stat TextViews from layout_shop_item.xml
        private val tvItemAttackBonus: TextView = itemView.findViewById(R.id.tvItemAttackBonus)
        private val tvItemDefenseBonus: TextView = itemView.findViewById(R.id.tvItemDefenseBonus)
        private val tvItemAttackSpeed: TextView = itemView.findViewById(R.id.tvItemAttackSpeed)
        private val tvItemCritRate: TextView = itemView.findViewById(R.id.tvItemCritRate)
        private val tvItemCritDamage: TextView = itemView.findViewById(R.id.tvItemCritDamage)
        private val tvItemDodge: TextView = itemView.findViewById(R.id.tvItemDodge)
        private val tvItemHit: TextView = itemView.findViewById(R.id.tvItemHit)


        // Constructor remains simple
        constructor(itemView: View) : super(itemView)

        fun bind(
            gearItem: GearItem,
            onBuyClicked: (GearItem) -> Unit,
            onCompareClicked: (GearItem) -> Unit, // Receive callback in bind
            currentCoins: Int // Receive current player coins
        ) {
            itemName.text = gearItem.name
            itemCost.text = "Cost: ${gearItem.cost} Coins"
            buyButton.text = "Buy"

            // Enable/disable buy button based on player coins
            val canAfford = currentCoins >= gearItem.cost
            buyButton.isEnabled = canAfford
            buyButton.alpha = if (canAfford) 1.0f else 0.5f

            buyButton.setOnClickListener { onBuyClicked(gearItem) }

            // Make the entire item view clickable for comparison
            itemView.setOnClickListener {
                onCompareClicked(gearItem)
            }
            // Ensure the buy button click doesn't also trigger the itemView click listener
            // by letting the button consume the event, or by checking source in a single listener.
            // For simplicity, separate listeners are fine if the button is on top and consumes touch.

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
            
            // Display HP and Mana bonuses if they exist
            if (gearItem.hpBonus > 0) {
                tvItemAttackSpeed.text = "HP: +${gearItem.hpBonus}"
                tvItemAttackSpeed.visibility = View.VISIBLE
            } else if (gearItem.manaBonus > 0) {
                tvItemAttackSpeed.text = "Mana: +${gearItem.manaBonus}"
                tvItemAttackSpeed.visibility = View.VISIBLE
            } else {
                setStatText(tvItemAttackSpeed, "Speed:", gearItem.attackSpeedBonus, "ms", positiveSign = false)
            }
            
            setStatText(tvItemCritRate, "Crit Rate:", gearItem.critRateBonus, "%")
            setStatText(tvItemCritDamage, "Crit DMG:", gearItem.critDamageBonus, "%")
            setStatText(tvItemDodge, "Dodge:", gearItem.dodgeBonus, "%")
            setStatText(tvItemHit, "Hit:", gearItem.hitBonus, "%")

            // Check if any stat is visible to manage overall stats container visibility (optional)
            val statsContainer: ViewGroup? = itemView.findViewById(R.id.llItemStatsContainer)
            statsContainer?.let {
                var anyStatVisible = false
                for (i in 0 until it.childCount) {
                    if (it.getChildAt(i).visibility == View.VISIBLE) {
                        anyStatVisible = true
                        break
                    }
                }
                // If you want to hide the container itself if no stats, uncomment below
                // it.visibility = if (anyStatVisible) View.VISIBLE else View.GONE
                // Or add a "No stat bonuses" text if all are GONE
            }
        }
    }
}
