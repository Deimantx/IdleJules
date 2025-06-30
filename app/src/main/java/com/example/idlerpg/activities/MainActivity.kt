package com.example.idlerpg.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.idlerpg.R
import com.example.idlerpg.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var tvPlayerLevel: TextView
    private lateinit var tvPlayerExperience: TextView
    private lateinit var tvPlayerHP: TextView
    private lateinit var tvPlayerAttack: TextView
    private lateinit var tvPlayerDefense: TextView
    private lateinit var tvPlayerCoins: TextView

    private lateinit var tvMonsterName: TextView
    private lateinit var tvMonsterHP: TextView

    private lateinit var tvCombatLog: TextView
    private lateinit var btnManualAttack: Button
    private lateinit var btnShop: Button

    private val gameLoopHandler = Handler(Looper.getMainLooper())
    private val autoFightRunnable: Runnable = object : Runnable {
        override fun run() {
            viewModel.autoFightTick()
            gameLoopHandler.postDelayed(this, AUTO_FIGHT_INTERVAL_MS)
        }
    }

    companion object {
        private const val AUTO_FIGHT_INTERVAL_MS = 2000L // 2 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUI()
        setupObservers()
        setupButtonClickListeners()
    }

    private fun initializeUI() {
        tvPlayerLevel = findViewById(R.id.tvPlayerLevel)
        tvPlayerExperience = findViewById(R.id.tvPlayerExperience)
        tvPlayerHP = findViewById(R.id.tvPlayerHP)
        tvPlayerAttack = findViewById(R.id.tvPlayerAttack)
        tvPlayerDefense = findViewById(R.id.tvPlayerDefense)
        tvPlayerCoins = findViewById(R.id.tvPlayerCoins)

        tvMonsterName = findViewById(R.id.tvMonsterName)
        tvMonsterHP = findViewById(R.id.tvMonsterHP)

        tvCombatLog = findViewById(R.id.tvCombatLog)
        tvCombatLog.movementMethod = ScrollingMovementMethod() // Enable scrolling

        btnManualAttack = findViewById(R.id.btnManualAttack)
        btnShop = findViewById(R.id.btnShop)
    }

    private fun setupObservers() {
        viewModel.playerData.observe(this) { player ->
            player?.let {
                tvPlayerLevel.text = "Level: ${it.level}"
                tvPlayerHP.text = "HP: ${it.currentHp} / ${it.maxHp}"
                tvPlayerAttack.text = "Attack: ${it.attack + (it.equippedWeapon?.attackBonus ?: 0)}"
                tvPlayerDefense.text = "Defense: ${it.defense + (it.equippedArmor?.defenseBonus ?: 0)}"
                tvPlayerCoins.text = "Coins: ${it.coins}"
            }
        }

        viewModel.playerExperienceDisplay.observe(this) { experienceText ->
            tvPlayerExperience.text = "XP: $experienceText"
        }

        viewModel.monsterData.observe(this) { monster ->
            monster?.let {
                tvMonsterName.text = "Monster: ${it.name}"
                tvMonsterHP.text = "HP: ${it.hp}"
            } ?: run {
                tvMonsterName.text = "Monster: None"
                tvMonsterHP.text = "HP: -"
            }
        }

        viewModel.combatLog.observe(this) { logMessages ->
            tvCombatLog.text = logMessages.joinToString("\n")
            // Scroll to the top to see the latest message when log is updated
            // (since new messages are added to the start of the list in ViewModel)
            if (tvCombatLog.lineCount > 0) { // Check to prevent issues if text is empty
                tvCombatLog.scrollTo(0, 0)
            }
        }
    }

    private fun setupButtonClickListeners() {
        btnManualAttack.setOnClickListener {
            viewModel.manualAttack()
        }

        btnShop.setOnClickListener {
            // TODO: Implement shop functionality
            Toast.makeText(this, "Shop not implemented yet!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        gameLoopHandler.postDelayed(autoFightRunnable, AUTO_FIGHT_INTERVAL_MS)
    }

    override fun onPause() {
        super.onPause()
        gameLoopHandler.removeCallbacks(autoFightRunnable)
        viewModel.saveGame() // Save game when activity is paused
    }
}
