package com.example.idlerpg.models

data class GearItem(
    val name: String,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val cost: Int = 0,
    val attackSpeedBonus: Float = 0f, // Reduces attack time (negative values slow down)
    val critRateBonus: Float = 0f, // Bonus crit rate
    val critDamageBonus: Float = 0f, // Bonus crit damage multiplier
    val dodgeBonus: Float = 0f, // Bonus dodge chance
    val hitBonus: Float = 0f // Bonus hit chance
)
