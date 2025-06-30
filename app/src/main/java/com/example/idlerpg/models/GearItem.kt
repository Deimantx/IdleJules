package com.example.idlerpg.models

data class GearItem(
    val name: String,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val cost: Int = 0
)
