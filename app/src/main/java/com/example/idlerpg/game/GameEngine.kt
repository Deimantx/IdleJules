package com.example.idlerpg.game

package com.example.idlerpg.game

import com.example.idlerpg.models.Monster
import com.example.idlerpg.models.Player
import kotlin.math.max
import kotlin.math.pow

class GameEngine {
    var player: Player = Player() // Initialize with a default player
    var currentMonster: Monster? = null

    private var monsterDefeatedCallback: ((Monster) -> Unit)? = null
    private var playerLeveledUpCallback: ((Player) -> Unit)? = null
    private var combatLogCallback: ((String) -> Unit)? = null

    init {
        spawnNewMonster()
    }

    fun setOnMonsterDefeated(callback: (Monster) -> Unit) {
        monsterDefeatedCallback = callback
    }

    fun setOnPlayerLeveledUp(callback: (Player) -> Unit) {
        playerLeveledUpCallback = callback
    }

    fun setOnCombatLog(callback: (String) -> Unit) {
        combatLogCallback = callback
    }

    private fun spawnNewMonster() {
        // Simple monster progression based on player level
        val monsterType = when (player.level) {
            1, 2 -> Monster("Slime", 30 + player.level * 5, 5 + player.level, 2 + player.level, 10 + player.level * 2, 5 + player.level)
            3, 4 -> Monster("Goblin", 50 + player.level * 7, 8 + player.level, 4 + player.level, 20 + player.level * 3, 10 + player.level * 2)
            else -> Monster("Orc", 100 + player.level * 10, 15 + player.level, 8 + player.level, 50 + player.level * 5, 25 + player.level * 3)
        }
        currentMonster = monsterType
        combatLogCallback?.invoke("A wild ${currentMonster?.name} appears!")
    }

    fun fightTick() {
        val monster = currentMonster ?: return

        // Player attacks monster
        val playerDamage = max(0, player.attack + (player.equippedWeapon?.attackBonus ?: 0) - monster.defense)
        monster.hp -= playerDamage
        combatLogCallback?.invoke("Player attacks ${monster.name} for $playerDamage damage. ${monster.name} HP: ${monster.hp}")

        if (monster.hp <= 0) {
            combatLogCallback?.invoke("${monster.name} defeated!")
            player.experience += monster.experienceReward.toLong()
            player.coins += monster.coinReward
            monsterDefeatedCallback?.invoke(monster)
            checkPlayerLevelUp()
            spawnNewMonster()
            return // Monster is defeated, it doesn't attack back
        }

        // Monster attacks player
        val monsterDamage = max(0, monster.attack - (player.defense + (player.equippedArmor?.defenseBonus ?: 0)))
        player.currentHp -= monsterDamage
        combatLogCallback?.invoke("${monster.name} attacks Player for $monsterDamage damage. Player HP: ${player.currentHp}")

        if (player.currentHp <= 0) {
            combatLogCallback?.invoke("Player has been defeated! Game Over (for now).")
            // TODO: Implement game over logic (e.g., reset player HP, penalty, etc.)
            player.currentHp = player.maxHp // Simple reset for now
        }
    }

    private fun checkPlayerLevelUp() {
        val experienceNeeded = calculateExperienceForNextLevel(player.level)
        if (player.experience >= experienceNeeded) {
            player.level++
            player.experience -= experienceNeeded // Or player.experience = 0 if levels reset exp

            // Stat increases on level up
            player.maxHp += 10 + (player.level * 2)
            player.currentHp = player.maxHp // Full heal on level up
            player.attack += 2 + (player.level / 2)
            player.defense += 1 + (player.level / 3)

            combatLogCallback?.invoke("Player leveled up to Level ${player.level}!")
            playerLeveledUpCallback?.invoke(player)
            checkPlayerLevelUp() // Check if multiple level ups occurred
        }
    }

    fun calculateExperienceForNextLevel(level: Int): Long {
        // Example formula: (level^1.5) * 100
        return (level.toDouble().pow(1.5) * 100).toLong()
    }

    fun getPlayerStats(): Player {
        return player.copy( // Return a copy to ensure encapsulation if needed, though direct modification is used here for simplicity
            attack = player.attack + (player.equippedWeapon?.attackBonus ?: 0),
            defense = player.defense + (player.equippedArmor?.defenseBonus ?: 0)
        )
    }
}
