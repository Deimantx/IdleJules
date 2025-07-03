package com.example.idlerpg.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
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
import com.example.idlerpg.ui.ShopDialogFragment
import com.example.idlerpg.ui.InventoryDialogFragment
import com.example.idlerpg.ui.EquipmentDialogFragment
import com.example.idlerpg.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val COMBAT_TICK_INTERVAL_MS = 200L // 200ms for smooth combat
    }

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
    private lateinit var btnInventory: Button
    private lateinit var btnEquipment: Button

    private val gameLoopHandler = Handler(Looper.getMainLooper())
    private val combatTickRunnable: Runnable = object : Runnable {
        override fun run() {
            try {
                viewModel.combatTick()
                gameLoopHandler.postDelayed(this, COMBAT_TICK_INTERVAL_MS)
            } catch (e: Exception) {
                Log.e(TAG, "Error in combat tick", e)
                // Continue the game loop even if there's an error
                gameLoopHandler.postDelayed(this, COMBAT_TICK_INTERVAL_MS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "Starting MainActivity onCreate")
            setContentView(R.layout.activity_main)
            Log.d(TAG, "Content view set successfully")

            initializeUI()
            Log.d(TAG, "UI initialized successfully")
            
            setupObservers()
            Log.d(TAG, "Observers set up successfully")
            
            setupButtonClickListeners()
            Log.d(TAG, "Button listeners set up successfully")
            
            setupLocationSpinner()
            Log.d(TAG, "Location spinner set up successfully")
            
            setupMonsterSpinner()
            Log.d(TAG, "Monster spinner set up successfully")
            
            Log.d(TAG, "MainActivity onCreate completed successfully")
            
            // Initialize UI with default values to prevent white screen
            initializeDefaultValues()
            
        } catch (e: Exception) {
            Log.e(TAG, "Critical error in onCreate", e)
            // Show a user-friendly error message
            Toast.makeText(this, "Error starting game: ${e.message}", Toast.LENGTH_LONG).show()
            
            // Try to continue with basic functionality
            try {
                setContentView(R.layout.activity_main)
                initializeUIWithFallbacks()
                showEmergencyUI()
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Emergency UI failed", fallbackError)
                finish()
            }
        }
    }

    private fun initializeDefaultValues() {
        try {
            // Set default values for all UI elements to prevent white screen
            tvPlayerLevel?.text = "Level: 1"
            tvPlayerExperience?.text = "XP: 0 / 100"
            tvPlayerHP?.text = "HP: 100 / 100"
            tvPlayerMana?.text = "Mana: 50 / 50"
            tvPlayerAttack?.text = "Attack: 10"
            tvPlayerDefense?.text = "Defense: 5"
            tvPlayerCoins?.text = "Coins: 0"
            tvPlayerStats?.text = "STR: 5 | AGI: 5 | INT: 5 | VIT: 5 | SPR: 5"
            tvPlayerSkillPoints?.text = "Available Skill Points: 0"
            tvPlayerStatusEffects?.text = "Status: None"
            tvCombatLog?.text = "Welcome to Idle RPG! Select a location and monster to begin."
            
            // Set default combat stats
            tvCritRate?.text = "Crit Rate: 2.5%"
            tvCritDamage?.text = "Crit Dmg: 175%"
            tvDodgeChance?.text = "Dodge: 1.5%"
            tvHitChance?.text = "Hit: 87%"
            tvAttackSpeed?.text = "Attack Speed: 2.0s"
            
            // Set default monster info
            tvMonsterName?.text = "Monster: None Selected"
            tvMonsterHP?.text = "HP: -"
            tvMonsterLevel?.text = "Level: -"
            tvMonsterType?.text = "Type: -"
            tvMonsterAbility?.text = "Ability: -"
            
            // Set progress bar
            progressBarExperience?.max = 100
            progressBarExperience?.progress = 0
            
            Log.d(TAG, "Default values initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing default values", e)
        }
    }

    private fun initializeUIWithFallbacks() {
        try {
            // Try to initialize UI elements with safe defaults in case of errors
            val safeViews = mapOf<Int, String>(
                R.id.tvPlayerLevel to "Level: 1",
                R.id.tvPlayerExperience to "XP: 0 / 100",
                R.id.tvPlayerHP to "HP: 100 / 100",
                R.id.tvPlayerMana to "Mana: 50 / 50",
                R.id.tvPlayerAttack to "Attack: 10",
                R.id.tvPlayerDefense to "Defense: 5",
                R.id.tvPlayerCoins to "Coins: 0",
                R.id.tvCombatLog to "Game initializing..."
            )
            
            safeViews.forEach { (id, text) ->
                try {
                    findViewById<TextView>(id)?.text = text
                } catch (e: Exception) {
                    Log.w(TAG, "Could not set text for view $id", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in fallback UI initialization", e)
        }
    }

    private fun showEmergencyUI() {
        try {
            // Don't replace the content view, just show a toast and try to continue
            Toast.makeText(this, "Game loading with basic functionality. Some features may not work.", Toast.LENGTH_LONG).show()
            Log.w(TAG, "Running in emergency mode")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show emergency UI", e)
        }
    }

    private fun initializeUI() {
        try {
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
            btnInventory = findViewById(R.id.btnInventory)
            btnEquipment = findViewById(R.id.btnEquipment)
            
            Log.d(TAG, "All UI elements found and initialized")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing UI elements", e)
            throw e
        }
    }

    private fun setupObservers() {
        try {
            viewModel.playerData.observe(this) { player ->
                try {
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
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating player UI", e)
                }
            }

            viewModel.playerExperienceDisplay.observe(this) { experienceText ->
                try {
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
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating experience UI", e)
                }
            }

            viewModel.monsterData.observe(this) { monster ->
                try {
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
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating monster UI", e)
                }
            }

            viewModel.combatLog.observe(this) { logMessages ->
                try {
                    val htmlText = logMessages.joinToString("<br>")
                    tvCombatLog.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
                    if (tvCombatLog.lineCount > 0) {
                        tvCombatLog.scrollTo(0, 0)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating combat log", e)
                }
            }

            viewModel.toastMessage.observe(this) { event ->
                try {
                    event.getContentIfNotHandled()?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error showing toast message", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up observers", e)
        }
    }

    private fun setupButtonClickListeners() {
        try {
            btnShop.setOnClickListener {
                try {
                    ShopDialogFragment().show(supportFragmentManager, ShopDialogFragment.TAG)
                } catch (e: Exception) {
                    Log.e(TAG, "Error opening shop dialog", e)
                    Toast.makeText(this, "Error opening shop", Toast.LENGTH_SHORT).show()
                }
            }

            btnInventory.setOnClickListener {
                try {
                    InventoryDialogFragment().show(supportFragmentManager, InventoryDialogFragment.TAG)
                } catch (e: Exception) {
                    Log.e(TAG, "Error opening inventory dialog", e)
                    Toast.makeText(this, "Error opening inventory", Toast.LENGTH_SHORT).show()
                }
            }

            btnEquipment.setOnClickListener {
                try {
                    EquipmentDialogFragment().show(supportFragmentManager, EquipmentDialogFragment.TAG)
                } catch (e: Exception) {
                    Log.e(TAG, "Error opening equipment dialog", e)
                    Toast.makeText(this, "Error opening equipment", Toast.LENGTH_SHORT).show()
                }
            }

            // New stat system buttons
            btnIncreaseStrength.setOnClickListener {
                try {
                    viewModel.spendSkillPointStrength()
                } catch (e: Exception) {
                    Log.e(TAG, "Error spending strength point", e)
                }
            }
            btnIncreaseAgility.setOnClickListener {
                try {
                    viewModel.spendSkillPointAgility()
                } catch (e: Exception) {
                    Log.e(TAG, "Error spending agility point", e)
                }
            }
            btnIncreaseIntelligence.setOnClickListener {
                try {
                    viewModel.spendSkillPointIntelligence()
                } catch (e: Exception) {
                    Log.e(TAG, "Error spending intelligence point", e)
                }
            }
            btnIncreaseVitality.setOnClickListener {
                try {
                    viewModel.spendSkillPointVitality()
                } catch (e: Exception) {
                    Log.e(TAG, "Error spending vitality point", e)
                }
            }
            btnIncreaseSpirit.setOnClickListener {
                try {
                    viewModel.spendSkillPointSpirit()
                } catch (e: Exception) {
                    Log.e(TAG, "Error spending spirit point", e)
                }
            }

            // Location selection
            btnSelectLocation.setOnClickListener {
                try {
                    val selectedLocation = spinnerLocationSelect.selectedItem as? String
                    selectedLocation?.let {
                        val location = Location.valueOf(it.uppercase())
                        viewModel.selectLocation(location)
                        updateMonsterSpinner(location)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error selecting location", e)
                    Toast.makeText(this, "Error selecting location", Toast.LENGTH_SHORT).show()
                }
            }

            // Monster selection
            btnSelectMonster.setOnClickListener {
                try {
                    val selectedMonster = spinnerMonsterSelect.selectedItem as? String
                    selectedMonster?.let {
                        viewModel.selectMonster(it)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error selecting monster", e)
                    Toast.makeText(this, "Error selecting monster", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up button listeners", e)
        }
    }

    private fun setupLocationSpinner() {
        try {
            val locations = listOf("Forest", "Mine", "Outskirts")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLocationSelect.adapter = adapter
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up location spinner", e)
        }
    }

    private fun setupMonsterSpinner() {
        try {
            val monsters = listOf("Select a location first")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monsters)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerMonsterSelect.adapter = adapter
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up monster spinner", e)
        }
    }

    private fun updateMonsterSpinner(location: Location) {
        try {
            val monsters = viewModel.getMonstersForLocation(location)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monsters)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerMonsterSelect.adapter = adapter
        } catch (e: Exception) {
            Log.e(TAG, "Error updating monster spinner", e)
        }
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
