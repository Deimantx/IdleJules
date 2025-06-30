package com.example.idlerpg.game

package com.example.idlerpg.game

import com.example.idlerpg.models.GearItem
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

    val availableShopItems: List<GearItem> = listOf(
        GearItem("Wooden Sword", attackBonus = 5, cost = 50),
        GearItem("Leather Vest", defenseBonus = 3, cost = 40),
        GearItem("Iron Sword", attackBonus = 10, cost = 200),
        GearItem("Chainmail Armor", defenseBonus = 8, cost = 180),
        GearItem("Steel Sword", attackBonus = 15, cost = 500),
        GearItem("Plate Armor", defenseBonus = 12, cost = 450)
    )

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
        val monsterType = when (player.level) {
            1, 2 -> Monster("Slime", 30 + player.level * 5, 5 + player.level, 2 + player.level, 10 + player.level * 2, 5 + player.level)
            3, 4 -> Monster("Goblin", 50 + player.level * 7, 8 + player.level, 4 + player.level, 20 + player.level * 3, 10 + player.level * 2)
            5, 6 -> Monster("Orc", 100 + player.level * 10, 15 + player.level, 8 + player.level, 50 + player.level * 5, 25 + player.level * 3)
            else -> Monster("Ogre", 200 + player.level * 12, 25 + player.level, 15 + player.level, 100 + player.level * 7, 50 + player.level * 4)
        }
        currentMonster = monsterType
        combatLogCallback?.invoke("A wild ${currentMonster?.name} appears!")
    }

    fun fightTick() {
        val monster = currentMonster ?: return

        // Player attacks monster
        val playerDamage = max(0, player.effectiveAttack - monster.defense)
        monster.hp -= playerDamage
        combatLogCallback?.invoke("Player attacks ${monster.name} for $playerDamage damage. ${monster.name} HP: ${monster.hp}")

        if (monster.hp <= 0) {
            combatLogCallback?.invoke("${monster.name} defeated!")
            player.experience += monster.experienceReward.toLong()
            player.coins += monster.coinReward
            monsterDefeatedCallback?.invoke(monster)
            checkPlayerLevelUp()
            spawnNewMonster()
            return
        }

        // Monster attacks player
        val monsterDamage = max(0, monster.attack - player.effectiveDefense)
        player.currentHp -= monsterDamage
        combatLogCallback?.invoke("${monster.name} attacks Player for $monsterDamage damage. Player HP: ${player.currentHp}")

        if (player.currentHp <= 0) {
            combatLogCallback?.invoke("Player has been defeated! Game Over (for now).")
            player.currentHp = player.maxHp // Simple reset
        }
    }

    private fun checkPlayerLevelUp() {
        val experienceNeeded = calculateExperienceForNextLevel(player.level)
        if (player.experience >= experienceNeeded) {
            player.level++
            player.experience -= experienceNeeded
            val skillPointsGained = 1 // Award 1 skill point per level
            player.skillPoints += skillPointsGained

            player.maxHp += 10 + (player.level * 2)
            player.currentHp = player.maxHp
            player.attack += 1 // Base stat increase is smaller now, skill points are for customization
            player.defense += 1

            combatLogCallback?.invoke("Player leveled up to Level ${player.level}! Gained $skillPointsGained skill point(s).")
            playerLeveledUpCallback?.invoke(player)
            checkPlayerLevelUp()
        }
    }

    fun calculateExperienceForNextLevel(level: Int): Long {
        return (level.toDouble().pow(1.5) * 100).toLong()
    }

    fun spendSkillPointOnAttack(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.attack++
            combatLogCallback?.invoke("Spent 1 skill point on Attack. Base Attack: ${player.attack}")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Attack.")
        return false
    }

    fun spendSkillPointOnDefense(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.defense++
            combatLogCallback?.invoke("Spent 1 skill point on Defense. Base Defense: ${player.defense}")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Defense.")
        return false
    }

    fun spendSkillPointOnMaxHp(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            val hpIncrease = 10 // Or some other scaling
            player.maxHp += hpIncrease
            player.currentHp += hpIncrease // Also increase current HP
            combatLogCallback?.invoke("Spent 1 skill point on Max HP. Max HP: ${player.maxHp}")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Max HP.")
        return false
    }

    fun buyItem(itemToBuy: GearItem): String {
        if (player.coins < itemToBuy.cost) {
            return "Not enough coins to buy ${itemToBuy.name}."
        }

        player.coins -= itemToBuy.cost
        player.inventory.add(itemToBuy.copy()) // Add a copy to inventory

        // Simplified equip logic: equip if it's a weapon or armor
        // A more robust system would check item type/slot
        if (itemToBuy.attackBonus > 0 && (player.equippedWeapon == null || itemToBuy.attackBonus > player.equippedWeapon!!.attackBonus)) {
            player.equippedWeapon = itemToBuy.copy()
            combatLogCallback?.invoke("Bought and equipped ${itemToBuy.name}.")
        } else if (itemToBuy.defenseBonus > 0 && (player.equippedArmor == null || itemToBuy.defenseBonus > player.equippedArmor!!.defenseBonus)) {
            player.equippedArmor = itemToBuy.copy()
            combatLogCallback?.invoke("Bought and equipped ${itemToBuy.name}.")
        } else {
            combatLogCallback?.invoke("Bought ${itemToBuy.name}. Added to inventory.")
        }

        return "Successfully bought ${itemToBuy.name}."
    }


    // This function is not strictly needed anymore if Player has effectiveAttack/Defense getters
    // but can be kept if direct access to a full Player copy with calculated stats is desired elsewhere.
    fun getPlayerStats(): Player {
         // The copy will now include skillPoints and inventory by default.
         // The effectiveAttack and effectiveDefense are now getters in Player class itself.
        return player.copy()
    }
}
