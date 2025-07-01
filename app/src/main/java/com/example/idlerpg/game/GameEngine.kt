package com.example.idlerpg.game

import com.example.idlerpg.models.GearItem
import com.example.idlerpg.models.Monster
import com.example.idlerpg.models.MonsterAbility
import com.example.idlerpg.models.Player
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

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
        currentMonster = MonsterFactory.createRandomMonster(player.level)
        currentMonster?.let { monster ->
            combatLogCallback?.invoke("A wild ${monster.name} appears!")
            if (monster.ability != MonsterAbility.NONE) {
                combatLogCallback?.invoke("${monster.name}: ${monster.getAbilityDescription()}")
            }
        }
    }

    fun fightTick() {
        val monster = currentMonster ?: return

        // Process player status effects first
        val statusMessages = player.processStatusEffects()
        statusMessages.forEach { combatLogCallback?.invoke(it) }

        // Check if player is stunned (skip their turn)
        if (!player.isStunned) {
            // Player attacks monster
            val playerDamage = calculatePlayerDamage(monster)
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
        } else {
            combatLogCallback?.invoke("Player is stunned and cannot attack!")
        }

        // Monster attacks player (with abilities)
        performMonsterAttack(monster)

        // Tick monster cooldowns
        monster.tickCooldown()

        if (player.currentHp <= 0) {
            combatLogCallback?.invoke("Player has been defeated! Game Over (for now).")
            player.currentHp = player.maxHp // Simple reset
            player.statusEffects.clear() // Clear all status effects on death
            player.isStunned = false
        }
    }

    private fun calculatePlayerDamage(monster: Monster): Int {
        val baseDamage = max(0, player.effectiveAttack - monster.defense)
        return baseDamage
    }

    private fun performMonsterAttack(monster: Monster) {
        // Check if monster uses special ability
        val useAbility = monster.canUseAbility() && Random.nextFloat() < 0.7f // 70% chance to use ability when available

        if (useAbility) {
            performMonsterAbility(monster)
        } else {
            performBasicMonsterAttack(monster)
        }
    }

    private fun performBasicMonsterAttack(monster: Monster) {
        var damage = calculateMonsterDamage(monster)
        
        // Apply magic shield if monster has it
        if (monster.ability == MonsterAbility.MAGIC_SHIELD) {
            // This is handled in damage calculation
        }

        player.currentHp -= damage
        combatLogCallback?.invoke("${monster.name} attacks Player for $damage damage. Player HP: ${player.currentHp}")
    }

    private fun performMonsterAbility(monster: Monster) {
        when (monster.ability) {
            MonsterAbility.CRITICAL_STRIKE -> {
                if (Random.nextInt(100) < monster.abilityPower) {
                    val critDamage = calculateMonsterDamage(monster) * 2
                    player.currentHp -= critDamage
                    combatLogCallback?.invoke("${monster.name} lands a CRITICAL HIT for $critDamage damage!")
                } else {
                    performBasicMonsterAttack(monster)
                }
            }
            MonsterAbility.POISON_ATTACK -> {
                val damage = calculateMonsterDamage(monster)
                player.currentHp -= damage
                player.addStatusEffect("poison", 3, monster.abilityPower)
                combatLogCallback?.invoke("${monster.name} attacks with poison for $damage damage and applies poison!")
            }
            MonsterAbility.LIFE_STEAL -> {
                val damage = calculateMonsterDamage(monster)
                val healAmount = (damage * monster.abilityPower / 100).coerceAtLeast(1)
                player.currentHp -= damage
                monster.hp = (monster.hp + healAmount).coerceAtMost(monster.maxHp)
                combatLogCallback?.invoke("${monster.name} drains life for $damage damage and heals $healAmount HP!")
            }
            MonsterAbility.STUN_CHANCE -> {
                val damage = calculateMonsterDamage(monster)
                player.currentHp -= damage
                if (Random.nextInt(100) < monster.abilityPower) {
                    player.isStunned = true
                    combatLogCallback?.invoke("${monster.name} attacks for $damage damage and stuns the player!")
                } else {
                    combatLogCallback?.invoke("${monster.name} attacks for $damage damage.")
                }
                monster.useAbility() // Set cooldown
            }
            MonsterAbility.DOUBLE_ATTACK -> {
                val damage1 = calculateMonsterDamage(monster)
                val damage2 = calculateMonsterDamage(monster)
                player.currentHp -= (damage1 + damage2)
                combatLogCallback?.invoke("${monster.name} attacks twice for $damage1 + $damage2 damage!")
                monster.useAbility() // Set cooldown
            }
            MonsterAbility.REGENERATION -> {
                performBasicMonsterAttack(monster)
                val healAmount = monster.abilityPower
                monster.hp = (monster.hp + healAmount).coerceAtMost(monster.maxHp)
                combatLogCallback?.invoke("${monster.name} regenerates $healAmount HP!")
            }
            MonsterAbility.BERSERKER_RAGE -> {
                val isEnraged = monster.hp <= monster.maxHp / 2
                val damage = if (isEnraged) {
                    calculateMonsterDamage(monster) + monster.abilityPower
                } else {
                    calculateMonsterDamage(monster)
                }
                player.currentHp -= damage
                val rageText = if (isEnraged) " (ENRAGED!)" else ""
                combatLogCallback?.invoke("${monster.name} attacks for $damage damage$rageText")
            }
            else -> performBasicMonsterAttack(monster)
        }
    }

    private fun calculateMonsterDamage(monster: Monster): Int {
        var baseDamage = monster.attack
        
        // Apply armor pierce
        val effectiveDefense = if (monster.ability == MonsterAbility.ARMOR_PIERCE) {
            max(0, player.effectiveDefense - monster.abilityPower)
        } else {
            player.effectiveDefense
        }
        
        var damage = max(0, baseDamage - effectiveDefense)
        
        // Apply magic shield reduction
        if (monster.ability == MonsterAbility.MAGIC_SHIELD) {
            // Note: This is for monsters that have magic shield, not the player
            // If we want player magic shield, we'd implement it differently
        }
        
        return damage
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
