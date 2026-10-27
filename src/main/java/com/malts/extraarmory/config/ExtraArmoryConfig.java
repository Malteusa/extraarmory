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

    // Generic
    public static boolean enableCopper;
    public static boolean enableTin;

    // Twilight Forest
    public static boolean enableTwilightForest;
    public static boolean enableIronwood;
    public static boolean enableSteeleaf;
    public static boolean enableKnightmetal;
    public static boolean enableFiery;

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
    public static boolean enableErebus;
    public static boolean enableJadeArthropodDamage;
    public static boolean enableAquaculture;
    public static boolean enableNetherlicious;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfig();
        }
    }

    public static void loadConfig() {
        config.load();

        enableCustomHitEffectsFix = config.getBoolean("enableCustomHitEffectsFix", CATEGORY_GENERAL, true, "Enables Forge Event Bus listener to allow custom on-hit effects for thrown BWM projectiles.");

        enableCopper = config.getBoolean("enableCopper", CATEGORY_GENERAL, true, "Enable Copper weapons if ingotCopper is present");
        enableTin = config.getBoolean("enableTin", CATEGORY_GENERAL, true, "Enable Tin weapons if ingotTin is present");

        enableTwilightForest = config.getBoolean("enableTwilightForest", CATEGORY_TWILIGHT_FOREST, true, "Enable Twilight Forest weapons");
        enableIronwood = config.getBoolean("enableIronwood", CATEGORY_TWILIGHT_FOREST, true, "Enable Ironwood weapons");
        enableSteeleaf = config.getBoolean("enableSteeleaf", CATEGORY_TWILIGHT_FOREST, true, "Enable Steeleaf weapons");
        enableKnightmetal = config.getBoolean("enableKnightmetal", CATEGORY_TWILIGHT_FOREST, true, "Enable Knightmetal weapons");
        enableFiery = config.getBoolean("enableFiery", CATEGORY_TWILIGHT_FOREST, true, "Enable Fiery weapons");

        enableAether = config.getBoolean("enableAether", CATEGORY_AETHER, true, "Enable Aether Departure weapons");
        enableSkyroot = config.getBoolean("enableSkyroot", CATEGORY_AETHER, true, "Enable Skyroot weapons");
        enableHolystone = config.getBoolean("enableHolystone", CATEGORY_AETHER, true, "Enable Holystone weapons");
        enableZanite = config.getBoolean("enableZanite", CATEGORY_AETHER, true, "Enable Zanite weapons");
        enableArkenium = config.getBoolean("enableArkenium", CATEGORY_AETHER, true, "Enable Arkenium weapons");
        enableContinuum = config.getBoolean("enableContinuum", CATEGORY_AETHER, true, "Enable Continuum weapons");
        enableGravitite = config.getBoolean("enableGravitite", CATEGORY_AETHER, true, "Enable Gravitite weapons");

        enableBOP = config.getBoolean("enableBOP", CATEGORY_MODS, true, "Enable Biomes O' Plenty Ender Amethyst weapons");
        enableErebus = config.getBoolean("enableErebus", CATEGORY_MODS, true, "Enable Erebus Jade weapons");
        enableJadeArthropodDamage = config.getBoolean("enableJadeArthropodDamage", CATEGORY_MODS, true, "Enable Jade weapons dealing extra damage to arthropods");
        enableAquaculture = config.getBoolean("enableAquaculture", CATEGORY_MODS, true, "Enable Aquaculture Neptunium weapons");
        enableNetherlicious = config.getBoolean("enableNetherlicious", CATEGORY_MODS, true, "Enable Netherlicious Efrine weapons");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
