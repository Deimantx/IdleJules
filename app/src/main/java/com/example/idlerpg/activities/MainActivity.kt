package com.example.idlerpg.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.idlerpg.R
import com.example.idlerpg.game.Location
import com.example.idlerpg.ui.ShopDialogFragment // Import the dialog
import com.example.idlerpg.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    // Player Info UI
    private lateinit var tvPlayerLevel: TextView
    private lateinit var tvPlayerExperience: TextView
    private lateinit var progressBarExperience: ProgressBar
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

    // Enhanced Combat Stats UI
    private lateinit var tvCritRate: TextView
    private lateinit var tvCritDamage: TextView
    private lateinit var tvDodgeChance: TextView
    private lateinit var tvHitChance: TextView
    private lateinit var tvAttackSpeed: TextView

    // Location Selection UI
    private lateinit var spinnerLocationSelect: Spinner
    private lateinit var btnSelectLocation: Button

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
        setupLocationSpinner()
        setupMonsterSpinner()
    }

    private fun initializeUI() {
        // Player Info
        tvPlayerLevel = findViewById(R.id.tvPlayerLevel)
        tvPlayerExperience = findViewById(R.id.tvPlayerExperience)
        progressBarExperience = findViewById(R.id.progressBarExperience)
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

        // Enhanced Combat Stats
        tvCritRate = findViewById(R.id.tvCritRate)
        tvCritDamage = findViewById(R.id.tvCritDamage)
        tvDodgeChance = findViewById(R.id.tvDodgeChance)
        tvHitChance = findViewById(R.id.tvHitChance)
        tvAttackSpeed = findViewById(R.id.tvAttackSpeed)

        // Location Selection
        spinnerLocationSelect = findViewById(R.id.spinnerLocationSelect)
        btnSelectLocation = findViewById(R.id.btnSelectLocation)

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
                
                // Display enhanced combat stats individually
                tvCritRate.text = "Crit Rate: ${"%.1f".format(it.critRate)}%"
                tvCritDamage.text = "Crit Dmg: ${"%.1f".format(it.critDamageMultiplier * 100)}%"
                tvDodgeChance.text = "Dodge: ${"%.1f".format(it.dodgeChance)}%"
                tvHitChance.text = "Hit: ${"%.1f".format(it.hitChance)}%"
                tvAttackSpeed.text = "Attack Speed: ${"%.1f".format(it.effectiveAttackSpeed / 1000f)}s"

                // Keep the old combined display for backward compatibility (now hidden)
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
            
            // Update progress bar - try to parse the experience text directly
            try {
                var progressPercentage = 75 // Default value
                
                // Parse experience text format like "3020 / 3162"
                if (experienceText.contains(" / ")) {
                    val parts = experienceText.split(" / ")
                    if (parts.size == 2) {
                        val current = parts[0].trim().toIntOrNull() ?: 0
                        val total = parts[1].trim().toIntOrNull() ?: 1
                        
                        progressPercentage = ((current.toFloat() / total.toFloat()) * 100).toInt()
                    } else {
                        // Fallback calculation using player data
                        viewModel.playerData.value?.let { player ->
                            val currentExp = player.experience
                            val expForCurrentLevel = player.getExperienceForLevel(player.level)
                            val expForNextLevel = player.getExperienceForLevel(player.level + 1)
                            val expInCurrentLevel = maxOf(0, currentExp - expForCurrentLevel)
                            val expNeededForNextLevel = maxOf(1, expForNextLevel - expForCurrentLevel)
                            
                            progressPercentage = ((expInCurrentLevel.toFloat() / expNeededForNextLevel.toFloat()) * 100).toInt()
                        }
                    }
                }
                
                // Update the progress bar
                progressBarExperience.max = 100
                progressBarExperience.progress = progressPercentage.coerceIn(0, 100)
                
            } catch (e: Exception) {
                // Fallback - set to 75% visible
                progressBarExperience.max = 100
                progressBarExperience.progress = 75
            }
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
            val htmlText = logMessages.joinToString("<br>")
            tvCombatLog.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
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

        // Location selection
        btnSelectLocation.setOnClickListener {
            val selectedLocation = spinnerLocationSelect.selectedItem as? String
            selectedLocation?.let {
                val location = Location.valueOf(it.uppercase())
                viewModel.selectLocation(location)
                updateMonsterSpinner(location)
            }
        }

        // Monster selection
        btnSelectMonster.setOnClickListener {
            val selectedMonster = spinnerMonsterSelect.selectedItem as? String
            selectedMonster?.let {
                viewModel.selectMonster(it)
            }
        }
    }

    private fun setupLocationSpinner() {
        val locations = listOf("Forest", "Mine", "Outskirts")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocationSelect.adapter = adapter
    }

    private fun setupMonsterSpinner() {
        val monsters = listOf("Select a location first")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monsters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonsterSelect.adapter = adapter
    }

    private fun updateMonsterSpinner(location: Location) {
        val monsters = viewModel.getMonstersForLocation(location)
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
