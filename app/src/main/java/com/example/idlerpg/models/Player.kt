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
    var attack: Int = 10, // Base attack
    var defense: Int = 5, // Base defense
    var coins: Int = 0,
    var equippedWeapon: GearItem? = null,
    var equippedArmor: GearItem? = null,
    var skillPoints: Int = 0,
    var inventory: MutableList<GearItem> = mutableListOf(),
    var statusEffects: MutableList<StatusEffect> = mutableListOf(),
    var isStunned: Boolean = false
) {
    // Effective stats including gear bonuses
    val effectiveAttack: Int
        get() = attack + (equippedWeapon?.attackBonus ?: 0)

    val effectiveDefense: Int
        get() = defense + (equippedArmor?.defenseBonus ?: 0)
    
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
