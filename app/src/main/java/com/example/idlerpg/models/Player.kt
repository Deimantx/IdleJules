package com.example.idlerpg.models

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
    var inventory: MutableList<GearItem> = mutableListOf()
) {
    // Effective stats including gear bonuses
    val effectiveAttack: Int
        get() = attack + (equippedWeapon?.attackBonus ?: 0)

    val effectiveDefense: Int
        get() = defense + (equippedArmor?.defenseBonus ?: 0)
}
