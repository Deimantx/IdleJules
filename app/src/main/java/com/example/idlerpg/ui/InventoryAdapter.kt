package com.example.idlerpg.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.idlerpg.R
import com.example.idlerpg.models.GearItem
import com.example.idlerpg.models.ItemType

class InventoryAdapter(
    private var items: List<GearItem>,
    private val onEquipClicked: (GearItem) -> Unit,
    private val onSellClicked: (GearItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    fun updateItems(newItems: List<GearItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_inventory_item, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvItemName: TextView = itemView.findViewById(R.id.tvInventoryItemName)
        private val tvItemType: TextView = itemView.findViewById(R.id.tvInventoryItemType)
        private val tvItemStats: TextView = itemView.findViewById(R.id.tvInventoryItemStats)
        private val tvItemDescription: TextView = itemView.findViewById(R.id.tvInventoryItemDescription)
        private val tvItemValue: TextView = itemView.findViewById(R.id.tvInventoryItemValue)
        private val btnEquip: Button = itemView.findViewById(R.id.btnEquipInventoryItem)
        private val btnSell: Button = itemView.findViewById(R.id.btnSellInventoryItem)

        fun bind(item: GearItem) {
            tvItemName.text = item.name
            
            // Display item type with subtype if available
            val typeText = when (item.type) {
                ItemType.WEAPON -> {
                    item.weaponType?.let { "Weapon (${it.name.lowercase().replaceFirstChar { c -> c.uppercase() }})" } ?: "Weapon"
                }
                ItemType.ARMOR -> {
                    item.armorType?.let { "Armor (${it.name.lowercase().replaceFirstChar { c -> c.uppercase() }})" } ?: "Armor"
                }
                else -> item.type.name.lowercase().replaceFirstChar { it.uppercase() }
            }
            tvItemType.text = typeText

            // Build stats text
            val stats = mutableListOf<String>()
            if (item.attackBonus > 0) stats.add("Atk: +${item.attackBonus}")
            if (item.defenseBonus > 0) stats.add("Def: +${item.defenseBonus}")
            if (item.hpBonus > 0) stats.add("HP: +${item.hpBonus}")
            if (item.manaBonus > 0) stats.add("Mana: +${item.manaBonus}")
            if (item.critRateBonus > 0) stats.add("Crit: +${item.critRateBonus}%")
            if (item.critDamageBonus > 0) stats.add("CritDmg: +${(item.critDamageBonus * 100).toInt()}%")
            if (item.dodgeBonus > 0) stats.add("Dodge: +${item.dodgeBonus}%")
            if (item.hitBonus > 0) stats.add("Hit: +${item.hitBonus}%")
            if (item.attackSpeedBonus != 0f) {
                val speedText = if (item.attackSpeedBonus < 0) "Speed: +${(-item.attackSpeedBonus / 1000f).toInt()}s" else "Speed: -${(item.attackSpeedBonus / 1000f).toInt()}s"
                stats.add(speedText)
            }

            tvItemStats.text = if (stats.isNotEmpty()) stats.joinToString(", ") else "No bonuses"
            tvItemDescription.text = item.description.ifEmpty { "No description" }
            
            val sellPrice = (item.cost * 0.5f).toInt()
            tvItemValue.text = "Sell: ${sellPrice}g"

            btnEquip.setOnClickListener { onEquipClicked(item) }
            btnSell.setOnClickListener { onSellClicked(item) }
        }
    }
}