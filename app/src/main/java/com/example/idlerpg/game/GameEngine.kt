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
        GearItem("Wooden Sword", attackBonus = 5, attackSpeed = -200L, cost = 50),
        GearItem("Leather Vest", defenseBonus = 3, cost = 40),
        GearItem("Iron Sword", attackBonus = 10, attackSpeed = -300L, cost = 200),
        GearItem("Chainmail Armor", defenseBonus = 8, cost = 180),
        GearItem("Steel Sword", attackBonus = 15, attackSpeed = -400L, cost = 500),
        GearItem("Plate Armor", defenseBonus = 12, cost = 450),
        GearItem("Swift Dagger", attackBonus = 8, attackSpeed = -600L, cost = 350),
        GearItem("Battle Axe", attackBonus = 20, attackSpeed = 300L, cost = 600)
    )

    var selectedMonsterName: String? = null

    init {
        // Don't spawn automatically anymore
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

    fun selectMonster(monsterName: String) {
        selectedMonsterName = monsterName
        currentMonster = MonsterFactory.createMonsterByName(monsterName)
        currentMonster?.let { monster ->
            combatLogCallback?.invoke("You engage ${monster.name} in combat!")
            combatLogCallback?.invoke("Level ${monster.level} ${monster.type.name.lowercase().replaceFirstChar { it.uppercase() }}")
            if (monster.ability != MonsterAbility.NONE) {
                combatLogCallback?.invoke("${monster.name}: ${monster.getAbilityDescription()}")
            }
        }
    }
    
    fun getAvailableMonsters(): List<Monster> {
        return MonsterFactory.getAllMonsters()
    }

    fun fightTick() {
        val monster = currentMonster ?: return

        // Process player status effects (every tick)
        val statusMessages = player.processStatusEffects()
        statusMessages.forEach { combatLogCallback?.invoke(it) }

        // Check if player can attack (based on attack speed)
        if (!player.isStunned && player.canAttack()) {
            performPlayerAttack(monster)
            if (monster.hp <= 0) {
                combatLogCallback?.invoke("${monster.name} defeated!")
                player.experience += monster.experienceReward.toLong()
                player.coins += monster.coinReward
                monsterDefeatedCallback?.invoke(monster)
                checkPlayerLevelUp()
                currentMonster = null // Clear current monster, player must select new one
                return
            }
        }

        // Check if monster can attack (based on attack speed)
        if (monster.canAttack()) {
            performMonsterAttack(monster)
        }

        // Tick monster cooldowns
        monster.tickCooldown()

        if (player.currentHp <= 0) {
            combatLogCallback?.invoke("Player has been defeated! Game Over (for now).")
            player.currentHp = player.maxHp // Simple reset
            player.statusEffects.clear() // Clear all status effects on death
            player.isStunned = false
            currentMonster = null // Clear monster on death
        }
    }

    private fun performPlayerAttack(monster: Monster) {
        player.performAttack() // Mark attack time
        
        // Check hit chance
        val hitRoll = Random.nextInt(100)
        if (hitRoll >= player.hitChance.coerceAtMost(95)) {
            combatLogCallback?.invoke("Player misses ${monster.name}!")
            return
        }
        
        // Calculate base damage
        var damage = calculatePlayerDamage(monster)
        
        // Check for critical hit
        val critRoll = Random.nextInt(100)
        val isCritical = critRoll < player.criticalChance
        
        if (isCritical) {
            damage = (damage * player.criticalDamage / 100)
            monster.hp -= damage
            combatLogCallback?.invoke("Player CRITICALLY hits ${monster.name} for $damage damage! ${monster.name} HP: ${monster.hp}")
        } else {
            monster.hp -= damage
            combatLogCallback?.invoke("Player attacks ${monster.name} for $damage damage. ${monster.name} HP: ${monster.hp}")
        }
    }

    private fun calculatePlayerDamage(monster: Monster): Int {
        val baseDamage = max(1, player.effectiveAttack - monster.defense)
        return baseDamage
    }

    private fun performMonsterAttack(monster: Monster) {
        monster.performAttack() // Mark attack time
        
        // Check monster dodge chance
        val dodgeRoll = Random.nextInt(100)
        if (dodgeRoll < player.dodgeChance) {
            combatLogCallback?.invoke("Player dodges ${monster.name}'s attack!")
            return
        }
        
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

    fun spendSkillPointOnStrength(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.strength++
            combatLogCallback?.invoke("Spent 1 skill point on Strength. STR: ${player.strength} (Damage: ${player.effectiveAttack}, Crit Dmg: ${player.criticalDamage}%)")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Strength.")
        return false
    }

    fun spendSkillPointOnAgility(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.agility++
            combatLogCallback?.invoke("Spent 1 skill point on Agility. AGI: ${player.agility} (Hit: ${player.hitChance}%, Dodge: ${player.dodgeChance}%, Crit: ${player.criticalChance}%)")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Agility.")
        return false
    }

    fun spendSkillPointOnIntelligence(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.intelligence++
            combatLogCallback?.invoke("Spent 1 skill point on Intelligence. INT: ${player.intelligence} (Max Mana: ${player.maxMana})")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Intelligence.")
        return false
    }

    fun spendSkillPointOnVitality(): Boolean {
        if (player.skillPoints > 0) {
            val oldMaxHp = player.maxHp
            player.skillPoints--
            player.vitality++
            val newMaxHp = player.maxHp
            val hpIncrease = newMaxHp - oldMaxHp
            player.currentHp += hpIncrease // Increase current HP by the difference
            combatLogCallback?.invoke("Spent 1 skill point on Vitality. VIT: ${player.vitality} (Max HP: ${player.maxHp})")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Vitality.")
        return false
    }

    fun spendSkillPointOnMana(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.mana++
            combatLogCallback?.invoke("Spent 1 skill point on Mana. MANA: ${player.mana} (Max Mana: ${player.maxMana})")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Mana.")
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
