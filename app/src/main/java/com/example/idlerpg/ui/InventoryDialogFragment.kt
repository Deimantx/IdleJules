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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.idlerpg.R
import com.example.idlerpg.viewmodels.MainViewModel

class InventoryDialogFragment : DialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var tvPlayerCoinsInventory: TextView
    private lateinit var rvInventoryItems: RecyclerView
    private lateinit var tvEmptyInventory: TextView
    private lateinit var inventoryAdapter: InventoryAdapter

    companion object {
        const val TAG = "InventoryDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPlayerCoinsInventory = view.findViewById(R.id.tvPlayerCoinsInventory)
        rvInventoryItems = view.findViewById(R.id.rvInventoryItems)
        tvEmptyInventory = view.findViewById(R.id.tvEmptyInventory)
        val btnCloseInventory: Button = view.findViewById(R.id.btnCloseInventory)

        setupRecyclerView()
        setupObservers()

        btnCloseInventory.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        try {
            inventoryAdapter = InventoryAdapter(
                emptyList(),
                onEquipClicked = { item ->
                    try {
                        viewModel.equipItem(item)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error equipping item: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                onSellClicked = { item ->
                    try {
                        viewModel.sellPlayerItem(item)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error selling item: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            rvInventoryItems.adapter = inventoryAdapter
            rvInventoryItems.layoutManager = LinearLayoutManager(context)
        } catch (e: Exception) {
            Toast.makeText(context, "Error setting up inventory: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.playerData.observe(viewLifecycleOwner) { player ->
            player?.let {
                tvPlayerCoinsInventory.text = "Your Coins: ${it.coins}"
                
                // Handle empty inventory state
                if (it.inventory.isEmpty()) {
                    rvInventoryItems.visibility = View.GONE
                    tvEmptyInventory.visibility = View.VISIBLE
                } else {
                    rvInventoryItems.visibility = View.VISIBLE
                    tvEmptyInventory.visibility = View.GONE
                    try {
                        inventoryAdapter.updateItems(it.inventory)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error updating inventory: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
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
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            it.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}