package com.example.idlerpg.models

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
        get() = (attack + strength * 2 + (equippedWeapon?.attackBonus ?: 0))

    val effectiveDefense: Int
        get() = defense + (equippedArmor?.defenseBonus ?: 0)
    
    val effectiveMaxHp: Int
        get() = maxHp + (vitality * 10)
        
    val effectiveMaxMana: Int
        get() = maxMana + (intelligence * 5) + (spirit * 8)
    
    // Combat stats derived from attributes
    val critRate: Float
        get() = (agility * 0.5f).coerceAtMost(50f) // Max 50% crit rate
        
    val critDamageMultiplier: Float
        get() = 1.5f + (strength * 0.05f) // Base 150% + 5% per STR
        
    val dodgeChance: Float
        get() = (agility * 0.3f).coerceAtMost(25f) // Max 25% dodge chance
        
    val hitChance: Float
        get() = 85f + (agility * 0.4f).coerceAtMost(95f) // Base 85%, max 95%
        
    // Attack speed affected by agility and weapon - lower is faster
    val effectiveAttackSpeed: Float
        get() = (baseAttackSpeed - (agility * 50f) + (equippedWeapon?.attackSpeedBonus ?: 0f)).coerceAtLeast(500f) // Min 0.5 seconds
    
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
    
    // Check if player can attack based on attack speed
    fun canAttack(currentTime: Long): Boolean {
        return currentTime - lastAttackTime >= effectiveAttackSpeed
    }
    
    // Update last attack time
    fun updateAttackTime(currentTime: Long) {
        lastAttackTime = currentTime
    }
}
