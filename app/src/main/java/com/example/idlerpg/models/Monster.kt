package com.example.idlerpg.models

enum class MonsterType {
    BEAST,
    UNDEAD,
    ELEMENTAL,
    DEMON,
    HUMANOID,
    DRAGON,
    CONSTRUCT,
    PLANT
}

enum class MonsterAbility {
    NONE,
    REGENERATION,    // Heals a small amount each turn
    POISON_ATTACK,   // Deals damage over time
    CRITICAL_STRIKE, // Chance for double damage
    ARMOR_PIERCE,    // Ignores some defense
    LIFE_STEAL,      // Heals when dealing damage
    BERSERKER_RAGE,  // Attack increases when low HP
    MAGIC_SHIELD,    // Reduces incoming damage
    STUN_CHANCE,     // Chance to skip player's turn
    DOUBLE_ATTACK    // Attacks twice in one turn
}

data class Monster(
    val name: String,
    val type: MonsterType,
    var hp: Int,
    val maxHp: Int = hp,
    val attack: Int,
    val defense: Int,
    val experienceReward: Int,
    val coinReward: Int,
    val ability: MonsterAbility = MonsterAbility.NONE,
    val abilityPower: Int = 0, // Strength of the ability (healing amount, damage bonus, etc.)
    val abilityCooldown: Int = 0, // Turns between ability use (0 = every turn)
    var currentCooldown: Int = 0, // Current cooldown counter
    val description: String = "",
    val level: Int = 1,
    
    // New combat stats
    val attackSpeed: Float = 2500f, // Milliseconds between attacks - larger monsters are slower
    var lastAttackTime: Long = 0L,
    val critRate: Float = 5f, // Base crit rate for monsters
    val critDamage: Float = 1.8f, // Crit damage multiplier
    val dodgeChance: Float = 2f, // Base dodge chance
    val hitChance: Float = 90f // Base hit chance
) {
    // Calculate if the monster can use its ability this turn
    fun canUseAbility(): Boolean = currentCooldown <= 0
    
    // Use the ability and set cooldown
    fun useAbility() {
        currentCooldown = abilityCooldown
    }
    
    // Reduce cooldown each turn
    fun tickCooldown() {
        if (currentCooldown > 0) currentCooldown--
    }
    
    // Check if monster can attack based on attack speed
    fun canAttack(currentTime: Long): Boolean {
        return currentTime - lastAttackTime >= attackSpeed
    }
    
    // Update last attack time
    fun updateAttackTime(currentTime: Long) {
        lastAttackTime = currentTime
    }
    
    // Get ability description for UI
    fun getAbilityDescription(): String {
        return when (ability) {
            MonsterAbility.NONE -> ""
            MonsterAbility.REGENERATION -> "Regenerates $abilityPower HP each turn"
            MonsterAbility.POISON_ATTACK -> "Poison attacks deal $abilityPower damage over time"
            MonsterAbility.CRITICAL_STRIKE -> "${abilityPower}% chance for critical hits"
            MonsterAbility.ARMOR_PIERCE -> "Ignores $abilityPower points of armor"
            MonsterAbility.LIFE_STEAL -> "Steals $abilityPower% of damage as health"
            MonsterAbility.BERSERKER_RAGE -> "Attack increases by $abilityPower when below 50% HP"
            MonsterAbility.MAGIC_SHIELD -> "Reduces all damage by $abilityPower points"
            MonsterAbility.STUN_CHANCE -> "${abilityPower}% chance to stun player"
            MonsterAbility.DOUBLE_ATTACK -> "Attacks twice every $abilityCooldown turns"
        }
    }
}
