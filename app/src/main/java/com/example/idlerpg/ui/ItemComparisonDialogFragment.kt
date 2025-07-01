package com.example.idlerpg.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.idlerpg.R
import com.example.idlerpg.models.GearItem
import com.google.gson.Gson // For passing GearItem objects

class ItemComparisonDialogFragment : DialogFragment() {

    private lateinit var tvSelectedName: TextView
    private lateinit var tvSelectedAttack: TextView
    private lateinit var tvSelectedDefense: TextView
    private lateinit var tvSelectedAttackSpeed: TextView
    private lateinit var tvSelectedCritRate: TextView
    private lateinit var tvSelectedCritDamage: TextView
    private lateinit var tvSelectedDodge: TextView
    private lateinit var tvSelectedHit: TextView
    private lateinit var tvSelectedCost: TextView

    private lateinit var tvEquippedName: TextView
    private lateinit var tvEquippedAttack: TextView
    private lateinit var tvEquippedDefense: TextView
    private lateinit var tvEquippedAttackSpeed: TextView
    private lateinit var tvEquippedCritRate: TextView
    private lateinit var tvEquippedCritDamage: TextView
    private lateinit var tvEquippedDodge: TextView
    private lateinit var tvEquippedHit: TextView
    // tvEquippedCost is present in layout but might not be used actively

    companion object {
        const val TAG = "ItemComparisonDialog"
        private const val ARG_SELECTED_ITEM_JSON = "selected_item_json"
        private const val ARG_EQUIPPED_ITEM_JSON = "equipped_item_json"

        fun newInstance(selectedItem: GearItem, equippedItem: GearItem?): ItemComparisonDialogFragment {
            val args = Bundle()
            val gson = Gson()
            args.putString(ARG_SELECTED_ITEM_JSON, gson.toJson(selectedItem))
            if (equippedItem != null) {
                args.putString(ARG_EQUIPPED_ITEM_JSON, gson.toJson(equippedItem))
            }
            val fragment = ItemComparisonDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_comparison, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)

        val gson = Gson()
        val selectedItemJson = arguments?.getString(ARG_SELECTED_ITEM_JSON)
        val equippedItemJson = arguments?.getString(ARG_EQUIPPED_ITEM_JSON)

        val selectedItem = gson.fromJson(selectedItemJson, GearItem::class.java)
        val equippedItem = equippedItemJson?.let { gson.fromJson(it, GearItem::class.java) }

        displayItemStats(selectedItem, equippedItem)

        view.findViewById<Button>(R.id.btnCloseComparison).setOnClickListener {
            dismiss()
        }
    }

    private fun bindViews(view: View) {
        tvSelectedName = view.findViewById(R.id.tvCompSelectedName)
        tvSelectedAttack = view.findViewById(R.id.tvCompSelectedAttack)
        tvSelectedDefense = view.findViewById(R.id.tvCompSelectedDefense)
        tvSelectedAttackSpeed = view.findViewById(R.id.tvCompSelectedAttackSpeed)
        tvSelectedCritRate = view.findViewById(R.id.tvCompSelectedCritRate)
        tvSelectedCritDamage = view.findViewById(R.id.tvCompSelectedCritDamage)
        tvSelectedDodge = view.findViewById(R.id.tvCompSelectedDodge)
        tvSelectedHit = view.findViewById(R.id.tvCompSelectedHit)
        tvSelectedCost = view.findViewById(R.id.tvCompSelectedCost)

        tvEquippedName = view.findViewById(R.id.tvCompEquippedName)
        tvEquippedAttack = view.findViewById(R.id.tvCompEquippedAttack)
        tvEquippedDefense = view.findViewById(R.id.tvCompEquippedDefense)
        tvEquippedAttackSpeed = view.findViewById(R.id.tvCompEquippedAttackSpeed)
        tvEquippedCritRate = view.findViewById(R.id.tvCompEquippedCritRate)
        tvEquippedCritDamage = view.findViewById(R.id.tvCompEquippedCritDamage)
        tvEquippedDodge = view.findViewById(R.id.tvCompEquippedDodge)
        tvEquippedHit = view.findViewById(R.id.tvCompEquippedHit)
    }

    private fun displayItemStats(selected: GearItem, equipped: GearItem?) {
        tvSelectedName.text = selected.name
        tvSelectedCost.text = "Cost: ${selected.cost}"
        setStatWithComparison(tvSelectedAttack, "ATK:", selected.attackBonus, equipped?.attackBonus)
        setStatWithComparison(tvSelectedDefense, "DEF:", selected.defenseBonus, equipped?.defenseBonus)
        setStatWithComparison(tvSelectedAttackSpeed, "Speed:", selected.attackSpeedBonus, equipped?.attackSpeedBonus, unit = "ms", lowerIsBetter = true)
        setStatWithComparison(tvSelectedCritRate, "Crit Rate:", selected.critRateBonus, equipped?.critRateBonus, unit = "%")
        setStatWithComparison(tvSelectedCritDamage, "Crit DMG:", selected.critDamageBonus, equipped?.critDamageBonus, unit = "%")
        setStatWithComparison(tvSelectedDodge, "Dodge:", selected.dodgeBonus, equipped?.dodgeBonus, unit = "%")
        setStatWithComparison(tvSelectedHit, "Hit:", selected.hitBonus, equipped?.hitBonus, unit = "%")

        if (equipped != null) {
            tvEquippedName.text = equipped.name
            // No cost for equipped item in this view usually
            setStatText(tvEquippedAttack, "ATK:", equipped.attackBonus)
            setStatText(tvEquippedDefense, "DEF:", equipped.defenseBonus)
            setStatText(tvEquippedAttackSpeed, "Speed:", equipped.attackSpeedBonus, "ms", false)
            setStatText(tvEquippedCritRate, "Crit Rate:", equipped.critRateBonus, "%")
            setStatText(tvEquippedCritDamage, "Crit DMG:", equipped.critDamageBonus, "%")
            setStatText(tvEquippedDodge, "Dodge:", equipped.dodgeBonus, "%")
            setStatText(tvEquippedHit, "Hit:", equipped.hitBonus, "%")
        } else {
            tvEquippedName.text = "Nothing Equipped"
            tvEquippedAttack.text = "ATK: 0"
            tvEquippedDefense.text = "DEF: 0"
            tvEquippedAttackSpeed.text = "Speed: 0ms"
            tvEquippedCritRate.text = "Crit Rate: 0%"
            tvEquippedCritDamage.text = "Crit DMG: 0%"
            tvEquippedDodge.text = "Dodge: 0%"
            tvEquippedHit.text = "Hit: 0%"
        }
    }

    private fun setStatText(textView: TextView, prefix: String, value: Number, unit: String = "", positiveSign: Boolean = true) {
        val sign = if (positiveSign && value.toFloat() > 0) "+" else if (!positiveSign && value.toFloat() == 0f) "" else if (value.toFloat() <0) "" else "" // Avoid double minus for speed
        textView.text = "$prefix $sign$value$unit"
    }

    private fun setStatWithComparison(selectedTextView: TextView, prefix: String, selectedValue: Number, equippedValue: Number?, unit: String = "", lowerIsBetter: Boolean = false) {
        val sVal = selectedValue.toFloat()
        val eVal = equippedValue?.toFloat() ?: 0f

        val sign = if (sVal > 0) "+" else ""
        var text = "$prefix $sign${selectedValue}$unit"
        selectedTextView.setTextColor(Color.WHITE) // Default color

        if (equippedValue != null) {
            val diff = sVal - eVal
            text += " ("
            if (diff != 0f) {
                val diffSign = if (diff > 0) "+" else ""
                text += "$diffSign${if (diff % 1 == 0f) diff.toInt() else String.format("%.1f", diff)}$unit"
                if ((!lowerIsBetter && diff > 0) || (lowerIsBetter && diff < 0)) {
                    selectedTextView.setTextColor(Color.GREEN) // Better
                } else if ((!lowerIsBetter && diff < 0) || (lowerIsBetter && diff > 0)) {
                    selectedTextView.setTextColor(Color.RED) // Worse
                }
            } else {
                text += "No Change"
            }
            text += ")"
        }
        selectedTextView.text = text
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        dialog?.let {
            val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            it.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}
