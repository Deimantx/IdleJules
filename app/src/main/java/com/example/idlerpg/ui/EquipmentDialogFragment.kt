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
import com.example.idlerpg.R
import com.example.idlerpg.models.ItemType
import com.example.idlerpg.viewmodels.MainViewModel

class EquipmentDialogFragment : DialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()
    
    private lateinit var tvEquippedWeapon: TextView
    private lateinit var tvEquippedArmor: TextView
    private lateinit var tvEquippedShield: TextView
    private lateinit var tvEquippedAmulet: TextView
    private lateinit var tvEquippedRing: TextView
    
    private lateinit var btnUnequipWeapon: Button
    private lateinit var btnUnequipArmor: Button
    private lateinit var btnUnequipShield: Button
    private lateinit var btnUnequipAmulet: Button
    private lateinit var btnUnequipRing: Button

    companion object {
        const val TAG = "EquipmentDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_equipment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        tvEquippedWeapon = view.findViewById(R.id.tvEquippedWeapon)
        tvEquippedArmor = view.findViewById(R.id.tvEquippedArmor)
        tvEquippedShield = view.findViewById(R.id.tvEquippedShield)
        tvEquippedAmulet = view.findViewById(R.id.tvEquippedAmulet)
        tvEquippedRing = view.findViewById(R.id.tvEquippedRing)
        
        btnUnequipWeapon = view.findViewById(R.id.btnUnequipWeapon)
        btnUnequipArmor = view.findViewById(R.id.btnUnequipArmor)
        btnUnequipShield = view.findViewById(R.id.btnUnequipShield)
        btnUnequipAmulet = view.findViewById(R.id.btnUnequipAmulet)
        btnUnequipRing = view.findViewById(R.id.btnUnequipRing)
        
        val btnCloseEquipment: Button = view.findViewById(R.id.btnCloseEquipment)

        setupClickListeners()
        setupObservers()

        btnCloseEquipment.setOnClickListener {
            dismiss()
        }
    }

    private fun setupClickListeners() {
        btnUnequipWeapon.setOnClickListener { viewModel.unequipItem(ItemType.WEAPON) }
        btnUnequipArmor.setOnClickListener { viewModel.unequipItem(ItemType.ARMOR) }
        btnUnequipShield.setOnClickListener { viewModel.unequipItem(ItemType.SHIELD) }
        btnUnequipAmulet.setOnClickListener { viewModel.unequipItem(ItemType.AMULET) }
        btnUnequipRing.setOnClickListener { viewModel.unequipItem(ItemType.RING) }
    }

    private fun setupObservers() {
        viewModel.playerData.observe(viewLifecycleOwner) { player ->
            player?.let {
                // Update weapon display
                if (it.equippedWeapon != null) {
                    tvEquippedWeapon.text = "${it.equippedWeapon!!.name}\n${getItemStatsText(it.equippedWeapon!!)}"
                    btnUnequipWeapon.isEnabled = true
                } else {
                    tvEquippedWeapon.text = "None"
                    btnUnequipWeapon.isEnabled = false
                }

                // Update armor display
                if (it.equippedArmor != null) {
                    tvEquippedArmor.text = "${it.equippedArmor!!.name}\n${getItemStatsText(it.equippedArmor!!)}"
                    btnUnequipArmor.isEnabled = true
                } else {
                    tvEquippedArmor.text = "None"
                    btnUnequipArmor.isEnabled = false
                }

                // Update shield display
                if (it.equippedShield != null) {
                    tvEquippedShield.text = "${it.equippedShield!!.name}\n${getItemStatsText(it.equippedShield!!)}"
                    btnUnequipShield.isEnabled = true
                } else {
                    tvEquippedShield.text = "None"
                    btnUnequipShield.isEnabled = false
                }

                // Update amulet display
                if (it.equippedAmulet != null) {
                    tvEquippedAmulet.text = "${it.equippedAmulet!!.name}\n${getItemStatsText(it.equippedAmulet!!)}"
                    btnUnequipAmulet.isEnabled = true
                } else {
                    tvEquippedAmulet.text = "None"
                    btnUnequipAmulet.isEnabled = false
                }

                // Update ring display
                if (it.equippedRing != null) {
                    tvEquippedRing.text = "${it.equippedRing!!.name}\n${getItemStatsText(it.equippedRing!!)}"
                    btnUnequipRing.isEnabled = true
                } else {
                    tvEquippedRing.text = "None"
                    btnUnequipRing.isEnabled = false
                }
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getItemStatsText(item: com.example.idlerpg.models.GearItem): String {
        val stats = mutableListOf<String>()
        if (item.attackBonus > 0) stats.add("Atk: +${item.attackBonus}")
        if (item.defenseBonus > 0) stats.add("Def: +${item.defenseBonus}")
        if (item.hpBonus > 0) stats.add("HP: +${item.hpBonus}")
        if (item.manaBonus > 0) stats.add("Mana: +${item.manaBonus}")
        if (item.critRateBonus > 0) stats.add("Crit: +${item.critRateBonus}%")
        if (item.critDamageBonus > 0) stats.add("CritDmg: +${(item.critDamageBonus * 100).toInt()}%")
        if (item.dodgeBonus > 0) stats.add("Dodge: +${item.dodgeBonus}%")
        if (item.hitBonus > 0) stats.add("Hit: +${item.hitBonus}%")
        
        return if (stats.isNotEmpty()) stats.joinToString(", ") else "No bonuses"
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        dialog?.let {
            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            it.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}