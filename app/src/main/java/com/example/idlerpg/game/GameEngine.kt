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

    val availableShopItems: List<GearItem> = ShopItems.allItems

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



    fun equipItem(itemToEquip: GearItem): String {
        val itemInInventory = player.inventory.find { it.name == itemToEquip.name && it.cost == itemToEquip.cost }
        if (itemInInventory == null) {
            return "Item not found in inventory."
        }

        // Check level requirement
        if (player.level < itemToEquip.levelRequirement) {
            return "Level ${itemToEquip.levelRequirement} required to equip ${itemToEquip.name}."
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
            ItemType.BELT -> {
                player.equippedBelt?.let { oldBelt ->
                    player.inventory.add(oldBelt) // Return old belt to inventory
                }
                player.equippedBelt = itemInInventory.copy()
                player.inventory.remove(itemInInventory)
                combatLogCallback?.invoke("Equipped ${itemToEquip.name} as belt.")
            }
            ItemType.GLOVES -> {
                player.equippedGloves?.let { oldGloves ->
                    player.inventory.add(oldGloves) // Return old gloves to inventory
                }
                player.equippedGloves = itemInInventory.copy()
                player.inventory.remove(itemInInventory)
                combatLogCallback?.invoke("Equipped ${itemToEquip.name} as gloves.")
            }
            ItemType.BOOTS -> {
                player.equippedBoots?.let { oldBoots ->
                    player.inventory.add(oldBoots) // Return old boots to inventory
                }
                player.equippedBoots = itemInInventory.copy()
                player.inventory.remove(itemInInventory)
                combatLogCallback?.invoke("Equipped ${itemToEquip.name} as boots.")
            }
            ItemType.CLOAK -> {
                player.equippedCloak?.let { oldCloak ->
                    player.inventory.add(oldCloak) // Return old cloak to inventory
                }
                player.equippedCloak = itemInInventory.copy()
                player.inventory.remove(itemInInventory)
                combatLogCallback?.invoke("Equipped ${itemToEquip.name} as cloak.")
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
            ItemType.BELT -> {
                player.equippedBelt?.let { belt ->
                    player.inventory.add(belt)
                    player.equippedBelt = null
                    combatLogCallback?.invoke("Unequipped ${belt.name}.")
                    return "Unequipped ${belt.name}."
                }
            }
            ItemType.GLOVES -> {
                player.equippedGloves?.let { gloves ->
                    player.inventory.add(gloves)
                    player.equippedGloves = null
                    combatLogCallback?.invoke("Unequipped ${gloves.name}.")
                    return "Unequipped ${gloves.name}."
                }
            }
            ItemType.BOOTS -> {
                player.equippedBoots?.let { boots ->
                    player.inventory.add(boots)
                    player.equippedBoots = null
                    combatLogCallback?.invoke("Unequipped ${boots.name}.")
                    return "Unequipped ${boots.name}."
                }
            }
            ItemType.CLOAK -> {
                player.equippedCloak?.let { cloak ->
                    player.inventory.add(cloak)
                    player.equippedCloak = null
                    combatLogCallback?.invoke("Unequipped ${cloak.name}.")
                    return "Unequipped ${cloak.name}."
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
        if (player.equippedBelt?.name == itemInInventory.name && player.equippedBelt?.cost == itemInInventory.cost) {
            player.equippedBelt = null
            unequippedMessage = " ${itemInInventory.name} was unequipped."
        }
        if (player.equippedGloves?.name == itemInInventory.name && player.equippedGloves?.cost == itemInInventory.cost) {
            player.equippedGloves = null
            unequippedMessage = " ${itemInInventory.name} was unequipped."
        }
        if (player.equippedBoots?.name == itemInInventory.name && player.equippedBoots?.cost == itemInInventory.cost) {
            player.equippedBoots = null
            unequippedMessage = " ${itemInInventory.name} was unequipped."
        }
        if (player.equippedCloak?.name == itemInInventory.name && player.equippedCloak?.cost == itemInInventory.cost) {
            player.equippedCloak = null
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
    
    // New categorization methods for expanded shop
    fun getItemsByWeaponCategory(): Map<String, List<GearItem>> {
        val weapons = availableShopItems.filter { it.type == ItemType.WEAPON }
        return weapons.groupBy { it.getWeaponCategory() }
    }
    
    fun getAccessoriesByType(accessoryType: AccessoryType): List<GearItem> {
        return availableShopItems.filter { 
            it.type in listOf(ItemType.RING, ItemType.AMULET, ItemType.BELT, ItemType.GLOVES, ItemType.BOOTS, ItemType.CLOAK) 
            && it.accessoryType == accessoryType 
        }
    }
    
    fun getItemsByRarity(rarity: ItemRarity): List<GearItem> {
        return availableShopItems.filter { it.rarity == rarity }
    }
    
    fun getItemsForPlayerLevel(playerLevel: Int, levelRange: Int = 5): List<GearItem> {
        return availableShopItems.filter { 
            it.levelRequirement <= playerLevel && 
            it.levelRequirement >= (playerLevel - levelRange).coerceAtLeast(1)
        }
    }
    
    fun buyItem(itemToBuy: GearItem): String {
        // Check level requirement
        if (player.level < itemToBuy.levelRequirement) {
            return "Level ${itemToBuy.levelRequirement} required to buy ${itemToBuy.name}."
        }
        
        if (player.coins < itemToBuy.cost) {
            return "Not enough coins to buy ${itemToBuy.name}."
        }

        player.coins -= itemToBuy.cost
        player.inventory.add(itemToBuy.copy()) // Add a copy to inventory

        combatLogCallback?.invoke("Bought ${itemToBuy.name}. Added to inventory.")
        return "Successfully bought ${itemToBuy.name}."
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
