package com.example.idlerpg.models

data class StatusEffect(
    val type: String,
    var duration: Int, // Remaining turns
    val value: Int // Effect strength (damage per turn, etc.)
)

data class Player(
    var level: Int = 1,
    var experience: Long = 0L,
    var baseHp: Int = 100, // Base HP before STR/HP bonuses
    var currentHp: Int = 100,
    
    // Core Stats
    var strength: Int = 10, // STR - damage, crit damage
    var agility: Int = 10,  // AGI - hit chance, dodge, crit rate, attack speed
    var intelligence: Int = 10, // INT - mana, magic damage (placeholder)
    var vitality: Int = 10, // HP - health bonus
    var mana: Int = 10,     // MANA - mana bonus (placeholder)
    
    // Legacy stats (kept for compatibility)
    var attack: Int = 10,
    var defense: Int = 5,
    
    var coins: Int = 0,
    var equippedWeapon: GearItem? = null,
    var equippedArmor: GearItem? = null,
    var skillPoints: Int = 0,
    var inventory: MutableList<GearItem> = mutableListOf(),
    var statusEffects: MutableList<StatusEffect> = mutableListOf(),
    var isStunned: Boolean = false,
    
    // Combat timing
    var lastAttackTime: Long = 0L
) {
    // Calculated Stats
    val maxHp: Int
        get() = baseHp + (vitality * 5) + (strength * 2)
    
    val maxMana: Int
        get() = 50 + (intelligence * 3) + (mana * 2)
    
    val effectiveAttack: Int
        get() = strength + (equippedWeapon?.attackBonus ?: 0)

    val effectiveDefense: Int
        get() = defense + (equippedArmor?.defenseBonus ?: 0)
    
    // Attack Speed (attacks per second * 1000 for milliseconds)
    val attackSpeed: Long
        get() {
            val baseSpeed = 2000L // 2 seconds base
            val agilityBonus = agility * 50L // 50ms faster per AGI point
            val weaponSpeed = equippedWeapon?.attackSpeed ?: 0L
            return (baseSpeed - agilityBonus - weaponSpeed).coerceAtLeast(500L) // Min 0.5 seconds
        }
    
    // Combat Stats
    val criticalChance: Int
        get() = 5 + (agility / 2) // Base 5% + 0.5% per AGI
    
    val criticalDamage: Int
        get() = 150 + (strength * 2) // Base 150% + 2% per STR
    
    val hitChance: Int
        get() = 85 + agility // Base 85% + 1% per AGI (capped at 95%)
    
    val dodgeChance: Int
        get() = 5 + (agility / 3) // Base 5% + 0.33% per AGI
    
    // Check if player can attack (based on attack speed)
    fun canAttack(): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime - lastAttackTime >= attackSpeed
    }
    
    // Mark that player attacked
    fun performAttack() {
        lastAttackTime = System.currentTimeMillis()
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
}
