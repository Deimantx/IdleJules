package com.example.idlerpg.models

import kotlin.math.pow

data class StatusEffect(
    val type: String,
    var duration: Int, // Remaining turns
    val value: Int // Effect strength (damage per turn, etc.)
)

data class Player(
    var level: Int = 1,
    var experience: Long = 0L,
    var maxHp: Int = 100,
    var currentHp: Int = 100,
    var maxMana: Int = 50,
    var currentMana: Int = 50,
    var attack: Int = 10, // Base attack - now derived from STR
    var defense: Int = 5, // Base defense 
    var coins: Int = 0,
    
    // Equipment slots - existing
    var equippedWeapon: GearItem? = null,
    var equippedArmor: GearItem? = null,
    var equippedShield: GearItem? = null,
    var equippedAmulet: GearItem? = null,
    var equippedRing: GearItem? = null,
    
    // Equipment slots - new
    var equippedBelt: GearItem? = null,
    var equippedGloves: GearItem? = null,
    var equippedBoots: GearItem? = null,
    var equippedCloak: GearItem? = null,
    
    var skillPoints: Int = 0,
    var inventory: MutableList<GearItem> = mutableListOf(),
    var statusEffects: MutableList<StatusEffect> = mutableListOf(),
    var isStunned: Boolean = false,
    
    // New stat system
    var strength: Int = 5,      // STR - damage increase, crit damage increase
    var agility: Int = 5,       // AGI - hit chance, dodge chance, crit rate, attack speed
    var intelligence: Int = 5,  // INT - mana, magic damage (placeholder for now)
    var vitality: Int = 5,      // HP - health increase
    var spirit: Int = 5,        // MANA - mana increase (placeholder for now)
    
    // Attack speed - lower is faster (milliseconds between attacks)
    var baseAttackSpeed: Float = 2000f, // 2 seconds base
    var lastAttackTime: Long = 0L
) {
    // Helper function to get all equipment items
    private fun getAllEquipment(): List<GearItem> {
        return listOfNotNull(
            equippedWeapon,
            equippedArmor,
            equippedShield,
            equippedAmulet,
            equippedRing,
            equippedBelt,
            equippedGloves,
            equippedBoots,
            equippedCloak
        )
    }
    
    // Effective stats including gear bonuses and stat-based calculations
    val effectiveStrength: Int
        get() = strength + getAllEquipment().sumOf { it.strengthBonus }
    
    val effectiveAgility: Int
        get() = agility + getAllEquipment().sumOf { it.agilityBonus }
    
    val effectiveIntelligence: Int
        get() = intelligence + getAllEquipment().sumOf { it.intelligenceBonus }
    
    val effectiveVitality: Int
        get() = vitality + getAllEquipment().sumOf { it.vitalityBonus }
    
    val effectiveSpirit: Int
        get() = spirit + getAllEquipment().sumOf { it.spiritBonus }
    
    val effectiveAttack: Int
        get() = (attack + effectiveStrength * 2 + 
                getAllEquipment().sumOf { it.attackBonus })

    val effectiveDefense: Int
        get() = defense + getAllEquipment().sumOf { it.defenseBonus }
    
    val effectiveMaxHp: Int
        get() = maxHp + (effectiveVitality * 10) +
                getAllEquipment().sumOf { it.hpBonus }
        
    val effectiveMaxMana: Int
        get() = maxMana + (effectiveIntelligence * 5) + (effectiveSpirit * 8) +
                getAllEquipment().sumOf { it.manaBonus }
    
    // Combat stats derived from attributes
    val critRate: Float
        get() = ((effectiveAgility * 0.5f) +
                getAllEquipment().sumOf { it.critRateBonus }).coerceAtMost(50f) // Max 50% crit rate
        
    val critDamageMultiplier: Float
        get() = 1.5f + (effectiveStrength * 0.05f) +
                getAllEquipment().sumOf { it.critDamageBonus } // Base 150% + 5% per STR
        
    val dodgeChance: Float
        get() = ((effectiveAgility * 0.3f) +
                getAllEquipment().sumOf { it.dodgeBonus }).coerceAtMost(25f) // Max 25% dodge chance
        
    val hitChance: Float
        get() = (85f + (effectiveAgility * 0.4f) +
                getAllEquipment().sumOf { it.hitBonus }).coerceAtMost(95f) // Base 85%, max 95%
        
    // Attack speed affected by agility and equipment - lower is faster
    val effectiveAttackSpeed: Float
        get() = (baseAttackSpeed + 
                getAllEquipment().sumOf { it.attackSpeedBonus }).coerceAtLeast(500f) // Min 0.5 seconds
    
    // New combat properties from equipment
    val totalArmorPiercing: Int
        get() = getAllEquipment().sumOf { it.armorPiercing }
    
    val totalLifeSteal: Float
        get() = getAllEquipment().sumOf { it.lifeSteal }
    
    val totalManaSteal: Float
        get() = getAllEquipment().sumOf { it.manaSteal }
    
    val totalElementalDamage: Int
        get() = getAllEquipment().sumOf { it.elementalDamage }
    
    // Helper function to get all equipped items of a specific set
    fun getEquippedSetItems(setName: String): List<GearItem> {
        return getAllEquipment().filter { it.setName == setName && it.setName.isNotEmpty() }
    }
    
    // Check if player has complete set bonus
    fun hasCompleteSet(setName: String, requiredPieces: Int): Boolean {
        return getEquippedSetItems(setName).size >= requiredPieces
    }
    
    // Add a status effect
    fun addStatusEffect(type: String, duration: Int, value: Int) {
        // Remove existing effect of the same type
        statusEffects.removeAll { it.type == type }
        // Add new effect
        statusEffects.add(StatusEffect(type, duration, value))
    }
    
    // Apply status effects and reduce their duration
    fun processStatusEffects(): List<String> {
        val messages = mutableListOf<String>()
        val effectsToRemove = mutableListOf<StatusEffect>()
        
        for (effect in statusEffects) {
            when (effect.type) {
                "poison", "burn" -> {
                    currentHp -= effect.value
                    messages.add("Player takes ${effect.value} ${effect.type} damage!")
                    if (currentHp <= 0) {
                        currentHp = 0
                        messages.add("Player has been defeated by ${effect.type}!")
                    }
                }
            }
            
            effect.duration--
            if (effect.duration <= 0) {
                effectsToRemove.add(effect)
                messages.add("${effect.type.capitalize()} effect wears off.")
            }
        }
        
        statusEffects.removeAll(effectsToRemove)
        
        // Reset stun status (it only lasts one turn)
        if (isStunned) {
            isStunned = false
            messages.add("Player recovers from stun.")
        }
        
        return messages
    }

    fun getExperienceForLevel(level: Int): Long {
        // Implement your logic to calculate experience for a given level
        // This is just a placeholder example, replace it with your actual formula
        if (level <= 0) return 0
        return (level * level * 100).toLong() // Example: 100 * level^2
    }
    
    // Get status effects description for UI
    fun getStatusEffectsDescription(): String {
        val descriptions = mutableListOf<String>()
        
        if (isStunned) {
            descriptions.add("Stunned")
        }
        
        for (effect in statusEffects) {
            descriptions.add("${effect.type.capitalize()} (${effect.duration} turns)")
        }
        
        return if (descriptions.isEmpty()) "None" else descriptions.joinToString(", ")
    }
    
    // Check if player can attack based on attack speed
    fun canAttack(currentTime: Long): Boolean {
        return currentTime - lastAttackTime >= effectiveAttackSpeed
    }
    
    // Update last attack time
    fun updateAttackTime(currentTime: Long) {
        lastAttackTime = currentTime
    }
}
