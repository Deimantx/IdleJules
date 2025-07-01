package com.example.idlerpg.models

data class GearItem(
    val name: String,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val attackSpeed: Long = 0, // Attack speed bonus in milliseconds (negative = faster)
    val cost: Int = 0
)
