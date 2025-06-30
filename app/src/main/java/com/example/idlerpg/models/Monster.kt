package com.example.idlerpg.models

data class Monster(
    val name: String,
    var hp: Int,
    val attack: Int,
    val defense: Int,
    val experienceReward: Int,
    val coinReward: Int
)
