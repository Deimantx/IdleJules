package com.example.idlerpg.game

import com.example.idlerpg.models.ArmorType
import com.example.idlerpg.models.GearItem
import com.example.idlerpg.models.ItemType
import com.example.idlerpg.models.Monster
import com.example.idlerpg.models.MonsterAbility
import com.example.idlerpg.models.Player
import com.example.idlerpg.models.WeaponType
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

enum class Location {
    FOREST,
    MINE,
    OUTSKIRTS
}

class GameEngine {
    var player: Player = Player() // Initialize with a default player
    var currentMonster: Monster? = null
    var selectedMonsterName: String? = null
    var selectedLocation: Location? = null

    private var monsterDefeatedCallback: ((Monster) -> Unit)? = null
    private var playerLeveledUpCallback: ((Player) -> Unit)? = null
    private var combatLogCallback: ((String) -> Unit)? = null

    val availableShopItems: List<GearItem> = listOf(
        // WEAPONS
        // Swords - normal attack speed and damage
        GearItem("Rusty Sword", ItemType.WEAPON, attackBonus = 5, cost = 50, attackSpeedBonus = -100f, weaponType = WeaponType.SWORD, description = "A worn but reliable blade"),
        GearItem("Iron Sword", ItemType.WEAPON, attackBonus = 12, cost = 200, attackSpeedBonus = -150f, critRateBonus = 2f, weaponType = WeaponType.SWORD, description = "A sturdy iron blade"),
        GearItem("Steel Sword", ItemType.WEAPON, attackBonus = 18, cost = 500, attackSpeedBonus = -200f, critRateBonus = 5f, weaponType = WeaponType.SWORD, description = "A well-crafted steel sword"),
        GearItem("Enchanted Blade", ItemType.WEAPON, attackBonus = 25, cost = 1200, attackSpeedBonus = -250f, critRateBonus = 8f, critDamageBonus = 0.2f, weaponType = WeaponType.SWORD, description = "A magically enhanced sword"),
        GearItem("Dragonslayer Sword", ItemType.WEAPON, attackBonus = 35, cost = 2500, attackSpeedBonus = -300f, critRateBonus = 12f, critDamageBonus = 0.3f, weaponType = WeaponType.SWORD, description = "Forged to slay dragons"),
        
        // Daggers - fast attack speed and low damage
        GearItem("Rusty Dagger", ItemType.WEAPON, attackBonus = 8, cost = 80, attackSpeedBonus = -400f, critRateBonus = 8f, weaponType = WeaponType.DAGGER, description = "Quick but weak"),
        GearItem("Steel Dagger", ItemType.WEAPON, attackBonus = 12, cost = 300, attackSpeedBonus = -500f, critRateBonus = 15f, weaponType = WeaponType.DAGGER, description = "Swift and deadly"),
        GearItem("Shadow Blade", ItemType.WEAPON, attackBonus = 16, cost = 800, attackSpeedBonus = -600f, critRateBonus = 20f, critDamageBonus = 0.4f, weaponType = WeaponType.DAGGER, description = "Strikes from the shadows"),
        GearItem("Venom Fang", ItemType.WEAPON, attackBonus = 20, cost = 1500, attackSpeedBonus = -700f, critRateBonus = 25f, critDamageBonus = 0.5f, weaponType = WeaponType.DAGGER, description = "Drips with deadly poison"),
        
        // 2Handers - slow but high damage
        GearItem("Iron Maul", ItemType.WEAPON, attackBonus = 22, cost = 400, attackSpeedBonus = 500f, critDamageBonus = 0.3f, weaponType = WeaponType.TWO_HANDED, description = "Crushes enemies with raw power"),
        GearItem("Steel Greatsword", ItemType.WEAPON, attackBonus = 30, cost = 800, attackSpeedBonus = 400f, critDamageBonus = 0.4f, weaponType = WeaponType.TWO_HANDED, description = "Massive two-handed blade"),
        GearItem("Warhammer of Might", ItemType.WEAPON, attackBonus = 40, cost = 1800, attackSpeedBonus = 600f, critDamageBonus = 0.6f, weaponType = WeaponType.TWO_HANDED, description = "Devastating crushing weapon"),
        GearItem("Titan's Cleaver", ItemType.WEAPON, attackBonus = 55, cost = 3500, attackSpeedBonus = 800f, critDamageBonus = 0.8f, weaponType = WeaponType.TWO_HANDED, description = "Splits mountains in half"),

        // ARMOR
        // Light Armor - low defense, bonus to mana
        GearItem("Cloth Robes", ItemType.ARMOR, defenseBonus = 2, cost = 40, manaBonus = 20, armorType = ArmorType.LIGHT, description = "Comfortable robes for spellcasters"),
        GearItem("Leather Vest", ItemType.ARMOR, defenseBonus = 5, cost = 120, manaBonus = 15, dodgeBonus = 3f, armorType = ArmorType.LIGHT, description = "Light and flexible protection"),
        GearItem("Studded Leather", ItemType.ARMOR, defenseBonus = 8, cost = 300, manaBonus = 25, dodgeBonus = 5f, armorType = ArmorType.LIGHT, description = "Reinforced leather armor"),
        GearItem("Elven Cloak", ItemType.ARMOR, defenseBonus = 12, cost = 800, manaBonus = 40, dodgeBonus = 8f, armorType = ArmorType.LIGHT, description = "Mystical elven garment"),
        
        // Medium Armor - medium defense, more chance to dodge
        GearItem("Chainmail Shirt", ItemType.ARMOR, defenseBonus = 10, cost = 250, dodgeBonus = 4f, armorType = ArmorType.MEDIUM, description = "Balanced protection and mobility"),
        GearItem("Scale Mail", ItemType.ARMOR, defenseBonus = 15, cost = 600, dodgeBonus = 6f, armorType = ArmorType.MEDIUM, description = "Overlapping metal scales"),
        GearItem("Brigandine Armor", ItemType.ARMOR, defenseBonus = 20, cost = 1200, dodgeBonus = 8f, armorType = ArmorType.MEDIUM, description = "Flexible metal plates"),
        GearItem("Mithril Chainmail", ItemType.ARMOR, defenseBonus = 28, cost = 2500, dodgeBonus = 12f, armorType = ArmorType.MEDIUM, description = "Legendary lightweight metal"),
        
        // Heavy Armor - more hp, attack speed penalty
        GearItem("Iron Plate", ItemType.ARMOR, defenseBonus = 18, cost = 500, hpBonus = 30, attackSpeedBonus = 200f, armorType = ArmorType.HEAVY, description = "Heavy but protective"),
        GearItem("Steel Plate", ItemType.ARMOR, defenseBonus = 25, cost = 1000, hpBonus = 50, attackSpeedBonus = 300f, armorType = ArmorType.HEAVY, description = "Superior heavy armor"),
        GearItem("Dragon Scale Mail", ItemType.ARMOR, defenseBonus = 35, cost = 2200, hpBonus = 80, attackSpeedBonus = 250f, armorType = ArmorType.HEAVY, description = "Crafted from dragon scales"),
        GearItem("Adamantine Plate", ItemType.ARMOR, defenseBonus = 45, cost = 4000, hpBonus = 120, attackSpeedBonus = 400f, armorType = ArmorType.HEAVY, description = "Unbreakable metal armor"),

        // SHIELDS
        GearItem("Wooden Shield", ItemType.SHIELD, defenseBonus = 3, cost = 60, description = "Basic wooden protection"),
        GearItem("Iron Shield", ItemType.SHIELD, defenseBonus = 6, cost = 180, dodgeBonus = 2f, description = "Sturdy iron shield"),
        GearItem("Steel Shield", ItemType.SHIELD, defenseBonus = 10, cost = 400, dodgeBonus = 4f, description = "Well-crafted steel shield"),
        GearItem("Tower Shield", ItemType.SHIELD, defenseBonus = 15, cost = 800, hpBonus = 25, description = "Massive protective shield"),
        GearItem("Aegis of Valor", ItemType.SHIELD, defenseBonus = 20, cost = 1600, dodgeBonus = 8f, hpBonus = 40, description = "Legendary protective aegis"),

        // AMULETS
        GearItem("Copper Amulet", ItemType.AMULET, cost = 100, attackBonus = 3, description = "Simple copper charm"),
        GearItem("Silver Pendant", ItemType.AMULET, cost = 300, manaBonus = 20, critRateBonus = 3f, description = "Mystical silver pendant"),
        GearItem("Amulet of Strength", ItemType.AMULET, cost = 600, attackBonus = 8, critDamageBonus = 0.15f, description = "Enhances physical power"),
        GearItem("Pendant of Vitality", ItemType.AMULET, cost = 800, hpBonus = 50, defenseBonus = 5, description = "Grants life force"),
        GearItem("Arcane Medallion", ItemType.AMULET, cost = 1200, manaBonus = 60, critRateBonus = 8f, description = "Pulses with magical energy"),
        GearItem("Heart of the Dragon", ItemType.AMULET, cost = 2500, attackBonus = 15, hpBonus = 80, critDamageBonus = 0.3f, description = "Contains a dragon's essence"),

        // RINGS
        GearItem("Copper Ring", ItemType.RING, cost = 80, hitBonus = 2f, description = "Simple copper band"),
        GearItem("Ring of Agility", ItemType.RING, cost = 250, dodgeBonus = 4f, critRateBonus = 5f, description = "Increases nimbleness"),
        GearItem("Ring of Power", ItemType.RING, cost = 500, attackBonus = 6, critDamageBonus = 0.1f, description = "Amplifies strength"),
        GearItem("Ring of Protection", ItemType.RING, cost = 400, defenseBonus = 4, hpBonus = 25, description = "Provides magical protection"),
        GearItem("Mana Crystal Ring", ItemType.RING, cost = 600, manaBonus = 30, critRateBonus = 6f, description = "Channels magical energy"),
        GearItem("Master's Signet", ItemType.RING, cost = 1500, attackBonus = 10, defenseBonus = 6, hitBonus = 8f, description = "Ring of a legendary warrior")
    )

    init {
        // Initialize with proper HP/Mana based on stats
        updatePlayerEffectiveStats()
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
    
    fun selectLocation(location: Location) {
        selectedLocation = location
        selectedMonsterName = null // Clear selected monster when changing location
        currentMonster = null
        combatLogCallback?.invoke("Selected location: ${location.name.lowercase().replaceFirstChar { it.uppercase() }}")
        combatLogCallback?.invoke("Choose a monster to fight in this location!")
    }
    
    fun selectMonster(monsterName: String) {
        selectedMonsterName = monsterName
        spawnSelectedMonster()
    }
    
    fun getAvailableLocations(): List<Location> {
        return Location.values().toList()
    }
    
    fun getAvailableMonsters(): List<String> {
        return MonsterFactory.getAllMonsterNames()
    }
    
    fun getMonstersForLocation(location: Location): List<String> {
        return when (location) {
            Location.FOREST -> listOf("Forest Wolf", "Giant Rat", "Goblin Scout", "Vampire Bat", "Shadow Demon")
            Location.MINE -> listOf("Skeleton Warrior", "Stone Golem", "Ancient Lich", "Orc Warrior", "Troll Berserker")
            Location.OUTSKIRTS -> listOf("Fire Elemental", "Young Dragon", "Vampire Bat", "Shadow Demon", "Troll Berserker")
        }
    }
    
    fun getMonstersForPlayerLevel(): List<String> {
        // Suggest monsters within reasonable level range of player
        val minLevel = (player.level - 2).coerceAtLeast(1)
        val maxLevel = player.level + 3
        return MonsterFactory.getMonstersForLevelRange(minLevel, maxLevel)
    }

    private fun spawnSelectedMonster() {
        val monsterName = selectedMonsterName ?: run {
            combatLogCallback?.invoke("No monster selected!")
            return
        }
        
        currentMonster = MonsterFactory.createMonsterByName(monsterName)
        currentMonster?.let { monster ->
            combatLogCallback?.invoke("A wild ${monster.name} (Level ${monster.level}) appears!")
            if (monster.ability != MonsterAbility.NONE) {
                combatLogCallback?.invoke("${monster.name}: ${monster.getAbilityDescription()}")
            }
        }
    }

    // Main combat tick - now handles attack speed timing
    fun combatTick() {
        val monster = currentMonster ?: return
        val currentTime = System.currentTimeMillis()
        
        // Process player status effects first
        val statusMessages = player.processStatusEffects()
        statusMessages.forEach { combatLogCallback?.invoke(it) }

        // Check if player died from status effects
        if (player.currentHp <= 0) {
            handlePlayerDeath()
            return
        }

        // Player auto-attack based on attack speed
        if (!player.isStunned && player.canAttack(currentTime)) {
            performPlayerAttack(monster)
            player.updateAttackTime(currentTime)
            
            if (monster.hp <= 0) {
                handleMonsterDefeat(monster)
                return
            }
        }

        // Monster attack based on attack speed
        if (monster.canAttack(currentTime)) {
            performMonsterAttack(monster)
            monster.updateAttackTime(currentTime)
        }

        // Tick monster cooldowns
        monster.tickCooldown()

        if (player.currentHp <= 0) {
            handlePlayerDeath()
        }
    }
    
    private fun performPlayerAttack(monster: Monster) {
        // Calculate hit chance
        val playerHitChance = player.hitChance + (player.equippedWeapon?.hitBonus ?: 0f)
        val monsterDodgeChance = monster.dodgeChance
        
        val hitRoll = Random.nextFloat() * 100f
        val effectiveHitChance = playerHitChance - monsterDodgeChance
        
        if (hitRoll > effectiveHitChance) {
            combatLogCallback?.invoke("Player's attack misses ${monster.name}!")
            return
        }
        
        // Calculate damage
        var damage = calculatePlayerDamage(monster)
        
        // Check for critical hit
        val playerCritRate = player.critRate + (player.equippedWeapon?.critRateBonus ?: 0f)
        val critRoll = Random.nextFloat() * 100f
        
        val isCritical = critRoll < playerCritRate
        if (isCritical) {
            val critMultiplier = player.critDamageMultiplier + (player.equippedWeapon?.critDamageBonus ?: 0f)
            damage = (damage * critMultiplier).toInt()
            combatLogCallback?.invoke("<font color='#0080FF'>CRITICAL HIT! Player deals $damage damage to ${monster.name}!</font>")
        } else {
            combatLogCallback?.invoke("<font color='#0080FF'>Player attacks ${monster.name} for $damage damage.</font>")
        }
        
        monster.hp -= damage
        combatLogCallback?.invoke("${monster.name} HP: ${monster.hp}/${monster.maxHp}")
    }

    private fun calculatePlayerDamage(monster: Monster): Int {
        val baseDamage = max(0, player.effectiveAttack - monster.defense)
        return baseDamage.coerceAtLeast(1) // Minimum 1 damage
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
        // Calculate hit chance
        val monsterHitChance = monster.hitChance
        val playerDodgeChance = player.dodgeChance + (player.equippedArmor?.dodgeBonus ?: 0f)
        
        val hitRoll = Random.nextFloat() * 100f
        val effectiveHitChance = monsterHitChance - playerDodgeChance
        
        if (hitRoll > effectiveHitChance) {
            combatLogCallback?.invoke("${monster.name}'s attack misses!")
            return
        }
        
        var damage = calculateMonsterDamage(monster)
        
        // Check for monster critical hit
        val critRoll = Random.nextFloat() * 100f
        val isCritical = critRoll < monster.critRate
        
        if (isCritical) {
            damage = (damage * monster.critDamage).toInt()
            combatLogCallback?.invoke("<font color='#FF0000'>${monster.name} lands a CRITICAL HIT for $damage damage!</font>")
        } else {
            combatLogCallback?.invoke("<font color='#FF0000'>${monster.name} attacks for $damage damage.</font>")
        }
        
        player.currentHp -= damage
        combatLogCallback?.invoke("Player HP: ${player.currentHp}/${player.effectiveMaxHp}")
    }

    private fun performMonsterAbility(monster: Monster) {
        when (monster.ability) {
            MonsterAbility.CRITICAL_STRIKE -> {
                if (Random.nextInt(100) < monster.abilityPower) {
                    val critDamage = calculateMonsterDamage(monster) * 2
                    player.currentHp -= critDamage
                    combatLogCallback?.invoke("<font color='#FF0000'>${monster.name} lands a CRITICAL HIT for $critDamage damage!</font>")
                } else {
                    performBasicMonsterAttack(monster)
                }
            }
            MonsterAbility.POISON_ATTACK -> {
                val damage = calculateMonsterDamage(monster)
                player.currentHp -= damage
                player.addStatusEffect("poison", 3, monster.abilityPower)
                combatLogCallback?.invoke("<font color='#FF0000'>${monster.name} attacks with poison for $damage damage and applies poison!</font>")
            }
            MonsterAbility.LIFE_STEAL -> {
                val damage = calculateMonsterDamage(monster)
                val healAmount = (damage * monster.abilityPower / 100).coerceAtLeast(1)
                player.currentHp -= damage
                monster.hp = (monster.hp + healAmount).coerceAtMost(monster.maxHp)
                combatLogCallback?.invoke("<font color='#FF0000'>${monster.name} drains life for $damage damage and heals $healAmount HP!</font>")
            }
            MonsterAbility.STUN_CHANCE -> {
                val damage = calculateMonsterDamage(monster)
                player.currentHp -= damage
                if (Random.nextInt(100) < monster.abilityPower) {
                    player.isStunned = true
                    combatLogCallback?.invoke("<font color='#FF0000'>${monster.name} attacks for $damage damage and stuns the player!</font>")
                } else {
                    combatLogCallback?.invoke("<font color='#FF0000'>${monster.name} attacks for $damage damage.</font>")
                }
                monster.useAbility() // Set cooldown
            }
            MonsterAbility.DOUBLE_ATTACK -> {
                val damage1 = calculateMonsterDamage(monster)
                val damage2 = calculateMonsterDamage(monster)
                player.currentHp -= (damage1 + damage2)
                combatLogCallback?.invoke("<font color='#FF0000'>${monster.name} attacks twice for $damage1 + $damage2 damage!</font>")
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
            damage = max(0, damage - monster.abilityPower)
        }
        
        return damage.coerceAtLeast(1) // Minimum 1 damage
    }
    
    private fun handleMonsterDefeat(monster: Monster) {
        combatLogCallback?.invoke("<font color='#FFD700'>${monster.name} defeated! Gained ${monster.experienceReward} XP and ${monster.coinReward} gold!</font>")
        player.experience += monster.experienceReward.toLong()
        player.coins += monster.coinReward
        monsterDefeatedCallback?.invoke(monster)
        checkPlayerLevelUp()
        
        // Reset player HP and Mana after each fight
        player.currentHp = player.effectiveMaxHp
        player.currentMana = player.effectiveMaxMana
        player.statusEffects.clear()
        player.isStunned = false
        combatLogCallback?.invoke("Player restored to full health and mana!")
        
        // Auto-respawn the same monster
        spawnSelectedMonster()
    }
    
    private fun handlePlayerDeath() {
        combatLogCallback?.invoke("Player has been defeated! Respawning...")
        player.currentHp = player.effectiveMaxHp
        player.currentMana = player.effectiveMaxMana
        player.statusEffects.clear()
        player.isStunned = false
        // Clear current monster
        currentMonster = null
    }

    private fun checkPlayerLevelUp() {
        val experienceNeeded = calculateExperienceForNextLevel(player.level)
        if (player.experience >= experienceNeeded) {
            player.level++
            player.experience -= experienceNeeded
            val skillPointsGained = 5 // Award 5 skill points per level for new stat system
            player.skillPoints += skillPointsGained

            // Update effective stats
            updatePlayerEffectiveStats()

            combatLogCallback?.invoke("Player leveled up to Level ${player.level}! Gained $skillPointsGained skill point(s).")
            playerLeveledUpCallback?.invoke(player)
            checkPlayerLevelUp()
        }
    }
    
    private fun updatePlayerEffectiveStats() {
        player.maxHp = player.effectiveMaxHp
        player.currentHp = player.maxHp
        player.maxMana = player.effectiveMaxMana
        player.currentMana = player.maxMana
    }

    fun calculateExperienceForNextLevel(level: Int): Long {
        return (level.toDouble().pow(1.5) * 100).toLong()
    }

    // New stat-based skill point spending
    fun spendSkillPointOnStrength(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.strength++
            combatLogCallback?.invoke("Spent 1 skill point on Strength. STR: ${player.strength} (+2 Attack, +5% Crit Damage)")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Strength.")
        return false
    }

    fun spendSkillPointOnAgility(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.agility++
            combatLogCallback?.invoke("Spent 1 skill point on Agility. AGI: ${player.agility} (+0.5% Crit Rate, +0.3% Dodge, +0.4% Hit)")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Agility.")
        return false
    }

    fun spendSkillPointOnIntelligence(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.intelligence++
            player.maxMana = player.effectiveMaxMana
            player.currentMana = player.maxMana
            combatLogCallback?.invoke("Spent 1 skill point on Intelligence. INT: ${player.intelligence} (+5 Mana)")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Intelligence.")
        return false
    }

    fun spendSkillPointOnVitality(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.vitality++
            val oldMaxHp = player.maxHp
            player.maxHp = player.effectiveMaxHp
            player.currentHp += (player.maxHp - oldMaxHp) // Add the HP difference to current HP
            combatLogCallback?.invoke("Spent 1 skill point on Vitality. VIT: ${player.vitality} (+10 Max HP)")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Vitality.")
        return false
    }

    fun spendSkillPointOnSpirit(): Boolean {
        if (player.skillPoints > 0) {
            player.skillPoints--
            player.spirit++
            val oldMaxMana = player.maxMana
            player.maxMana = player.effectiveMaxMana
            player.currentMana += (player.maxMana - oldMaxMana) // Add the mana difference
            combatLogCallback?.invoke("Spent 1 skill point on Spirit. SPR: ${player.spirit} (+8 Mana)")
            return true
        }
        combatLogCallback?.invoke("Not enough skill points to increase Spirit.")
        return false
    }

    fun buyItem(itemToBuy: GearItem): String {
        if (player.coins < itemToBuy.cost) {
            return "Not enough coins to buy ${itemToBuy.name}."
        }

        player.coins -= itemToBuy.cost
        player.inventory.add(itemToBuy.copy()) // Add a copy to inventory

        combatLogCallback?.invoke("Bought ${itemToBuy.name}. Added to inventory.")
        return "Successfully bought ${itemToBuy.name}."
    }

    fun equipItem(itemToEquip: GearItem): String {
        val itemInInventory = player.inventory.find { it.name == itemToEquip.name && it.cost == itemToEquip.cost }
        if (itemInInventory == null) {
            return "Item not found in inventory."
        }

        when (itemToEquip.type) {
            ItemType.WEAPON -> {
                player.equippedWeapon?.let { oldWeapon ->
                    player.inventory.add(oldWeapon) // Return old weapon to inventory
                }
                player.equippedWeapon = itemInInventory.copy()
                player.inventory.remove(itemInInventory)
                combatLogCallback?.invoke("Equipped ${itemToEquip.name} as weapon.")
            }
            ItemType.ARMOR -> {
                player.equippedArmor?.let { oldArmor ->
                    player.inventory.add(oldArmor) // Return old armor to inventory
                }
                player.equippedArmor = itemInInventory.copy()
                player.inventory.remove(itemInInventory)
                combatLogCallback?.invoke("Equipped ${itemToEquip.name} as armor.")
            }
            ItemType.SHIELD -> {
                player.equippedShield?.let { oldShield ->
                    player.inventory.add(oldShield) // Return old shield to inventory
                }
                player.equippedShield = itemInInventory.copy()
                player.inventory.remove(itemInInventory)
                combatLogCallback?.invoke("Equipped ${itemToEquip.name} as shield.")
            }
            ItemType.AMULET -> {
                player.equippedAmulet?.let { oldAmulet ->
                    player.inventory.add(oldAmulet) // Return old amulet to inventory
                }
                player.equippedAmulet = itemInInventory.copy()
                player.inventory.remove(itemInInventory)
                combatLogCallback?.invoke("Equipped ${itemToEquip.name} as amulet.")
            }
            ItemType.RING -> {
                player.equippedRing?.let { oldRing ->
                    player.inventory.add(oldRing) // Return old ring to inventory
                }
                player.equippedRing = itemInInventory.copy()
                player.inventory.remove(itemInInventory)
                combatLogCallback?.invoke("Equipped ${itemToEquip.name} as ring.")
            }
        }

        return "Successfully equipped ${itemToEquip.name}."
    }

    fun unequipItem(itemType: ItemType): String {
        when (itemType) {
            ItemType.WEAPON -> {
                player.equippedWeapon?.let { weapon ->
                    player.inventory.add(weapon)
                    player.equippedWeapon = null
                    combatLogCallback?.invoke("Unequipped ${weapon.name}.")
                    return "Unequipped ${weapon.name}."
                }
            }
            ItemType.ARMOR -> {
                player.equippedArmor?.let { armor ->
                    player.inventory.add(armor)
                    player.equippedArmor = null
                    combatLogCallback?.invoke("Unequipped ${armor.name}.")
                    return "Unequipped ${armor.name}."
                }
            }
            ItemType.SHIELD -> {
                player.equippedShield?.let { shield ->
                    player.inventory.add(shield)
                    player.equippedShield = null
                    combatLogCallback?.invoke("Unequipped ${shield.name}.")
                    return "Unequipped ${shield.name}."
                }
            }
            ItemType.AMULET -> {
                player.equippedAmulet?.let { amulet ->
                    player.inventory.add(amulet)
                    player.equippedAmulet = null
                    combatLogCallback?.invoke("Unequipped ${amulet.name}.")
                    return "Unequipped ${amulet.name}."
                }
            }
            ItemType.RING -> {
                player.equippedRing?.let { ring ->
                    player.inventory.add(ring)
                    player.equippedRing = null
                    combatLogCallback?.invoke("Unequipped ${ring.name}.")
                    return "Unequipped ${ring.name}."
                }
            }
        }
        return "No item equipped in that slot."
    }

    fun sellItem(itemToSell: GearItem, sellPricePercentage: Float = 0.5f): String {
        val itemInInventory = player.inventory.find { it.name == itemToSell.name } // Basic find, assumes unique names for now or sells first found

        if (itemInInventory == null) {
            return "Cannot sell ${itemToSell.name}, item not found in inventory."
        }

        player.inventory.remove(itemInInventory)
        val sellPrice = (itemInInventory.cost * sellPricePercentage).toInt()
        player.coins += sellPrice

        // Check if the sold item was equipped and unequip it
        var unequippedMessage = ""
        if (player.equippedWeapon?.name == itemInInventory.name && player.equippedWeapon?.cost == itemInInventory.cost) {
            player.equippedWeapon = null
            unequippedMessage = " ${itemInInventory.name} was unequipped."
        }
        if (player.equippedArmor?.name == itemInInventory.name && player.equippedArmor?.cost == itemInInventory.cost) {
            player.equippedArmor = null
            unequippedMessage = " ${itemInInventory.name} was unequipped."
        }
        if (player.equippedShield?.name == itemInInventory.name && player.equippedShield?.cost == itemInInventory.cost) {
            player.equippedShield = null
            unequippedMessage = " ${itemInInventory.name} was unequipped."
        }
        if (player.equippedAmulet?.name == itemInInventory.name && player.equippedAmulet?.cost == itemInInventory.cost) {
            player.equippedAmulet = null
            unequippedMessage = " ${itemInInventory.name} was unequipped."
        }
        if (player.equippedRing?.name == itemInInventory.name && player.equippedRing?.cost == itemInInventory.cost) {
            player.equippedRing = null
            unequippedMessage = " ${itemInInventory.name} was unequipped."
        }

        combatLogCallback?.invoke("Sold ${itemInInventory.name} for $sellPrice coins.$unequippedMessage")
        return "Sold ${itemInInventory.name} for $sellPrice coins.$unequippedMessage"
    }

    fun getPlayerStats(): Player {
        return player.copy()
    }

    // Helper methods for categorized shop
    fun getShopItemsByCategory(itemType: ItemType): List<GearItem> {
        return availableShopItems.filter { it.type == itemType }
    }

    fun getWeaponsByType(weaponType: WeaponType): List<GearItem> {
        return availableShopItems.filter { it.type == ItemType.WEAPON && it.weaponType == weaponType }
    }

    fun getArmorByType(armorType: ArmorType): List<GearItem> {
        return availableShopItems.filter { it.type == ItemType.ARMOR && it.armorType == armorType }
    }
    
    // Legacy methods for compatibility - now do nothing since we removed manual attack
    @Deprecated("Manual attacks have been removed - combat is now automatic based on attack speed")
    fun fightTick() {
        // Do nothing - combat is now handled by combatTick()
    }
    
    // Legacy skill point methods for compatibility
    @Deprecated("Use spendSkillPointOnStrength instead")
    fun spendSkillPointOnAttack(): Boolean = spendSkillPointOnStrength()
    
    @Deprecated("Use spendSkillPointOnVitality instead") 
    fun spendSkillPointOnDefense(): Boolean = false // Defense is now equipment-based
    
    @Deprecated("Use spendSkillPointOnVitality instead")
    fun spendSkillPointOnMaxHp(): Boolean = spendSkillPointOnVitality()
}
