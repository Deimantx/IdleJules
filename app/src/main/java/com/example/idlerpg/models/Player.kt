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
    var equippedWeapon: GearItem? = null,
    var equippedArmor: GearItem? = null,
    var equippedShield: GearItem? = null,
    var equippedAmulet: GearItem? = null,
    var equippedRing: GearItem? = null,
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
    // Effective stats including gear bonuses and stat-based calculations
    val effectiveAttack: Int
        get() = (attack + strength * 2 + 
                (equippedWeapon?.attackBonus ?: 0) +
                (equippedShield?.attackBonus ?: 0) +
                (equippedAmulet?.attackBonus ?: 0) +
                (equippedRing?.attackBonus ?: 0))

    val effectiveDefense: Int
        get() = defense + 
                (equippedArmor?.defenseBonus ?: 0) +
                (equippedShield?.defenseBonus ?: 0) +
                (equippedAmulet?.defenseBonus ?: 0) +
                (equippedRing?.defenseBonus ?: 0)
    
    val effectiveMaxHp: Int
        get() = maxHp + (vitality * 10) +
                (equippedArmor?.hpBonus ?: 0) +
                (equippedShield?.hpBonus ?: 0) +
                (equippedAmulet?.hpBonus ?: 0) +
                (equippedRing?.hpBonus ?: 0)
        
    val effectiveMaxMana: Int
        get() = maxMana + (intelligence * 5) + (spirit * 8) +
                (equippedArmor?.manaBonus ?: 0) +
                (equippedShield?.manaBonus ?: 0) +
                (equippedAmulet?.manaBonus ?: 0) +
                (equippedRing?.manaBonus ?: 0)
    
    // Combat stats derived from attributes
    val critRate: Float
        get() = ((agility * 0.5f) +
                (equippedWeapon?.critRateBonus ?: 0f) +
                (equippedShield?.critRateBonus ?: 0f) +
                (equippedAmulet?.critRateBonus ?: 0f) +
                (equippedRing?.critRateBonus ?: 0f)).coerceAtMost(50f) // Max 50% crit rate
        
    val critDamageMultiplier: Float
        get() = 1.5f + (strength * 0.05f) +
                (equippedWeapon?.critDamageBonus ?: 0f) +
                (equippedShield?.critDamageBonus ?: 0f) +
                (equippedAmulet?.critDamageBonus ?: 0f) +
                (equippedRing?.critDamageBonus ?: 0f) // Base 150% + 5% per STR
        
    val dodgeChance: Float
        get() = ((agility * 0.3f) +
                (equippedArmor?.dodgeBonus ?: 0f) +
                (equippedShield?.dodgeBonus ?: 0f) +
                (equippedAmulet?.dodgeBonus ?: 0f) +
                (equippedRing?.dodgeBonus ?: 0f)).coerceAtMost(25f) // Max 25% dodge chance
        
    val hitChance: Float
        get() = (85f + (agility * 0.4f) +
                (equippedWeapon?.hitBonus ?: 0f) +
                (equippedShield?.hitBonus ?: 0f) +
                (equippedAmulet?.hitBonus ?: 0f) +
                (equippedRing?.hitBonus ?: 0f)).coerceAtMost(95f) // Base 85%, max 95%
        
    // Attack speed affected by agility and weapon - lower is faster
    val effectiveAttackSpeed: Float
        get() = (baseAttackSpeed + 
                (equippedWeapon?.attackSpeedBonus ?: 0f) +
                (equippedShield?.attackSpeedBonus ?: 0f) +
                (equippedAmulet?.attackSpeedBonus ?: 0f) +
                (equippedRing?.attackSpeedBonus ?: 0f)).coerceAtLeast(500f) // Min 0.5 seconds
    
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
