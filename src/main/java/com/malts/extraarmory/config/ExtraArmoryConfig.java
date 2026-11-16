package com.malts.extraarmory.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class ExtraArmoryConfig {

    public static Configuration config;
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_TWILIGHT_FOREST = "twilightforest";
    public static final String CATEGORY_AETHER = "aether";
    public static final String CATEGORY_MODS = "mods";

    public static boolean enableCustomHitEffectsFix;
    public static boolean enableMusketBulletEffects;

    // Generic & Vanilla Mobs
    public static boolean enableCopper;
    public static boolean enableTin;
    public static boolean enableCopperChestLoot;
    public static boolean enableTinChestLoot;
    public static boolean enableCopperZombieWeapons;
    public static boolean enableTinZombieWeapons;
    public static boolean enablePigZombieGoldWeapons;
    public static float pigZombieGoldWeaponChance;
    public static boolean enableWitherSkeletonStoneWeapons;
    public static float witherSkeletonStoneWeaponChance;

    // Twilight Forest
    public static boolean enableTwilightForest;
    public static boolean enableIronwood;
    public static boolean enableSteeleaf;
    public static boolean enableKnightmetal;
    public static boolean enableFiery;
    public static boolean enableTFChestLoot;
    public static boolean enableTFZombieWeapons;
    public static float tfIronwoodZombieChance;
    public static float tfSteeleafZombieChance;
    public static boolean enableTFMinotaurBattleaxe;
    public static float tfMinotaurBattleaxeChance;
    public static boolean enableTFSnowGuardianWeapons;
    public static float tfSnowGuardianWeaponChance;

    // Aether
    public static boolean enableAether;
    public static boolean enableSkyroot;
    public static boolean enableHolystone;
    public static boolean enableZanite;
    public static boolean enableArkenium;
    public static boolean enableContinuum;
    public static boolean enableGravitite;

    // Other Mods
    public static boolean enableBOP;
    public static boolean enableAmethystAoEDamage;
    public static boolean enableErebus;
    public static boolean enableJadeArthropodDamage;
    public static boolean enableAquaculture;
    public static boolean enableNeptuniumWaterDamage;
    public static boolean enableNeptunesBountyLoot;
    public static boolean enableNetherlicious;
    public static boolean enableEnderlicious;
    public static boolean enableSugiliteTeleport;
    public static int sugiliteTeleportChance;
    public static boolean enableNetherrocks;
    public static boolean enableFyrite;
    public static boolean enableIllumenite;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfig();
        }
    }

    public static void loadConfig() {
        config.load();

        enableCustomHitEffectsFix = config.getBoolean("enableCustomHitEffectsFix", CATEGORY_GENERAL, true, "Enables Forge Event Bus listener to allow custom on-hit effects for thrown BWM projectiles.");
        enableMusketBulletEffects = config.getBoolean("enableMusketBulletEffects", CATEGORY_GENERAL, true, "Enable musket bullets inheriting material special effects from bayonets/knives.");

        enableCopper = config.getBoolean("enableCopper", CATEGORY_GENERAL, true, "Enable Copper weapons if ingotCopper is present");
        enableTin = config.getBoolean("enableTin", CATEGORY_GENERAL, true, "Enable Tin weapons if ingotTin is present");
        enableCopperChestLoot = config.getBoolean("enableCopperChestLoot", CATEGORY_GENERAL, true, "Add Copper weapons to Stronghold Corridor and Village Blacksmith chests");
        enableTinChestLoot = config.getBoolean("enableTinChestLoot", CATEGORY_GENERAL, true, "Add Tin weapons to Stronghold Corridor and Village Blacksmith chests");
        enableCopperZombieWeapons = config.getBoolean("enableCopperZombieWeapons", CATEGORY_GENERAL, true, "Allow Zombies to spawn with Copper weapons");
        enableTinZombieWeapons = config.getBoolean("enableTinZombieWeapons", CATEGORY_GENERAL, true, "Allow Zombies to spawn with Tin weapons");
        enablePigZombieGoldWeapons = config.getBoolean("enablePigZombieGoldWeapons", CATEGORY_GENERAL, true, "Allow Zombie Pigmen to spawn with Gold weapons from Balkons WeaponMod");
        pigZombieGoldWeaponChance = config.getFloat("pigZombieGoldWeaponChance", CATEGORY_GENERAL, 0.05F, 0.0F, 1.0F, "Chance (0.0 - 1.0) for a Zombie Pigman to spawn with a Gold weapon instead of a sword");
        enableWitherSkeletonStoneWeapons = config.getBoolean("enableWitherSkeletonStoneWeapons", CATEGORY_GENERAL, true, "Allow Wither Skeletons to spawn with Stone weapons from Balkons WeaponMod");
        witherSkeletonStoneWeaponChance = config.getFloat("witherSkeletonStoneWeaponChance", CATEGORY_GENERAL, 0.05F, 0.0F, 1.0F, "Chance (0.0 - 1.0) for a Wither Skeleton to spawn with Stone weapons instead of a sword");

        enableTwilightForest = config.getBoolean("enableTwilightForest", CATEGORY_TWILIGHT_FOREST, true, "Enable Twilight Forest weapons");
        enableIronwood = config.getBoolean("enableIronwood", CATEGORY_TWILIGHT_FOREST, true, "Enable Ironwood weapons");
        enableSteeleaf = config.getBoolean("enableSteeleaf", CATEGORY_TWILIGHT_FOREST, true, "Enable Steeleaf weapons");
        enableKnightmetal = config.getBoolean("enableKnightmetal", CATEGORY_TWILIGHT_FOREST, true, "Enable Knightmetal weapons");
        enableFiery = config.getBoolean("enableFiery", CATEGORY_TWILIGHT_FOREST, true, "Enable Fiery weapons");
        enableTFChestLoot = config.getBoolean("enableTFChestLoot", CATEGORY_TWILIGHT_FOREST, true, "Add Ironwood and Steeleaf weapons to Twilight Forest chests");
        enableTFZombieWeapons = config.getBoolean("enableTFZombieWeapons", CATEGORY_TWILIGHT_FOREST, true, "Allow Zombies in the Twilight Forest to spawn with Ironwood and Steeleaf weapons");
        tfIronwoodZombieChance = config.getFloat("tfIronwoodZombieChance", CATEGORY_TWILIGHT_FOREST, 0.02F, 0.0F, 1.0F, "Chance (0.0 - 1.0) for a Zombie spawning in the Twilight Forest with an Ironwood weapon");
        tfSteeleafZombieChance = config.getFloat("tfSteeleafZombieChance", CATEGORY_TWILIGHT_FOREST, 0.003F, 0.0F, 1.0F, "Chance (0.0 - 1.0) for a Zombie spawning in the Twilight Forest with a Steeleaf weapon");
        enableTFMinotaurBattleaxe = config.getBoolean("enableTFMinotaurBattleaxe", CATEGORY_TWILIGHT_FOREST, true, "Allow Twilight Forest Minotaurs to spawn with a Gold Battleaxe from Balkons WeaponMod");
        tfMinotaurBattleaxeChance = config.getFloat("tfMinotaurBattleaxeChance", CATEGORY_TWILIGHT_FOREST, 0.25F, 0.0F, 1.0F, "Chance (0.0 - 1.0) for a Minotaur to spawn with the Gold Battleaxe");
        enableTFSnowGuardianWeapons = config.getBoolean("enableTFSnowGuardianWeapons", CATEGORY_TWILIGHT_FOREST, true, "Allow Twilight Forest Snow Guardians to spawn with matching Ironwood, Steeleaf, or Knightmetal weapons");
        tfSnowGuardianWeaponChance = config.getFloat("tfSnowGuardianWeaponChance", CATEGORY_TWILIGHT_FOREST, 0.30F, 0.0F, 1.0F, "Chance (0.0 - 1.0) for a Snow Guardian to replace its sword with a matching tier weapon");

        enableAether = config.getBoolean("enableAether", CATEGORY_AETHER, true, "Enable Aether weapons");
        enableSkyroot = config.getBoolean("enableSkyroot", CATEGORY_AETHER, true, "Enable Skyroot weapons");
        enableHolystone = config.getBoolean("enableHolystone", CATEGORY_AETHER, true, "Enable Holystone weapons");
        enableZanite = config.getBoolean("enableZanite", CATEGORY_AETHER, true, "Enable Zanite weapons");
        enableArkenium = config.getBoolean("enableArkenium", CATEGORY_AETHER, true, "Enable Arkenium weapons");
        enableContinuum = config.getBoolean("enableContinuum", CATEGORY_AETHER, true, "Enable Continuum weapons");
        enableGravitite = config.getBoolean("enableGravitite", CATEGORY_AETHER, true, "Enable Gravitite weapons");

        enableBOP = config.getBoolean("enableBOP", CATEGORY_MODS, true, "Enable Biomes O' Plenty Ender Amethyst weapons");
        enableAmethystAoEDamage = config.getBoolean("enableAmethystAoEDamage", CATEGORY_MODS, true, "Enable Ender Amethyst weapons dealing AOE damage");
        enableErebus = config.getBoolean("enableErebus", CATEGORY_MODS, true, "Enable Erebus Jade weapons");
        enableJadeArthropodDamage = config.getBoolean("enableJadeArthropodDamage", CATEGORY_MODS, true, "Enable Jade weapons dealing extra damage to arthropods");
        enableAquaculture = config.getBoolean("enableAquaculture", CATEGORY_MODS, true, "Enable Aquaculture Neptunium weapons");
        enableNeptuniumWaterDamage = config.getBoolean("enableNeptuniumWaterDamage", CATEGORY_MODS, true, "Enable Neptunium weapons dealing extra damage in water/rain");
        enableNeptunesBountyLoot = config.getBoolean("enableNeptunesBountyLoot", CATEGORY_MODS, true, "Add this mod's Neptunium weapons to the Neptune's Bounty");
        enableNetherlicious = config.getBoolean("enableNetherlicious", CATEGORY_MODS, true, "Enable Netherlicious Efrine weapons");
        enableEnderlicious = config.getBoolean("enableEnderlicious", CATEGORY_MODS, true, "Enable Enderlicious Sugilite weapons");
        enableSugiliteTeleport = config.getBoolean("enableSugiliteTeleport", CATEGORY_MODS, true, "Enable Sugilite weapons teleporting targets on hit");
        sugiliteTeleportChance = config.getInt("sugiliteTeleportChance", CATEGORY_MODS, 50, 1, 100, "Percentage chance for Sugilite weapons to teleport targets on hit");

        enableNetherrocks = config.getBoolean("enableNetherrocks", CATEGORY_MODS, true, "Enable Netherrocks weapons");
        enableFyrite = config.getBoolean("enableFyrite", CATEGORY_MODS, true, "Enable Fyrite weapons");
        enableIllumenite = config.getBoolean("enableIllumenite", CATEGORY_MODS, true, "Enable Illumenite weapons");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
