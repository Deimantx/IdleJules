package com.example.idlerpg.game

import com.example.idlerpg.models.*

object ShopItems {
    val allItems: List<GearItem> = listOf(
        // ==================== SWORDS ====================
        GearItem("Rusty Sword", ItemType.WEAPON, attackBonus = 5, cost = 50, attackSpeedBonus = -100f, weaponType = WeaponType.SWORD, weaponSubType = WeaponSubType.ONE_HANDED_SWORD, levelRequirement = 1, description = "A worn but reliable blade"),
        GearItem("Iron Sword", ItemType.WEAPON, attackBonus = 12, cost = 200, attackSpeedBonus = -150f, critRateBonus = 2f, weaponType = WeaponType.SWORD, weaponSubType = WeaponSubType.ONE_HANDED_SWORD, levelRequirement = 3, description = "A sturdy iron blade"),
        GearItem("Steel Sword", ItemType.WEAPON, attackBonus = 18, cost = 500, attackSpeedBonus = -200f, critRateBonus = 5f, weaponType = WeaponType.SWORD, weaponSubType = WeaponSubType.ONE_HANDED_SWORD, levelRequirement = 6, description = "A well-crafted steel sword"),
        GearItem("Enchanted Blade", ItemType.WEAPON, attackBonus = 25, cost = 1200, attackSpeedBonus = -250f, critRateBonus = 8f, critDamageBonus = 0.2f, weaponType = WeaponType.SWORD, weaponSubType = WeaponSubType.ONE_HANDED_SWORD, levelRequirement = 10, rarity = ItemRarity.RARE, description = "A magically enhanced sword"),
        GearItem("Dragonslayer Sword", ItemType.WEAPON, attackBonus = 35, cost = 2500, attackSpeedBonus = -300f, critRateBonus = 12f, critDamageBonus = 0.3f, weaponType = WeaponType.SWORD, weaponSubType = WeaponSubType.ONE_HANDED_SWORD, levelRequirement = 15, rarity = ItemRarity.EPIC, description = "Forged to slay dragons"),
        
        // Rapiers - High crit, fast, low damage
        GearItem("Training Rapier", ItemType.WEAPON, attackBonus = 6, cost = 100, attackSpeedBonus = -300f, critRateBonus = 12f, weaponType = WeaponType.SWORD, weaponSubType = WeaponSubType.RAPIER, levelRequirement = 2, description = "Swift piercing blade for beginners"),
        GearItem("Dueling Rapier", ItemType.WEAPON, attackBonus = 10, cost = 350, attackSpeedBonus = -400f, critRateBonus = 18f, critDamageBonus = 0.1f, weaponType = WeaponType.SWORD, weaponSubType = WeaponSubType.RAPIER, levelRequirement = 5, description = "Elegant weapon of nobles"),
        GearItem("Master's Rapier", ItemType.WEAPON, attackBonus = 16, cost = 900, attackSpeedBonus = -500f, critRateBonus = 25f, critDamageBonus = 0.2f, weaponType = WeaponType.SWORD, weaponSubType = WeaponSubType.RAPIER, levelRequirement = 8, rarity = ItemRarity.RARE, description = "Masterwork dueling blade"),
        
        // ==================== DAGGERS ====================
        GearItem("Rusty Dagger", ItemType.WEAPON, attackBonus = 8, cost = 80, attackSpeedBonus = -400f, critRateBonus = 8f, weaponType = WeaponType.DAGGER, levelRequirement = 1, description = "Quick but weak"),
        GearItem("Steel Dagger", ItemType.WEAPON, attackBonus = 12, cost = 300, attackSpeedBonus = -500f, critRateBonus = 15f, weaponType = WeaponType.DAGGER, levelRequirement = 3, description = "Swift and deadly"),
        GearItem("Shadow Blade", ItemType.WEAPON, attackBonus = 16, cost = 800, attackSpeedBonus = -600f, critRateBonus = 20f, critDamageBonus = 0.4f, weaponType = WeaponType.DAGGER, levelRequirement = 6, rarity = ItemRarity.RARE, description = "Strikes from the shadows"),
        GearItem("Venom Fang", ItemType.WEAPON, attackBonus = 20, cost = 1500, attackSpeedBonus = -700f, critRateBonus = 25f, critDamageBonus = 0.5f, weaponType = WeaponType.DAGGER, levelRequirement = 10, rarity = ItemRarity.EPIC, statusEffectChance = 15f, statusEffectType = "poison", description = "Drips with deadly poison"),
        
        // ==================== AXES ====================
        GearItem("Woodcutter's Axe", ItemType.WEAPON, attackBonus = 10, cost = 120, attackSpeedBonus = -50f, critDamageBonus = 0.1f, weaponType = WeaponType.AXE, weaponSubType = WeaponSubType.HAND_AXE, levelRequirement = 2, description = "Simple but effective chopping tool"),
        GearItem("Battle Axe", ItemType.WEAPON, attackBonus = 18, cost = 450, attackSpeedBonus = 0f, critDamageBonus = 0.2f, armorPiercing = 2, weaponType = WeaponType.AXE, weaponSubType = WeaponSubType.BATTLE_AXE, levelRequirement = 5, description = "Cleaves through armor"),
        GearItem("Berserker's Axe", ItemType.WEAPON, attackBonus = 26, cost = 900, attackSpeedBonus = 100f, critDamageBonus = 0.3f, armorPiercing = 4, weaponType = WeaponType.AXE, weaponSubType = WeaponSubType.BATTLE_AXE, levelRequirement = 8, rarity = ItemRarity.RARE, description = "Fueled by rage"),
        GearItem("Executioner's Axe", ItemType.WEAPON, attackBonus = 35, cost = 1800, attackSpeedBonus = 200f, critDamageBonus = 0.4f, armorPiercing = 6, weaponType = WeaponType.AXE, weaponSubType = WeaponSubType.BATTLE_AXE, levelRequirement = 12, rarity = ItemRarity.EPIC, description = "Ends lives with single strikes"),
        
        // ==================== MACES ====================
        GearItem("Wooden Club", ItemType.WEAPON, attackBonus = 8, cost = 60, attackSpeedBonus = 100f, weaponType = WeaponType.MACE, weaponSubType = WeaponSubType.SPIKED_CLUB, levelRequirement = 1, description = "Primitive but effective"),
        GearItem("Iron Mace", ItemType.WEAPON, attackBonus = 15, cost = 300, attackSpeedBonus = 150f, armorPiercing = 3, weaponType = WeaponType.MACE, weaponSubType = WeaponSubType.WAR_MACE, levelRequirement = 4, description = "Crushes through armor"),
        GearItem("Morning Star", ItemType.WEAPON, attackBonus = 28, cost = 1200, attackSpeedBonus = 250f, critRateBonus = 18f, armorPiercing = 6, statusEffectChance = 10f, statusEffectType = "stun", weaponType = WeaponType.MACE, weaponSubType = WeaponSubType.MORNING_STAR, levelRequirement = 10, rarity = ItemRarity.RARE, description = "Spiked ball of destruction"),
        
        // ==================== STAVES ====================
        GearItem("Apprentice Staff", ItemType.WEAPON, attackBonus = 4, cost = 100, manaBonus = 25, intelligenceBonus = 1, weaponType = WeaponType.STAFF, weaponSubType = WeaponSubType.BATTLE_STAFF, levelRequirement = 2, description = "Simple wooden staff"),
        GearItem("Wizard's Staff", ItemType.WEAPON, attackBonus = 8, cost = 400, manaBonus = 50, intelligenceBonus = 2, critRateBonus = 5f, weaponType = WeaponType.STAFF, weaponSubType = WeaponSubType.WIZARD_STAFF, levelRequirement = 5, description = "Channels magical energy"),
        GearItem("Arcane Staff", ItemType.WEAPON, attackBonus = 12, cost = 900, manaBonus = 80, intelligenceBonus = 3, critRateBonus = 8f, critDamageBonus = 0.2f, weaponType = WeaponType.STAFF, weaponSubType = WeaponSubType.WIZARD_STAFF, levelRequirement = 8, rarity = ItemRarity.RARE, description = "Pulsing with arcane power"),
        
        // ==================== BOWS ====================
        GearItem("Training Bow", ItemType.WEAPON, attackBonus = 10, cost = 200, attackSpeedBonus = -100f, hitBonus = 8f, weaponType = WeaponType.BOW, weaponSubType = WeaponSubType.SHORT_BOW, levelRequirement = 3, description = "Simple bow for beginners"),
        GearItem("Hunter's Bow", ItemType.WEAPON, attackBonus = 16, cost = 500, attackSpeedBonus = -150f, hitBonus = 12f, critRateBonus = 8f, weaponType = WeaponType.BOW, weaponSubType = WeaponSubType.SHORT_BOW, levelRequirement = 6, description = "Reliable hunting weapon"),
        GearItem("Elven Bow", ItemType.WEAPON, attackBonus = 32, cost = 2000, attackSpeedBonus = -50f, hitBonus = 20f, critRateBonus = 18f, critDamageBonus = 0.3f, weaponType = WeaponType.BOW, weaponSubType = WeaponSubType.COMPOSITE_BOW, levelRequirement = 12, rarity = ItemRarity.RARE, description = "Crafted by elven masters"),
        
        // ==================== EXOTIC WEAPONS ====================
        GearItem("Katana", ItemType.WEAPON, attackBonus = 22, cost = 1000, attackSpeedBonus = -200f, critRateBonus = 20f, critDamageBonus = 0.3f, weaponType = WeaponType.KATANA, levelRequirement = 8, rarity = ItemRarity.RARE, description = "Curved blade of the east"),
        GearItem("War Scythe", ItemType.WEAPON, attackBonus = 28, cost = 1500, attackSpeedBonus = 300f, critRateBonus = 18f, critDamageBonus = 0.4f, weaponType = WeaponType.SCYTHE, levelRequirement = 11, rarity = ItemRarity.RARE, description = "Reaper's weapon of death"),
        GearItem("Iron Claws", ItemType.WEAPON, attackBonus = 14, cost = 400, attackSpeedBonus = -500f, critRateBonus = 25f, critDamageBonus = 0.2f, weaponType = WeaponType.CLAW, levelRequirement = 5, description = "Dual-wielded slashing weapons"),
        
        // ==================== TWO-HANDED WEAPONS ====================
        GearItem("Iron Maul", ItemType.WEAPON, attackBonus = 22, cost = 400, attackSpeedBonus = 500f, critDamageBonus = 0.3f, weaponType = WeaponType.TWO_HANDED, weaponSubType = WeaponSubType.WARHAMMER, levelRequirement = 4, description = "Crushes enemies with raw power"),
        GearItem("Steel Greatsword", ItemType.WEAPON, attackBonus = 30, cost = 800, attackSpeedBonus = 400f, critDamageBonus = 0.4f, weaponType = WeaponType.TWO_HANDED, weaponSubType = WeaponSubType.GREATSWORD, levelRequirement = 7, description = "Massive two-handed blade"),
        GearItem("Godslayer Greatsword", ItemType.WEAPON, attackBonus = 60, cost = 5000, attackSpeedBonus = 600f, critRateBonus = 12f, critDamageBonus = 0.7f, armorPiercing = 10, weaponType = WeaponType.TWO_HANDED, weaponSubType = WeaponSubType.GREATSWORD, levelRequirement = 20, rarity = ItemRarity.LEGENDARY, description = "Forged to kill gods"),
        
        // ==================== ARMOR ====================
        // Light Armor
        GearItem("Cloth Robes", ItemType.ARMOR, defenseBonus = 2, cost = 40, manaBonus = 20, intelligenceBonus = 1, armorType = ArmorType.LIGHT, levelRequirement = 1, description = "Comfortable robes for spellcasters"),
        GearItem("Leather Vest", ItemType.ARMOR, defenseBonus = 5, cost = 120, manaBonus = 15, dodgeBonus = 3f, agilityBonus = 1, armorType = ArmorType.LIGHT, levelRequirement = 2, description = "Light and flexible protection"),
        GearItem("Studded Leather", ItemType.ARMOR, defenseBonus = 8, cost = 300, manaBonus = 25, dodgeBonus = 5f, agilityBonus = 2, armorType = ArmorType.LIGHT, levelRequirement = 4, description = "Reinforced leather armor"),
        GearItem("Archmage Robes", ItemType.ARMOR, defenseBonus = 15, cost = 1500, manaBonus = 80, dodgeBonus = 6f, intelligenceBonus = 4, armorType = ArmorType.LIGHT, levelRequirement = 10, rarity = ItemRarity.RARE, description = "Robes of magical mastery"),
        
        // Medium Armor
        GearItem("Chainmail Shirt", ItemType.ARMOR, defenseBonus = 10, cost = 250, dodgeBonus = 4f, armorType = ArmorType.MEDIUM, levelRequirement = 3, description = "Balanced protection and mobility"),
        GearItem("Scale Mail", ItemType.ARMOR, defenseBonus = 15, cost = 600, dodgeBonus = 6f, strengthBonus = 1, armorType = ArmorType.MEDIUM, levelRequirement = 5, description = "Overlapping metal scales"),
        GearItem("Mithril Chainmail", ItemType.ARMOR, defenseBonus = 28, cost = 2500, dodgeBonus = 12f, strengthBonus = 3, agilityBonus = 2, armorType = ArmorType.MEDIUM, levelRequirement = 12, rarity = ItemRarity.EPIC, description = "Legendary lightweight metal"),
        
        // Heavy Armor
        GearItem("Iron Plate", ItemType.ARMOR, defenseBonus = 18, cost = 500, hpBonus = 30, attackSpeedBonus = 200f, vitalityBonus = 2, armorType = ArmorType.HEAVY, levelRequirement = 4, description = "Heavy but protective"),
        GearItem("Steel Plate", ItemType.ARMOR, defenseBonus = 25, cost = 1000, hpBonus = 50, attackSpeedBonus = 300f, vitalityBonus = 3, armorType = ArmorType.HEAVY, levelRequirement = 7, description = "Superior heavy armor"),
        GearItem("Adamantine Plate", ItemType.ARMOR, defenseBonus = 45, cost = 4000, hpBonus = 120, attackSpeedBonus = 400f, vitalityBonus = 6, strengthBonus = 3, armorType = ArmorType.HEAVY, levelRequirement = 15, rarity = ItemRarity.LEGENDARY, description = "Unbreakable metal armor"),
        
        // Specialized Armor
        GearItem("Assassin's Garb", ItemType.ARMOR, defenseBonus = 8, cost = 800, dodgeBonus = 15f, critRateBonus = 10f, agilityBonus = 3, armorType = ArmorType.STEALTH, levelRequirement = 8, rarity = ItemRarity.RARE, description = "Silent death's uniform"),
        GearItem("Mage's Vestments", ItemType.ARMOR, defenseBonus = 6, cost = 600, manaBonus = 100, critRateBonus = 8f, intelligenceBonus = 3, spiritBonus = 2, armorType = ArmorType.MAGE, levelRequirement = 6, rarity = ItemRarity.RARE, description = "Robes of magical power"),
        GearItem("Berserker's Hide", ItemType.ARMOR, defenseBonus = 15, cost = 1000, attackBonus = 10, critDamageBonus = 0.2f, strengthBonus = 4, armorType = ArmorType.BERSERKER, levelRequirement = 9, rarity = ItemRarity.RARE, description = "Armor of the wild warriors"),
        GearItem("Paladin's Mail", ItemType.ARMOR, defenseBonus = 22, cost = 1200, hpBonus = 40, manaBonus = 30, vitalityBonus = 3, spiritBonus = 2, armorType = ArmorType.PALADIN, levelRequirement = 10, rarity = ItemRarity.RARE, description = "Blessed armor of holy warriors"),
        
        // ==================== SHIELDS ====================
        GearItem("Wooden Shield", ItemType.SHIELD, defenseBonus = 3, cost = 60, levelRequirement = 1, description = "Basic wooden protection"),
        GearItem("Iron Shield", ItemType.SHIELD, defenseBonus = 6, cost = 180, dodgeBonus = 2f, levelRequirement = 3, description = "Sturdy iron shield"),
        GearItem("Steel Shield", ItemType.SHIELD, defenseBonus = 10, cost = 400, dodgeBonus = 4f, levelRequirement = 5, description = "Well-crafted steel shield"),
        GearItem("Tower Shield", ItemType.SHIELD, defenseBonus = 15, cost = 800, hpBonus = 25, vitalityBonus = 2, levelRequirement = 7, description = "Massive protective shield"),
        GearItem("Aegis of Valor", ItemType.SHIELD, defenseBonus = 20, cost = 1600, dodgeBonus = 8f, hpBonus = 40, vitalityBonus = 3, levelRequirement = 10, rarity = ItemRarity.RARE, description = "Legendary protective aegis"),
        
        // Bucklers
        GearItem("Steel Buckler", ItemType.SHIELD, defenseBonus = 5, cost = 300, dodgeBonus = 12f, agilityBonus = 2, critRateBonus = 3f, levelRequirement = 5, description = "Swift parrying shield"),
        GearItem("Duelist's Buckler", ItemType.SHIELD, defenseBonus = 8, cost = 700, dodgeBonus = 18f, agilityBonus = 3, critRateBonus = 6f, levelRequirement = 8, rarity = ItemRarity.RARE, description = "Perfect for dueling"),
        
        // Magical Shields
        GearItem("Mage Shield", ItemType.SHIELD, defenseBonus = 6, cost = 500, manaBonus = 40, intelligenceBonus = 2, levelRequirement = 6, description = "Magical barrier shield"),
        GearItem("Elemental Ward", ItemType.SHIELD, defenseBonus = 12, cost = 1200, manaBonus = 60, intelligenceBonus = 3, elementalDamage = 5, levelRequirement = 10, rarity = ItemRarity.RARE, description = "Protects against elements"),
        
        // ==================== RINGS ====================
        GearItem("Copper Ring", ItemType.RING, cost = 80, hitBonus = 2f, accessoryType = AccessoryType.STAT_RING, levelRequirement = 1, description = "Simple copper band"),
        GearItem("Ring of Strength", ItemType.RING, cost = 300, strengthBonus = 2, attackBonus = 4, accessoryType = AccessoryType.STAT_RING, levelRequirement = 4, description = "Enhances physical power"),
        GearItem("Ring of Agility", ItemType.RING, cost = 250, agilityBonus = 2, dodgeBonus = 4f, critRateBonus = 5f, accessoryType = AccessoryType.STAT_RING, levelRequirement = 4, description = "Increases nimbleness"),
        GearItem("Ring of Intelligence", ItemType.RING, cost = 400, intelligenceBonus = 2, manaBonus = 30, accessoryType = AccessoryType.STAT_RING, levelRequirement = 4, description = "Boosts mental acuity"),
        GearItem("Ring of Power", ItemType.RING, cost = 500, attackBonus = 6, critDamageBonus = 0.1f, accessoryType = AccessoryType.COMBAT_RING, levelRequirement = 5, description = "Amplifies strength"),
        GearItem("Ring of Protection", ItemType.RING, cost = 400, defenseBonus = 4, hpBonus = 25, accessoryType = AccessoryType.COMBAT_RING, levelRequirement = 5, description = "Provides magical protection"),
        GearItem("Master's Signet", ItemType.RING, cost = 1500, attackBonus = 10, defenseBonus = 6, hitBonus = 8f, accessoryType = AccessoryType.COMBAT_RING, levelRequirement = 12, rarity = ItemRarity.RARE, description = "Ring of a legendary warrior"),
        
        // ==================== AMULETS ====================
        GearItem("Copper Amulet", ItemType.AMULET, cost = 100, attackBonus = 3, accessoryType = AccessoryType.POWER_AMULET, levelRequirement = 1, description = "Simple copper charm"),
        GearItem("Silver Pendant", ItemType.AMULET, cost = 300, manaBonus = 20, critRateBonus = 3f, accessoryType = AccessoryType.WISDOM_AMULET, levelRequirement = 3, description = "Mystical silver pendant"),
        GearItem("Amulet of Strength", ItemType.AMULET, cost = 600, attackBonus = 8, critDamageBonus = 0.15f, strengthBonus = 2, accessoryType = AccessoryType.POWER_AMULET, levelRequirement = 6, description = "Enhances physical power"),
        GearItem("Pendant of Vitality", ItemType.AMULET, cost = 800, hpBonus = 50, defenseBonus = 5, vitalityBonus = 2, accessoryType = AccessoryType.PROTECTION_AMULET, levelRequirement = 7, description = "Grants life force"),
        GearItem("Arcane Medallion", ItemType.AMULET, cost = 1200, manaBonus = 60, critRateBonus = 8f, intelligenceBonus = 3, accessoryType = AccessoryType.WISDOM_AMULET, levelRequirement = 8, rarity = ItemRarity.RARE, description = "Pulses with magical energy"),
        GearItem("Heart of the Dragon", ItemType.AMULET, cost = 2500, attackBonus = 15, hpBonus = 80, critDamageBonus = 0.3f, strengthBonus = 4, accessoryType = AccessoryType.POWER_AMULET, levelRequirement = 15, rarity = ItemRarity.LEGENDARY, description = "Contains a dragon's essence"),
        
        // ==================== BELTS ====================
        GearItem("Leather Belt", ItemType.BELT, cost = 100, strengthBonus = 1, accessoryType = AccessoryType.LEATHER_BELT, levelRequirement = 2, description = "Simple leather belt"),
        GearItem("Studded Belt", ItemType.BELT, cost = 250, strengthBonus = 2, attackBonus = 3, accessoryType = AccessoryType.LEATHER_BELT, levelRequirement = 4, description = "Reinforced with metal studs"),
        GearItem("Chain Belt", ItemType.BELT, cost = 400, defenseBonus = 4, vitalityBonus = 2, accessoryType = AccessoryType.CHAIN_BELT, levelRequirement = 5, description = "Metal chain belt for protection"),
        GearItem("Warrior's Belt", ItemType.BELT, cost = 700, attackBonus = 6, strengthBonus = 3, critDamageBonus = 0.1f, accessoryType = AccessoryType.CHAIN_BELT, levelRequirement = 7, description = "Belt of a seasoned warrior"),
        GearItem("Mage's Sash", ItemType.BELT, cost = 800, manaBonus = 50, intelligenceBonus = 3, spiritBonus = 2, accessoryType = AccessoryType.MAGICAL_SASH, levelRequirement = 8, description = "Silk sash of spellcasters"),
        
        // ==================== GLOVES ====================
        GearItem("Leather Gloves", ItemType.GLOVES, cost = 120, agilityBonus = 1, hitBonus = 3f, accessoryType = AccessoryType.LEATHER_GLOVES, levelRequirement = 2, description = "Flexible leather handwear"),
        GearItem("Chain Gauntlets", ItemType.GLOVES, cost = 350, defenseBonus = 3, strengthBonus = 2, accessoryType = AccessoryType.CHAIN_GAUNTLETS, levelRequirement = 5, description = "Metal-reinforced protection"),
        GearItem("Mage Gloves", ItemType.GLOVES, cost = 500, manaBonus = 30, intelligenceBonus = 2, critRateBonus = 5f, accessoryType = AccessoryType.MAGE_GLOVES, levelRequirement = 6, description = "Enhance spellcasting"),
        GearItem("Berserker Gauntlets", ItemType.GLOVES, cost = 900, attackBonus = 8, strengthBonus = 4, critDamageBonus = 0.15f, accessoryType = AccessoryType.BERSERKER_GAUNTLETS, levelRequirement = 9, rarity = ItemRarity.RARE, description = "Brutal fighting gloves"),
        
        // ==================== BOOTS ====================
        GearItem("Leather Boots", ItemType.BOOTS, cost = 100, agilityBonus = 1, dodgeBonus = 2f, accessoryType = AccessoryType.LEATHER_BOOTS, levelRequirement = 2, description = "Comfortable walking boots"),
        GearItem("Iron Boots", ItemType.BOOTS, cost = 400, defenseBonus = 4, vitalityBonus = 2, attackSpeedBonus = 100f, accessoryType = AccessoryType.IRON_BOOTS, levelRequirement = 5, description = "Heavy but protective"),
        GearItem("Stealth Boots", ItemType.BOOTS, cost = 600, agilityBonus = 3, dodgeBonus = 8f, critRateBonus = 6f, accessoryType = AccessoryType.STEALTH_BOOTS, levelRequirement = 6, description = "Silent as shadows"),
        GearItem("Boots of Speed", ItemType.BOOTS, cost = 1000, agilityBonus = 4, attackSpeedBonus = -300f, dodgeBonus = 10f, accessoryType = AccessoryType.MAGICAL_BOOTS, levelRequirement = 9, rarity = ItemRarity.RARE, description = "Swift as the wind"),
        
        // ==================== CLOAKS ====================
        GearItem("Travel Cloak", ItemType.CLOAK, cost = 150, agilityBonus = 1, dodgeBonus = 3f, accessoryType = AccessoryType.TRAVEL_CLOAK, levelRequirement = 2, description = "Protects from weather"),
        GearItem("Battle Cloak", ItemType.CLOAK, cost = 500, defenseBonus = 4, strengthBonus = 2, accessoryType = AccessoryType.BATTLE_CLOAK, levelRequirement = 5, description = "Reinforced for combat"),
        GearItem("Mage Cloak", ItemType.CLOAK, cost = 600, manaBonus = 40, intelligenceBonus = 2, spiritBonus = 1, accessoryType = AccessoryType.MAGE_CLOAK, levelRequirement = 6, description = "Channels magical energy"),
        GearItem("Shadow Cloak", ItemType.CLOAK, cost = 800, agilityBonus = 3, dodgeBonus = 10f, critRateBonus = 8f, accessoryType = AccessoryType.SHADOW_CLOAK, levelRequirement = 7, description = "Blend with darkness"),
        GearItem("Cloak of Invisibility", ItemType.CLOAK, cost = 1800, agilityBonus = 5, dodgeBonus = 15f, critRateBonus = 12f, critDamageBonus = 0.2f, accessoryType = AccessoryType.SHADOW_CLOAK, levelRequirement = 12, rarity = ItemRarity.EPIC, description = "Become one with shadows")
    )
}