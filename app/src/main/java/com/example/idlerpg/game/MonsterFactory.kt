package com.example.idlerpg.game

import com.example.idlerpg.models.Monster
import com.example.idlerpg.models.MonsterAbility
import com.example.idlerpg.models.MonsterType
import kotlin.random.Random

object MonsterFactory {
    
    // Fixed-level monster templates - no more automatic scaling
    private val monsterTemplates = listOf(
        MonsterTemplate(
            name = "Forest Wolf",
            type = MonsterType.BEAST,
            level = 1,
            hp = 40,
            attack = 12,
            defense = 2,
            attackSpeed = 1800L, // Fast attacker
            exp = 20,
            coins = 10,
            ability = MonsterAbility.CRITICAL_STRIKE,
            abilityPower = 25, // 25% crit chance
            abilityCooldown = 0,
            description = "A fierce wolf with sharp fangs and keen hunting instincts."
        ),
        MonsterTemplate(
            name = "Skeleton Warrior",
            type = MonsterType.UNDEAD,
            level = 2,
            hp = 60,
            attack = 18,
            defense = 8,
            attackSpeed = 2200L, // Slow but steady
            exp = 35,
            coins = 18,
            ability = MonsterAbility.ARMOR_PIERCE,
            abilityPower = 3, // Ignores 3 defense
            abilityCooldown = 0,
            description = "An undead warrior wielding ancient weapons."
        ),
        MonsterTemplate(
            name = "Fire Elemental",
            type = MonsterType.ELEMENTAL,
            level = 3,
            hp = 45,
            attack = 22,
            defense = 3,
            attackSpeed = 1600L, // Very fast
            exp = 50,
            coins = 25,
            ability = MonsterAbility.POISON_ATTACK,
            abilityPower = 8, // 8 burn damage per turn
            abilityCooldown = 0,
            description = "A blazing creature of pure fire that burns everything it touches."
        ),
        MonsterTemplate(
            name = "Goblin Shaman",
            type = MonsterType.HUMANOID,
            level = 2,
            hp = 35,
            attack = 14,
            defense = 6,
            attackSpeed = 2000L, // Average speed
            exp = 30,
            coins = 15,
            ability = MonsterAbility.MAGIC_SHIELD,
            abilityPower = 3, // Reduces damage by 3
            abilityCooldown = 0,
            description = "A cunning goblin that weaves protective magic spells."
        ),
        MonsterTemplate(
            name = "Troll Berserker",
            type = MonsterType.HUMANOID,
            level = 4,
            hp = 120,
            attack = 25,
            defense = 10,
            attackSpeed = 3000L, // Very slow but powerful
            exp = 80,
            coins = 40,
            ability = MonsterAbility.BERSERKER_RAGE,
            abilityPower = 15, // +15 attack when below 50% HP
            abilityCooldown = 0,
            description = "A massive troll that becomes more dangerous when wounded."
        ),
        MonsterTemplate(
            name = "Vampire Bat",
            type = MonsterType.UNDEAD,
            level = 1,
            hp = 25,
            attack = 10,
            defense = 1,
            attackSpeed = 1400L, // Very fast
            exp = 15,
            coins = 8,
            ability = MonsterAbility.LIFE_STEAL,
            abilityPower = 30, // Steals 30% of damage as health
            abilityCooldown = 0,
            description = "A bloodthirsty creature that feeds on life force."
        ),
        MonsterTemplate(
            name = "Stone Golem",
            type = MonsterType.CONSTRUCT,
            level = 5,
            hp = 200,
            attack = 30,
            defense = 20,
            attackSpeed = 4000L, // Very slow but devastating
            exp = 120,
            coins = 60,
            ability = MonsterAbility.STUN_CHANCE,
            abilityPower = 25, // 25% chance to stun
            abilityCooldown = 3,
            description = "An ancient construct of living stone with crushing blows."
        ),
        MonsterTemplate(
            name = "Thornling",
            type = MonsterType.PLANT,
            level = 3,
            hp = 80,
            attack = 16,
            defense = 12,
            attackSpeed = 2500L, // Slow but tanky
            exp = 55,
            coins = 28,
            ability = MonsterAbility.REGENERATION,
            abilityPower = 5, // Heals 5 HP per turn
            abilityCooldown = 0,
            description = "A plant creature covered in poisonous thorns that heals over time."
        ),
        MonsterTemplate(
            name = "Shadow Demon",
            type = MonsterType.DEMON,
            level = 4,
            hp = 70,
            attack = 28,
            defense = 8,
            attackSpeed = 1200L, // Extremely fast
            exp = 75,
            coins = 35,
            ability = MonsterAbility.DOUBLE_ATTACK,
            abilityPower = 0,
            abilityCooldown = 3, // Attacks twice every 3 turns
            description = "A dark entity from the shadow realm with lightning-fast strikes."
        ),
        MonsterTemplate(
            name = "Young Dragon",
            type = MonsterType.DRAGON,
            level = 6,
            hp = 300,
            attack = 45,
            defense = 25,
            attackSpeed = 3500L, // Slow but extremely powerful
            exp = 200,
            coins = 100,
            ability = MonsterAbility.CRITICAL_STRIKE,
            abilityPower = 50, // 50% crit chance
            abilityCooldown = 0,
            description = "A juvenile dragon with devastating breath attacks and razor-sharp claws."
        )
    )
    
    // Data class for monster templates
    private data class MonsterTemplate(
        val name: String,
        val type: MonsterType,
        val level: Int,
        val hp: Int,
        val attack: Int,
        val defense: Int,
        val attackSpeed: Long,
        val exp: Int,
        val coins: Int,
        val ability: MonsterAbility,
        val abilityPower: Int,
        val abilityCooldown: Int,
        val description: String
    )
    
    /**
     * Gets all available monsters for selection
     */
    fun getAllMonsters(): List<Monster> {
        return monsterTemplates.map { createMonsterFromTemplate(it) }
    }
    
    /**
     * Creates a specific monster by name
     */
    fun createMonsterByName(name: String): Monster? {
        val template = monsterTemplates.find { it.name == name } ?: return null
        return createMonsterFromTemplate(template)
    }
    
    /**
     * Gets all available monster names
     */
    fun getAllMonsterNames(): List<String> {
        return monsterTemplates.map { it.name }
    }
    
    /**
     * Creates a monster from a template with fixed stats
     */
    private fun createMonsterFromTemplate(template: MonsterTemplate): Monster {
        return Monster(
            name = template.name,
            type = template.type,
            hp = template.hp,
            maxHp = template.hp,
            attack = template.attack,
            defense = template.defense,
            attackSpeed = template.attackSpeed,
            experienceReward = template.exp,
            coinReward = template.coins,
            ability = template.ability,
            abilityPower = template.abilityPower,
            abilityCooldown = template.abilityCooldown,
            currentCooldown = 0,
            lastAttackTime = 0L,
            description = template.description,
            level = template.level
        )
    }
    
    /**
     * Creates a boss version of a specific monster (enhanced stats and abilities)
     */
    fun createBossMonster(monsterName: String): Monster? {
        val baseMonster = createMonsterByName(monsterName) ?: return null
        
        return baseMonster.copy(
            name = "Elite ${baseMonster.name}",
            hp = (baseMonster.hp * 1.5).toInt(),
            maxHp = (baseMonster.maxHp * 1.5).toInt(),
            attack = (baseMonster.attack * 1.3).toInt(),
            defense = (baseMonster.defense * 1.2).toInt(),
            attackSpeed = (baseMonster.attackSpeed * 1.2).toLong(), // Slightly slower
            experienceReward = (baseMonster.experienceReward * 2).toInt(),
            coinReward = (baseMonster.coinReward * 2).toInt(),
            abilityPower = (baseMonster.abilityPower * 1.2).toInt(),
            description = "An elite version of ${baseMonster.name} with enhanced abilities.",
            level = baseMonster.level + 1
        )
    }
}