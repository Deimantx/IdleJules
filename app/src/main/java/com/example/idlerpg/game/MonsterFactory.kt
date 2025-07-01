package com.example.idlerpg.game

import com.example.idlerpg.models.Monster
import com.example.idlerpg.models.MonsterAbility
import com.example.idlerpg.models.MonsterType
import kotlin.random.Random

object MonsterFactory {
    
    // Base monster templates - now with fixed balanced stats for different difficulty levels
    private val monsterTemplates = listOf(
        // Level 1-3 monsters (Easy)
        MonsterTemplate(
            name = "Forest Wolf",
            type = MonsterType.BEAST,
            baseHp = 25,
            baseAttack = 8,
            baseDefense = 2,
            baseExp = 15,
            baseCoins = 8,
            ability = MonsterAbility.CRITICAL_STRIKE,
            abilityPower = 20, // 20% crit chance
            abilityCooldown = 0,
            description = "A fierce wolf with sharp fangs and keen hunting instincts.",
            level = 2,
            attackSpeed = 1800f, // Fast attacker
            critRate = 15f,
            dodgeChance = 8f
        ),
        MonsterTemplate(
            name = "Goblin Scout",
            type = MonsterType.HUMANOID,
            baseHp = 20,
            baseAttack = 6,
            baseDefense = 1,
            baseExp = 12,
            baseCoins = 6,
            ability = MonsterAbility.NONE,
            abilityPower = 0,
            abilityCooldown = 0,
            description = "A small, quick goblin armed with crude weapons.",
            level = 1,
            attackSpeed = 1500f, // Very fast
            critRate = 5f,
            dodgeChance = 12f
        ),
        MonsterTemplate(
            name = "Giant Rat",
            type = MonsterType.BEAST,
            baseHp = 18,
            baseAttack = 5,
            baseDefense = 0,
            baseExp = 10,
            baseCoins = 4,
            ability = MonsterAbility.NONE,
            abilityPower = 0,
            abilityCooldown = 0,
            description = "A large rat with disease-ridden fangs.",
            level = 1,
            attackSpeed = 1200f, // Very fast but weak
            critRate = 3f,
            dodgeChance = 15f
        ),
        
        // Level 4-6 monsters (Medium)
        MonsterTemplate(
            name = "Skeleton Warrior",
            type = MonsterType.UNDEAD,
            baseHp = 35,
            baseAttack = 12,
            baseDefense = 5,
            baseExp = 25,
            baseCoins = 15,
            ability = MonsterAbility.ARMOR_PIERCE,
            abilityPower = 3, // Ignores 3 defense
            abilityCooldown = 0,
            description = "An undead warrior wielding ancient weapons.",
            level = 4,
            attackSpeed = 2200f, // Medium speed
            critRate = 8f,
            dodgeChance = 3f
        ),
        MonsterTemplate(
            name = "Fire Elemental",
            type = MonsterType.ELEMENTAL,
            baseHp = 30,
            baseAttack = 15,
            baseDefense = 2,
            baseExp = 30,
            baseCoins = 18,
            ability = MonsterAbility.POISON_ATTACK,
            abilityPower = 4, // 4 burn damage per turn
            abilityCooldown = 0,
            description = "A blazing creature of pure fire that burns everything it touches.",
            level = 5,
            attackSpeed = 2000f, // Medium speed
            critRate = 12f,
            dodgeChance = 5f
        ),
        MonsterTemplate(
            name = "Orc Warrior",
            type = MonsterType.HUMANOID,
            baseHp = 45,
            baseAttack = 14,
            baseDefense = 6,
            baseExp = 28,
            baseCoins = 16,
            ability = MonsterAbility.BERSERKER_RAGE,
            abilityPower = 6, // +6 attack when below 50% HP
            abilityCooldown = 0,
            description = "A brutal orc warrior with massive strength.",
            level = 5,
            attackSpeed = 2400f, // Slower but strong
            critRate = 10f,
            dodgeChance = 2f
        ),
        
        // Level 7-10 monsters (Hard)
        MonsterTemplate(
            name = "Troll Berserker",
            type = MonsterType.HUMANOID,
            baseHp = 70,
            baseAttack = 18,
            baseDefense = 8,
            baseExp = 40,
            baseCoins = 25,
            ability = MonsterAbility.BERSERKER_RAGE,
            abilityPower = 10, // +10 attack when below 50% HP
            abilityCooldown = 0,
            description = "A massive troll that becomes more dangerous when wounded.",
            level = 8,
            attackSpeed = 3000f, // Slow but devastating
            critRate = 15f,
            dodgeChance = 1f
        ),
        MonsterTemplate(
            name = "Vampire Bat",
            type = MonsterType.UNDEAD,
            baseHp = 25,
            baseAttack = 10,
            baseDefense = 1,
            baseExp = 20,
            baseCoins = 12,
            ability = MonsterAbility.LIFE_STEAL,
            abilityPower = 40, // Steals 40% of damage as health
            abilityCooldown = 0,
            description = "A bloodthirsty creature that feeds on life force.",
            level = 6,
            attackSpeed = 1600f, // Fast
            critRate = 20f,
            dodgeChance = 18f
        ),
        MonsterTemplate(
            name = "Stone Golem",
            type = MonsterType.CONSTRUCT,
            baseHp = 90,
            baseAttack = 20,
            baseDefense = 15,
            baseExp = 50,
            baseCoins = 35,
            ability = MonsterAbility.STUN_CHANCE,
            abilityPower = 25, // 25% chance to stun
            abilityCooldown = 3,
            description = "An ancient construct of living stone with crushing blows.",
            level = 9,
            attackSpeed = 3500f, // Very slow but extremely powerful
            critRate = 5f,
            dodgeChance = 0f
        ),
        MonsterTemplate(
            name = "Shadow Demon",
            type = MonsterType.DEMON,
            baseHp = 40,
            baseAttack = 16,
            baseDefense = 4,
            baseExp = 35,
            baseCoins = 22,
            ability = MonsterAbility.DOUBLE_ATTACK,
            abilityPower = 0,
            abilityCooldown = 3, // Attacks twice every 3 turns
            description = "A dark entity from the shadow realm with lightning-fast strikes.",
            level = 7,
            attackSpeed = 1400f, // Very fast
            critRate = 25f,
            dodgeChance = 12f
        ),
        
        // Level 10+ monsters (Very Hard)
        MonsterTemplate(
            name = "Young Dragon",
            type = MonsterType.DRAGON,
            baseHp = 120,
            baseAttack = 25,
            baseDefense = 12,
            baseExp = 80,
            baseCoins = 50,
            ability = MonsterAbility.CRITICAL_STRIKE,
            abilityPower = 35, // 35% crit chance
            abilityCooldown = 0,
            description = "A juvenile dragon with devastating breath attacks and razor-sharp claws.",
            level = 12,
            attackSpeed = 2800f, // Slow but extremely powerful
            critRate = 35f,
            critDamage = 2.5f,
            dodgeChance = 3f
        ),
        MonsterTemplate(
            name = "Ancient Lich",
            type = MonsterType.UNDEAD,
            baseHp = 80,
            baseAttack = 22,
            baseDefense = 10,
            baseExp = 70,
            baseCoins = 45,
            ability = MonsterAbility.MAGIC_SHIELD,
            abilityPower = 5, // Reduces damage by 5
            abilityCooldown = 0,
            description = "A powerful undead sorcerer with ancient magic.",
            level = 11,
            attackSpeed = 2600f, // Medium-slow
            critRate = 20f,
            critDamage = 2.2f,
            dodgeChance = 8f
        )
    )
    
    // Data class for monster templates
    private data class MonsterTemplate(
        val name: String,
        val type: MonsterType,
        val baseHp: Int,
        val baseAttack: Int,
        val baseDefense: Int,
        val baseExp: Int,
        val baseCoins: Int,
        val ability: MonsterAbility,
        val abilityPower: Int,
        val abilityCooldown: Int,
        val description: String,
        val level: Int,
        val attackSpeed: Float,
        val critRate: Float,
        val critDamage: Float = 1.8f,
        val dodgeChance: Float
    )
    
    /**
     * Creates a monster by name without level scaling
     */
    fun createMonsterByName(name: String): Monster? {
        val template = monsterTemplates.find { it.name == name } ?: return null
        return createMonsterFromTemplate(template)
    }
    
    /**
     * Gets all available monster names sorted by level
     */
    fun getAllMonsterNames(): List<String> {
        return monsterTemplates.sortedBy { it.level }.map { it.name }
    }
    
    /**
     * Gets monsters filtered by level range
     */
    fun getMonstersForLevelRange(minLevel: Int, maxLevel: Int): List<String> {
        return monsterTemplates
            .filter { it.level in minLevel..maxLevel }
            .sortedBy { it.level }
            .map { it.name }
    }
    
    /**
     * Gets monster level by name
     */
    fun getMonsterLevel(name: String): Int {
        return monsterTemplates.find { it.name == name }?.level ?: 1
    }
    
    /**
     * Creates a monster from a template without level scaling
     */
    private fun createMonsterFromTemplate(template: MonsterTemplate): Monster {
        return Monster(
            name = template.name,
            type = template.type,
            hp = template.baseHp,
            maxHp = template.baseHp,
            attack = template.baseAttack,
            defense = template.baseDefense,
            experienceReward = template.baseExp,
            coinReward = template.baseCoins,
            ability = template.ability,
            abilityPower = template.abilityPower,
            abilityCooldown = template.abilityCooldown,
            currentCooldown = 0,
            description = template.description,
            level = template.level,
            attackSpeed = template.attackSpeed,
            lastAttackTime = 0L,
            critRate = template.critRate,
            critDamage = template.critDamage,
            dodgeChance = template.dodgeChance,
            hitChance = 90f
        )
    }
    
    /**
     * Creates a boss version of a monster (enhanced stats and abilities)
     */
    fun createBossMonster(monsterName: String): Monster? {
        val baseMonster = createMonsterByName(monsterName) ?: return null
        
        return baseMonster.copy(
            name = "Elite ${baseMonster.name}",
            hp = (baseMonster.hp * 1.5).toInt(),
            maxHp = (baseMonster.maxHp * 1.5).toInt(),
            attack = (baseMonster.attack * 1.3).toInt(),
            defense = (baseMonster.defense * 1.2).toInt(),
            experienceReward = (baseMonster.experienceReward * 2).toInt(),
            coinReward = (baseMonster.coinReward * 2).toInt(),
            abilityPower = (baseMonster.abilityPower * 1.2).toInt(),
            description = "An elite version of ${baseMonster.name} with enhanced abilities.",
            attackSpeed = baseMonster.attackSpeed * 0.8f, // 20% faster
            critRate = baseMonster.critRate * 1.5f,
            critDamage = baseMonster.critDamage + 0.3f
        )
    }
    
    // Legacy method for compatibility - now returns first monster
    @Deprecated("Use createMonsterByName instead")
    fun createRandomMonster(playerLevel: Int): Monster {
        return createMonsterFromTemplate(monsterTemplates.first())
    }
}