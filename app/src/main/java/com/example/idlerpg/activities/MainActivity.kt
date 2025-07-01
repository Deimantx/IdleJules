package com.example.idlerpg.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Spinner
import android.widget.ArrayAdapter
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
    private lateinit var tvPlayerMana: TextView
    private lateinit var tvPlayerAttack: TextView
    private lateinit var tvPlayerDefense: TextView
    private lateinit var tvPlayerCoins: TextView

    // New Stat System UI
    private lateinit var tvPlayerStats: TextView
    private lateinit var tvPlayerCombatStats: TextView
    private lateinit var tvPlayerSkillPoints: TextView
    private lateinit var btnIncreaseStrength: Button
    private lateinit var btnIncreaseAgility: Button
    private lateinit var btnIncreaseIntelligence: Button
    private lateinit var btnIncreaseVitality: Button
    private lateinit var btnIncreaseSpirit: Button

    // Monster Selection UI
    private lateinit var spinnerMonsterSelect: Spinner
    private lateinit var btnSelectMonster: Button

    // Monster Info UI
    private lateinit var tvMonsterName: TextView
    private lateinit var tvMonsterHP: TextView
    private lateinit var tvMonsterType: TextView
    private lateinit var tvMonsterAbility: TextView
    private lateinit var tvMonsterLevel: TextView

    // Player Status UI
    private lateinit var tvPlayerStatusEffects: TextView

    // Other UI
    private lateinit var tvCombatLog: TextView
    private lateinit var btnShop: Button

    private val gameLoopHandler = Handler(Looper.getMainLooper())
    private val combatTickRunnable: Runnable = object : Runnable {
        override fun run() {
            viewModel.combatTick()
            gameLoopHandler.postDelayed(this, COMBAT_TICK_INTERVAL_MS)
        }
    }

    companion object {
        private const val COMBAT_TICK_INTERVAL_MS = 200L // 200ms for smooth combat
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUI()
        setupObservers()
        setupButtonClickListeners()
        setupMonsterSpinner()
    }

    private fun initializeUI() {
        // Player Info
        tvPlayerLevel = findViewById(R.id.tvPlayerLevel)
        tvPlayerExperience = findViewById(R.id.tvPlayerExperience)
        tvPlayerHP = findViewById(R.id.tvPlayerHP)
        tvPlayerMana = findViewById(R.id.tvPlayerMana)
        tvPlayerAttack = findViewById(R.id.tvPlayerAttack)
        tvPlayerDefense = findViewById(R.id.tvPlayerDefense)
        tvPlayerCoins = findViewById(R.id.tvPlayerCoins)

        // New Stats
        tvPlayerStats = findViewById(R.id.tvPlayerStats)
        tvPlayerCombatStats = findViewById(R.id.tvPlayerCombatStats)
        tvPlayerSkillPoints = findViewById(R.id.tvPlayerSkillPoints)
        btnIncreaseStrength = findViewById(R.id.btnIncreaseStrength)
        btnIncreaseAgility = findViewById(R.id.btnIncreaseAgility)
        btnIncreaseIntelligence = findViewById(R.id.btnIncreaseIntelligence)
        btnIncreaseVitality = findViewById(R.id.btnIncreaseVitality)
        btnIncreaseSpirit = findViewById(R.id.btnIncreaseSpirit)

        // Monster Selection
        spinnerMonsterSelect = findViewById(R.id.spinnerMonsterSelect)
        btnSelectMonster = findViewById(R.id.btnSelectMonster)

        // Monster Info
        tvMonsterName = findViewById(R.id.tvMonsterName)
        tvMonsterHP = findViewById(R.id.tvMonsterHP)
        tvMonsterType = findViewById(R.id.tvMonsterType)
        tvMonsterAbility = findViewById(R.id.tvMonsterAbility)
        tvMonsterLevel = findViewById(R.id.tvMonsterLevel)

        // Player Status
        tvPlayerStatusEffects = findViewById(R.id.tvPlayerStatusEffects)

        // Other
        tvCombatLog = findViewById(R.id.tvCombatLog)
        tvCombatLog.movementMethod = ScrollingMovementMethod()

        btnShop = findViewById(R.id.btnShop)
    }

    private fun setupObservers() {
        viewModel.playerData.observe(this) { player ->
            player?.let {
                tvPlayerLevel.text = "Level: ${it.level}"
                tvPlayerHP.text = "HP: ${it.currentHp} / ${it.effectiveMaxHp}"
                tvPlayerMana.text = "Mana: ${it.currentMana} / ${it.effectiveMaxMana}"
                tvPlayerAttack.text = "Attack: ${it.effectiveAttack}"
                tvPlayerDefense.text = "Defense: ${it.effectiveDefense}"
                tvPlayerCoins.text = "Coins: ${it.coins}"
                tvPlayerSkillPoints.text = "Available Skill Points: ${it.skillPoints}"

                // Display new stats
                tvPlayerStats.text = "STR: ${it.strength} | AGI: ${it.agility} | INT: ${it.intelligence} | VIT: ${it.vitality} | SPR: ${it.spirit}"
                
                // Display combat stats
                val combatStats = "Crit Rate: ${"%.1f".format(it.critRate)}% | " +
                        "Crit Dmg: ${"%.1f".format(it.critDamageMultiplier * 100)}% | " +
                        "Dodge: ${"%.1f".format(it.dodgeChance)}% | " +
                        "Hit: ${"%.1f".format(it.hitChance)}% | " +
                        "Speed: ${"%.1f".format(it.effectiveAttackSpeed / 1000f)}s"
                tvPlayerCombatStats.text = combatStats

                // Enable/disable skill point buttons
                val hasSkillPoints = it.skillPoints > 0
                btnIncreaseStrength.isEnabled = hasSkillPoints
                btnIncreaseAgility.isEnabled = hasSkillPoints
                btnIncreaseIntelligence.isEnabled = hasSkillPoints
                btnIncreaseVitality.isEnabled = hasSkillPoints
                btnIncreaseSpirit.isEnabled = hasSkillPoints

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
                tvMonsterLevel.text = "Level: ${it.level}"
                tvMonsterType.text = "Type: ${it.type.name.lowercase().replaceFirstChar { char -> char.uppercase() }}"
                val abilityText = if (it.ability.name != "NONE") {
                    it.getAbilityDescription()
                } else {
                    "None"
                }
                tvMonsterAbility.text = "Ability: $abilityText"
            } ?: run {
                tvMonsterName.text = "Monster: None Selected"
                tvMonsterHP.text = "HP: -"
                tvMonsterLevel.text = "Level: -"
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
        btnShop.setOnClickListener {
            ShopDialogFragment().show(supportFragmentManager, ShopDialogFragment.TAG)
        }

        // New stat system buttons
        btnIncreaseStrength.setOnClickListener {
            viewModel.spendSkillPointStrength()
        }
        btnIncreaseAgility.setOnClickListener {
            viewModel.spendSkillPointAgility()
        }
        btnIncreaseIntelligence.setOnClickListener {
            viewModel.spendSkillPointIntelligence()
        }
        btnIncreaseVitality.setOnClickListener {
            viewModel.spendSkillPointVitality()
        }
        btnIncreaseSpirit.setOnClickListener {
            viewModel.spendSkillPointSpirit()
        }

        // Monster selection
        btnSelectMonster.setOnClickListener {
            val selectedMonster = spinnerMonsterSelect.selectedItem as? String
            selectedMonster?.let {
                viewModel.selectMonster(it)
            }
        }
    }

    private fun setupMonsterSpinner() {
        val monsters = viewModel.getAvailableMonsters()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monsters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonsterSelect.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        gameLoopHandler.postDelayed(combatTickRunnable, COMBAT_TICK_INTERVAL_MS)
    }

    override fun onPause() {
        super.onPause()
        gameLoopHandler.removeCallbacks(combatTickRunnable)
        viewModel.saveGame()
    }
}
