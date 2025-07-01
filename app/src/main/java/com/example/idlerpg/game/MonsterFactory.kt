package com.example.idlerpg.game

import com.example.idlerpg.models.Monster
import com.example.idlerpg.models.MonsterAbility
import com.example.idlerpg.models.MonsterType
import kotlin.random.Random

object MonsterFactory {
    
    // Base monster templates - these will be scaled based on player level
    private val monsterTemplates = listOf(
        MonsterTemplate(
            name = "Forest Wolf",
            type = MonsterType.BEAST,
            baseHp = 35,
            baseAttack = 8,
            baseDefense = 3,
            baseExp = 15,
            baseCoins = 8,
            ability = MonsterAbility.CRITICAL_STRIKE,
            abilityPower = 25, // 25% crit chance
            abilityCooldown = 0,
            description = "A fierce wolf with sharp fangs and keen hunting instincts."
        ),
        MonsterTemplate(
            name = "Skeleton Warrior",
            type = MonsterType.UNDEAD,
            baseHp = 40,
            baseAttack = 12,
            baseDefense = 6,
            baseExp = 20,
            baseCoins = 12,
            ability = MonsterAbility.ARMOR_PIERCE,
            abilityPower = 3, // Ignores 3 defense
            abilityCooldown = 0,
            description = "An undead warrior wielding ancient weapons."
        ),
        MonsterTemplate(
            name = "Fire Elemental",
            type = MonsterType.ELEMENTAL,
            baseHp = 30,
            baseAttack = 15,
            baseDefense = 2,
            baseExp = 25,
            baseCoins = 15,
            ability = MonsterAbility.POISON_ATTACK,
            abilityPower = 5, // 5 burn damage per turn
            abilityCooldown = 0,
            description = "A blazing creature of pure fire that burns everything it touches."
        ),
        MonsterTemplate(
            name = "Goblin Shaman",
            type = MonsterType.HUMANOID,
            baseHp = 25,
            baseAttack = 6,
            baseDefense = 4,
            baseExp = 18,
            baseCoins = 10,
            ability = MonsterAbility.MAGIC_SHIELD,
            abilityPower = 2, // Reduces damage by 2
            abilityCooldown = 0,
            description = "A cunning goblin that weaves protective magic spells."
        ),
        MonsterTemplate(
            name = "Troll Berserker",
            type = MonsterType.HUMANOID,
            baseHp = 60,
            baseAttack = 10,
            baseDefense = 5,
            baseExp = 30,
            baseCoins = 18,
            ability = MonsterAbility.BERSERKER_RAGE,
            abilityPower = 8, // +8 attack when below 50% HP
            abilityCooldown = 0,
            description = "A massive troll that becomes more dangerous when wounded."
        ),
        MonsterTemplate(
            name = "Vampire Bat",
            type = MonsterType.UNDEAD,
            baseHp = 20,
            baseAttack = 7,
            baseDefense = 1,
            baseExp = 12,
            baseCoins = 6,
            ability = MonsterAbility.LIFE_STEAL,
            abilityPower = 30, // Steals 30% of damage as health
            abilityCooldown = 0,
            description = "A bloodthirsty creature that feeds on life force."
        ),
        MonsterTemplate(
            name = "Stone Golem",
            type = MonsterType.CONSTRUCT,
            baseHp = 80,
            baseAttack = 14,
            baseDefense = 12,
            baseExp = 35,
            baseCoins = 25,
            ability = MonsterAbility.STUN_CHANCE,
            abilityPower = 20, // 20% chance to stun
            abilityCooldown = 3,
            description = "An ancient construct of living stone with crushing blows."
        ),
        MonsterTemplate(
            name = "Thornling",
            type = MonsterType.PLANT,
            baseHp = 45,
            baseAttack = 9,
            baseDefense = 7,
            baseExp = 22,
            baseCoins = 14,
            ability = MonsterAbility.REGENERATION,
            abilityPower = 3, // Heals 3 HP per turn
            abilityCooldown = 0,
            description = "A plant creature covered in poisonous thorns that heals over time."
        ),
        MonsterTemplate(
            name = "Shadow Demon",
            type = MonsterType.DEMON,
            baseHp = 35,
            baseAttack = 13,
            baseDefense = 4,
            baseExp = 28,
            baseCoins = 20,
            ability = MonsterAbility.DOUBLE_ATTACK,
            abilityPower = 0,
            abilityCooldown = 3, // Attacks twice every 3 turns
            description = "A dark entity from the shadow realm with lightning-fast strikes."
        ),
        MonsterTemplate(
            name = "Young Dragon",
            type = MonsterType.DRAGON,
            baseHp = 100,
            baseAttack = 18,
            baseDefense = 10,
            baseExp = 50,
            baseCoins = 35,
            ability = MonsterAbility.CRITICAL_STRIKE,
            abilityPower = 40, // 40% crit chance
            abilityCooldown = 0,
            description = "A juvenile dragon with devastating breath attacks and razor-sharp claws."
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
        val description: String
    )
    
    /**
     * Creates a random monster appropriate for the given player level
     */
    fun createRandomMonster(playerLevel: Int): Monster {
        // Filter monsters based on player level for appropriate difficulty
        val availableTemplates = when (playerLevel) {
            1, 2 -> monsterTemplates.filter { it.baseHp <= 40 } // Easier monsters
            3, 4, 5 -> monsterTemplates.filter { it.baseHp <= 60 } // Medium monsters
            6, 7, 8 -> monsterTemplates.filter { it.baseHp <= 80 } // Harder monsters
            else -> monsterTemplates // All monsters available
        }
        
        val template = availableTemplates.random()
        return createMonsterFromTemplate(template, playerLevel)
    }
    
    /**
     * Creates a specific monster by name, scaled to player level
     */
    fun createMonsterByName(name: String, playerLevel: Int): Monster? {
        val template = monsterTemplates.find { it.name == name } ?: return null
        return createMonsterFromTemplate(template, playerLevel)
    }
    
    /**
     * Gets all available monster names
     */
    fun getAllMonsterNames(): List<String> {
        return monsterTemplates.map { it.name }
    }
    
    /**
     * Creates a monster from a template, scaling stats based on player level
     */
    private fun createMonsterFromTemplate(template: MonsterTemplate, playerLevel: Int): Monster {
        // Scale factor based on player level (with some randomness)
        val scaleFactor = 1.0 + (playerLevel - 1) * 0.3 + Random.nextDouble(-0.1, 0.1)
        
        // Calculate scaled stats
        val scaledHp = (template.baseHp * scaleFactor).toInt().coerceAtLeast(1)
        val scaledAttack = (template.baseAttack * scaleFactor).toInt().coerceAtLeast(1)
        val scaledDefense = (template.baseDefense * scaleFactor).toInt().coerceAtLeast(0)
        val scaledExp = (template.baseExp * scaleFactor).toInt().coerceAtLeast(1)
        val scaledCoins = (template.baseCoins * scaleFactor).toInt().coerceAtLeast(1)
        
        // Scale ability power for some abilities
        val scaledAbilityPower = when (template.ability) {
            MonsterAbility.REGENERATION,
            MonsterAbility.POISON_ATTACK,
            MonsterAbility.ARMOR_PIERCE,
            MonsterAbility.MAGIC_SHIELD,
            MonsterAbility.BERSERKER_RAGE -> (template.abilityPower * scaleFactor).toInt().coerceAtLeast(1)
            else -> template.abilityPower // Percentage-based abilities don't scale
        }
        
        return Monster(
            name = template.name,
            type = template.type,
            hp = scaledHp,
            maxHp = scaledHp,
            attack = scaledAttack,
            defense = scaledDefense,
            experienceReward = scaledExp,
            coinReward = scaledCoins,
            ability = template.ability,
            abilityPower = scaledAbilityPower,
            abilityCooldown = template.abilityCooldown,
            currentCooldown = 0,
            description = template.description,
            level = playerLevel
        )
    }
    
    /**
     * Creates a boss version of a random monster (enhanced stats and abilities)
     */
    fun createBossMonster(playerLevel: Int): Monster {
        val baseMonster = createRandomMonster(playerLevel)
        
        return baseMonster.copy(
            name = "Elite ${baseMonster.name}",
            hp = (baseMonster.hp * 1.5).toInt(),
            maxHp = (baseMonster.maxHp * 1.5).toInt(),
            attack = (baseMonster.attack * 1.3).toInt(),
            defense = (baseMonster.defense * 1.2).toInt(),
            experienceReward = (baseMonster.experienceReward * 2).toInt(),
            coinReward = (baseMonster.coinReward * 2).toInt(),
            abilityPower = (baseMonster.abilityPower * 1.2).toInt(),
            description = "An elite version of ${baseMonster.name} with enhanced abilities."
        )
    }
}