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
import com.example.idlerpg.ui.ShopDialogFragment // Import the dialog
import com.example.idlerpg.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    // Player Info UI
    private lateinit var tvPlayerLevel: TextView
    private lateinit var tvPlayerExperience: TextView
    private lateinit var tvPlayerHP: TextView
    private lateinit var tvPlayerAttack: TextView
    private lateinit var tvPlayerDefense: TextView
    private lateinit var tvPlayerCoins: TextView

    // Skill Points UI
    private lateinit var tvPlayerSkillPoints: TextView
    private lateinit var btnIncreaseAttack: Button
    private lateinit var btnIncreaseDefense: Button
    private lateinit var btnIncreaseMaxHp: Button

    // Monster Info UI
    private lateinit var tvMonsterName: TextView
    private lateinit var tvMonsterHP: TextView
    private lateinit var tvMonsterType: TextView
    private lateinit var tvMonsterAbility: TextView

    // Player Status UI
    private lateinit var tvPlayerStatusEffects: TextView

    // Other UI
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
        // Player Info
        tvPlayerLevel = findViewById(R.id.tvPlayerLevel)
        tvPlayerExperience = findViewById(R.id.tvPlayerExperience)
        tvPlayerHP = findViewById(R.id.tvPlayerHP)
        tvPlayerAttack = findViewById(R.id.tvPlayerAttack)
        tvPlayerDefense = findViewById(R.id.tvPlayerDefense)
        tvPlayerCoins = findViewById(R.id.tvPlayerCoins)

        // Skill Points
        tvPlayerSkillPoints = findViewById(R.id.tvPlayerSkillPoints)
        btnIncreaseAttack = findViewById(R.id.btnIncreaseAttack)
        btnIncreaseDefense = findViewById(R.id.btnIncreaseDefense)
        btnIncreaseMaxHp = findViewById(R.id.btnIncreaseMaxHp)

        // Monster Info
        tvMonsterName = findViewById(R.id.tvMonsterName)
        tvMonsterHP = findViewById(R.id.tvMonsterHP)
        tvMonsterType = findViewById(R.id.tvMonsterType)
        tvMonsterAbility = findViewById(R.id.tvMonsterAbility)

        // Player Status
        tvPlayerStatusEffects = findViewById(R.id.tvPlayerStatusEffects)

        // Other
        tvCombatLog = findViewById(R.id.tvCombatLog)
        tvCombatLog.movementMethod = ScrollingMovementMethod()

        btnManualAttack = findViewById(R.id.btnManualAttack)
        btnShop = findViewById(R.id.btnShop)
    }

    private fun setupObservers() {
        viewModel.playerData.observe(this) { player ->
            player?.let {
                tvPlayerLevel.text = "Level: ${it.level}"
                tvPlayerHP.text = "HP: ${it.currentHp} / ${it.maxHp}"
                // Use effectiveAttack and effectiveDefense from Player model
                tvPlayerAttack.text = "Attack: ${it.effectiveAttack} (Base: ${it.attack})"
                tvPlayerDefense.text = "Defense: ${it.effectiveDefense} (Base: ${it.defense})"
                tvPlayerCoins.text = "Coins: ${it.coins}"
                tvPlayerSkillPoints.text = "Available Skill Points: ${it.skillPoints}"

                // Enable/disable skill point buttons
                val hasSkillPoints = it.skillPoints > 0
                btnIncreaseAttack.isEnabled = hasSkillPoints
                btnIncreaseDefense.isEnabled = hasSkillPoints
                btnIncreaseMaxHp.isEnabled = hasSkillPoints

                // Update status effects
                tvPlayerStatusEffects.text = "Status: ${it.getStatusEffectsDescription()}"
            }
        }

        viewModel.playerExperienceDisplay.observe(this) { experienceText ->
            tvPlayerExperience.text = "XP: $experienceText"
        }

        viewModel.monsterData.observe(this) { monster ->
            monster?.let {
                tvMonsterName.text = "Monster: ${it.name}"
                tvMonsterHP.text = "HP: ${it.hp} / ${it.maxHp}"
                tvMonsterType.text = "Type: ${it.type.name.lowercase().replaceFirstChar { char -> char.uppercase() }}"
                val abilityText = if (it.ability.name != "NONE") {
                    it.getAbilityDescription()
                } else {
                    "None"
                }
                tvMonsterAbility.text = "Ability: $abilityText"
            } ?: run {
                tvMonsterName.text = "Monster: None"
                tvMonsterHP.text = "HP: -"
                tvMonsterType.text = "Type: -"
                tvMonsterAbility.text = "Ability: -"
            }
        }

        viewModel.combatLog.observe(this) { logMessages ->
            tvCombatLog.text = logMessages.joinToString("\n")
            if (tvCombatLog.lineCount > 0) {
                tvCombatLog.scrollTo(0, 0)
            }
        }

        viewModel.toastMessage.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtonClickListeners() {
        btnManualAttack.setOnClickListener {
            viewModel.manualAttack()
        }

        btnShop.setOnClickListener {
            ShopDialogFragment().show(supportFragmentManager, ShopDialogFragment.TAG)
        }

        btnIncreaseAttack.setOnClickListener {
            viewModel.spendSkillPointAttack()
        }
        btnIncreaseDefense.setOnClickListener {
            viewModel.spendSkillPointDefense()
        }
        btnIncreaseMaxHp.setOnClickListener {
            viewModel.spendSkillPointMaxHp()
        }
    }

    override fun onResume() {
        super.onResume()
        gameLoopHandler.postDelayed(autoFightRunnable, AUTO_FIGHT_INTERVAL_MS)
    }

    override fun onPause() {
        super.onPause()
        gameLoopHandler.removeCallbacks(autoFightRunnable)
        viewModel.saveGame()
    }
}
