package com.example.idlerpg.models

enum class ItemType {
    WEAPON,
    ARMOR,
    SHIELD,
    AMULET,
    RING,
    BELT,        // NEW
    GLOVES,      // NEW
    BOOTS,       // NEW
    CLOAK        // NEW
}

enum class WeaponType {
    SWORD,
    DAGGER,
    TWO_HANDED,
    
    // New weapon types
    AXE,
    MACE,
    STAFF,
    WAND,
    WHIP,
    BOW,
    CROSSBOW,
    SPEAR,
    POLEARM,
    KATANA,
    SCYTHE,
    CLAW,
    THROWING_WEAPON,
    EXOTIC
}

enum class WeaponSubType {
    // Sword subtypes
    ONE_HANDED_SWORD,
    RAPIER,
    SCIMITAR,
    BROADSWORD,
    
    // Dagger subtypes
    THROWING_KNIFE,
    POISON_DAGGER,
    PARRYING_DAGGER,
    
    // Two-handed subtypes
    GREATSWORD,
    WARHAMMER,
    BATTLEAXE,
    HALBERD,
    
    // Axe subtypes
    HAND_AXE,
    BATTLE_AXE,
    THROWING_AXE,
    BEARDED_AXE,
    
    // Mace subtypes
    WAR_MACE,
    SPIKED_CLUB,
    MORNING_STAR,
    FLANGED_MACE,
    
    // Staff subtypes
    BATTLE_STAFF,
    WIZARD_STAFF,
    WAR_STAFF,
    MAGIC_WAND,
    
    // Bow subtypes
    SHORT_BOW,
    LONG_BOW,
    COMPOSITE_BOW,
    
    // Spear subtypes
    SHORT_SPEAR,
    LONG_SPEAR,
    TRIDENT,
    GLAIVE,
    
    // Exotic subtypes
    CHAKRAM,
    BOOMERANG,
    THROWING_STAR,
    JAVELIN,
    SLING,
    
    // Default
    NONE
}

enum class ArmorType {
    LIGHT,
    MEDIUM,
    HEAVY,
    
    // New specialized types
    STEALTH,     // Assassin gear
    MAGE,        // Spell-focused
    BERSERKER,   // Attack-focused
    PALADIN      // Holy-themed
}

enum class AccessoryType {
    // Ring types
    STAT_RING,
    COMBAT_RING,
    UTILITY_RING,
    RESISTANCE_RING,
    SET_RING,
    
    // Amulet types
    POWER_AMULET,
    PROTECTION_AMULET,
    WISDOM_AMULET,
    FORTUNE_AMULET,
    ELEMENTAL_AMULET,
    
    // Belt types
    LEATHER_BELT,
    CHAIN_BELT,
    UTILITY_BELT,
    MAGICAL_SASH,
    
    // Glove types
    LEATHER_GLOVES,
    CHAIN_GAUNTLETS,
    MAGE_GLOVES,
    BERSERKER_GAUNTLETS,
    
    // Boot types
    LEATHER_BOOTS,
    IRON_BOOTS,
    STEALTH_BOOTS,
    MAGICAL_BOOTS,
    
    // Cloak types
    TRAVEL_CLOAK,
    BATTLE_CLOAK,
    MAGE_CLOAK,
    SHADOW_CLOAK,
    
    // Default
    NONE
}

enum class ItemRarity {
    COMMON,      // White
    UNCOMMON,    // Green
    RARE,        // Blue
    EPIC,        // Purple
    LEGENDARY,   // Orange
    ARTIFACT     // Red
}

data class GearItem(
    val name: String,
    val type: ItemType,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val cost: Int = 0,
    val attackSpeedBonus: Float = 0f, // Reduces attack time (negative values speed up)
    val critRateBonus: Float = 0f, // Bonus crit rate
    val critDamageBonus: Float = 0f, // Bonus crit damage multiplier
    val dodgeBonus: Float = 0f, // Bonus dodge chance
    val hitBonus: Float = 0f, // Bonus hit chance
    val hpBonus: Int = 0, // Bonus HP
    val manaBonus: Int = 0, // Bonus Mana
    val weaponType: WeaponType? = null, // For weapons
    val weaponSubType: WeaponSubType = WeaponSubType.NONE, // Weapon subtype
    val armorType: ArmorType? = null, // For armor
    val accessoryType: AccessoryType = AccessoryType.NONE, // For accessories
    val rarity: ItemRarity = ItemRarity.COMMON, // Item rarity
    val levelRequirement: Int = 1, // Minimum level to use
    val description: String = "", // Item description
    
    // New stat bonuses
    val strengthBonus: Int = 0, // Direct stat bonuses
    val agilityBonus: Int = 0,
    val intelligenceBonus: Int = 0,
    val vitalityBonus: Int = 0,
    val spiritBonus: Int = 0,
    
    // Special properties
    val armorPiercing: Int = 0, // Ignores armor
    val lifeSteal: Float = 0f, // Percentage of damage returned as health
    val manaSteal: Float = 0f, // Percentage of damage returned as mana
    val elementalDamage: Int = 0, // Extra elemental damage
    val statusEffectChance: Float = 0f, // Chance to apply status effect
    val statusEffectType: String = "", // Type of status effect (poison, stun, etc.)
    
    // Set item properties
    val setName: String = "", // Name of the item set
    val setBonus: String = "" // Description of set bonus
) {
    // Helper function to get rarity color
    fun getRarityColor(): String {
        return when (rarity) {
            ItemRarity.COMMON -> "#FFFFFF"      // White
            ItemRarity.UNCOMMON -> "#00FF00"    // Green
            ItemRarity.RARE -> "#0080FF"        // Blue
            ItemRarity.EPIC -> "#8000FF"        // Purple
            ItemRarity.LEGENDARY -> "#FF8000"   // Orange
            ItemRarity.ARTIFACT -> "#FF0000"    // Red
        }
    }
    
    // Helper function to get weapon category for shop organization
    fun getWeaponCategory(): String {
        return when (weaponType) {
            WeaponType.SWORD, WeaponType.DAGGER, WeaponType.TWO_HANDED, 
            WeaponType.AXE, WeaponType.MACE, WeaponType.SPEAR, 
            WeaponType.POLEARM, WeaponType.KATANA, WeaponType.SCYTHE, 
            WeaponType.CLAW, WeaponType.WHIP -> "Melee Weapons"
            
            WeaponType.BOW, WeaponType.CROSSBOW, WeaponType.THROWING_WEAPON -> "Ranged Weapons"
            
            WeaponType.STAFF, WeaponType.WAND -> "Magic Weapons"
            
            WeaponType.EXOTIC -> "Exotic Weapons"
            
            else -> "Weapons"
        }
    }
}
