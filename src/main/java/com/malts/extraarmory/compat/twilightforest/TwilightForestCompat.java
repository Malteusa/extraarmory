package com.malts.extraarmory.compat.twilightforest;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.WeaponModConfig;
import com.malts.extraarmory.ExtraArmory;
import com.malts.extraarmory.compat.ICompatModule;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.WeaponCollection;
import com.malts.extraarmory.items.WeaponFactory;
import cpw.mods.fml.common.registry.GameRegistry;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class TwilightForestCompat implements ICompatModule {

    public static WeaponCollection ironwoodWeapons;
    public static WeaponCollection steeleafWeapons;
    public static WeaponCollection knightmetalWeapons;
    public static WeaponCollection fieryWeapons;

    private static Item fierySword = null;
    private static Item fieryPick = null;
    private static boolean searchedFieryTools = false;

    private static Integer tfDimensionId = null;
    private static boolean searchedDimensionId = false;

    private static Class<?> snowGuardianClass = null;
    private static Class<?> minotaurClass = null;
    private static boolean searchedEntityClasses = false;

    private static Item tfIronwoodSword = null;
    private static Item tfSteeleafSword = null;
    private static Item tfKnightlySword = null;
    private static boolean searchedTFSwords = false;

    public static boolean isFieryTool(Item item) {
        if (item == null) return false;
        if (!searchedFieryTools) {
            searchedFieryTools = true;
            fierySword = GameRegistry.findItem("TwilightForest", "item.fierySword");
            fieryPick = GameRegistry.findItem("TwilightForest", "item.fieryPick");
        }
        return item == fierySword || item == fieryPick;
    }

    public static boolean isTwilightForestDimension(World world) {
        if (world == null || world.provider == null) return false;
        if ("twilightforest.world.WorldProviderTwilightForest".equals(world.provider.getClass().getName())) {
            return true;
        }
        if (!searchedDimensionId) {
            searchedDimensionId = true;
            try {
                Class<?> tfModClass = Class.forName("twilightforest.TwilightForestMod");
                Field dimField = tfModClass.getField("dimensionID");
                tfDimensionId = dimField.getInt(null);
            } catch (Throwable ignored) {
            }
        }
        if (tfDimensionId != null && world.provider.dimensionId == tfDimensionId) {
            return true;
        }
        return "Twilight Forest".equals(world.provider.getDimensionName());
    }

    private static void initEntityClasses() {
        if (searchedEntityClasses) return;
        searchedEntityClasses = true;
        try {
            snowGuardianClass = Class.forName("twilightforest.entity.EntityTFSnowGuardian");
        } catch (Throwable ignored) {
        }
        try {
            minotaurClass = Class.forName("twilightforest.entity.EntityTFMinotaur");
        } catch (Throwable ignored) {
        }
    }

    public static boolean isSnowGuardian(Entity entity) {
        if (entity == null) return false;
        initEntityClasses();
        return snowGuardianClass != null && snowGuardianClass.isInstance(entity);
    }

    public static boolean isMinotaur(Entity entity) {
        if (entity == null) return false;
        initEntityClasses();
        return minotaurClass != null && minotaurClass.isInstance(entity);
    }

    public static int getSnowGuardianSwordTier(Item item) {
        if (item == null) return -1;
        if (!searchedTFSwords) {
            searchedTFSwords = true;
            try {
                Class<?> tfItemsClass = Class.forName("twilightforest.item.TFItems");
                tfIronwoodSword = (Item) tfItemsClass.getField("ironwoodSword").get(null);
                tfSteeleafSword = (Item) tfItemsClass.getField("steeleafSword").get(null);
                tfKnightlySword = (Item) tfItemsClass.getField("knightlySword").get(null);
            } catch (Throwable ignored) {
            }
        }
        if (item == tfIronwoodSword) return 0;
        if (item == tfSteeleafSword) return 1;
        if (item == tfKnightlySword) return 2;
        return -1;
    }

    @Override
    public String getModId() {
        return "TwilightForest";
    }

    @Override
    public String getName() {
        return "The Twilight Forest";
    }

    @Override
    public void init() {
        if (!ExtraArmoryConfig.enableTwilightForest) return;
        registerOreDictFallbacks();

        if (ExtraArmoryConfig.enableIronwood) {
            Item.ToolMaterial matIronwood = getMaterial("IRONWOOD", 2, 512, 6.5F, 2.0F, 25);
            ironwoodWeapons = WeaponFactory.createWeaponSet(
                    "ironwood", matIronwood, "ingotIronwood", 0x81776C,
                    SpecialEffectType.NONE, Enchantment.knockback, 1, false, null
            );
            WeaponFactory.registerStandardRecipes(ironwoodWeapons, "stickWood", "ingotIronwood");
        }

        if (ExtraArmoryConfig.enableSteeleaf) {
            Item.ToolMaterial matSteeleaf = getMaterial("STEELEAF", 3, 131, 8.0F, 3.0F, 9);
            steeleafWeapons = WeaponFactory.createWeaponSet(
                    "steeleaf", matSteeleaf, "ingotSteeleaf", 0x6DA25E,
                    SpecialEffectType.NONE, Enchantment.looting, 2, false, null
            );
            WeaponFactory.registerStandardRecipes(steeleafWeapons, "stickWood", "ingotSteeleaf");
        }

        if (ExtraArmoryConfig.enableKnightmetal) {
            Item.ToolMaterial matKnightmetal = getMaterial("KNIGHTMETAL", 3, 512, 8.0F, 3.0F, 8);
            knightmetalWeapons = WeaponFactory.createWeaponSet(
                    "knightmetal", matKnightmetal, "ingotKnightmetal", 0xC4D6AE,
                    SpecialEffectType.KNIGHTMETAL, null, 0, false, EnumChatFormatting.AQUA
            );
            WeaponFactory.registerStandardRecipes(knightmetalWeapons, "stickWood", "ingotKnightmetal");
        }

        if (ExtraArmoryConfig.enableFiery) {
            Item.ToolMaterial matFiery = getMaterial("FIERY", 4, 1024, 9.0F, 4.0F, 10);
            fieryWeapons = WeaponFactory.createWeaponSet(
                    "fiery", matFiery, "ingotFiery", 0x3C2323,
                    SpecialEffectType.FIERY, Enchantment.fireAspect, 2, false, null
            );
            registerFieryRecipes(fieryWeapons);
        }
    }

    @Override
    public void postInit() {
        if (!ExtraArmoryConfig.enableTwilightForest || !ExtraArmoryConfig.enableTFChestLoot) return;
        if (ironwoodWeapons == null && steeleafWeapons == null) return;

        addWeaponsToTFTreasure();
    }

    private static void addWeaponsToTFTreasure() {
        try {
            Class<?> tfTreasureClass = Class.forName("twilightforest.TFTreasure");
            Field uncommonField = tfTreasureClass.getField("uncommon");
            Field rareField = tfTreasureClass.getField("rare");

            Class<?> tableClass = uncommonField.getType();
            Method addStackMethod = tableClass.getMethod("add", ItemStack.class);
            Method addRandomEnchantedMethod = tableClass.getMethod("addRandomEnchanted", Item.class, int.class);

            Object labyrinthRoom = tfTreasureClass.getField("labyrinth_room").get(null);
            Object darktowerKey = tfTreasureClass.getField("darktower_key").get(null);
            Object strongholdCache = tfTreasureClass.getField("stronghold_cache").get(null);
            Object strongholdRoom = tfTreasureClass.getField("stronghold_room").get(null);

            if (ironwoodWeapons != null) {
                addPreEnchantedIronwood(uncommonField.get(labyrinthRoom), addStackMethod,
                        ironwoodWeapons.battleaxe, ironwoodWeapons.halberd, ironwoodWeapons.spear, ironwoodWeapons.warhammer);

                addPreEnchantedIronwood(uncommonField.get(darktowerKey), addStackMethod,
                        ironwoodWeapons.boomerang, ironwoodWeapons.flail, ironwoodWeapons.katana, ironwoodWeapons.knife);

                addPreEnchantedIronwood(uncommonField.get(strongholdRoom), addStackMethod,
                        ironwoodWeapons.battleaxe, ironwoodWeapons.katana, ironwoodWeapons.knife, ironwoodWeapons.spear);

                addRandomEnchantedItems(rareField.get(strongholdCache), addRandomEnchantedMethod, 15,
                        ironwoodWeapons.boomerang, ironwoodWeapons.flail, ironwoodWeapons.halberd);

                addRandomEnchantedItems(rareField.get(strongholdRoom), addRandomEnchantedMethod, 25,
                        ironwoodWeapons.battleaxe, ironwoodWeapons.katana, ironwoodWeapons.warhammer);
            }

            if (steeleafWeapons != null) {
                addRandomEnchantedItems(rareField.get(strongholdCache), addRandomEnchantedMethod, 10,
                        steeleafWeapons.battleaxe, steeleafWeapons.katana, steeleafWeapons.knife, steeleafWeapons.warhammer);

                addRandomEnchantedItems(rareField.get(strongholdRoom), addRandomEnchantedMethod, 20,
                        steeleafWeapons.boomerang, steeleafWeapons.flail, steeleafWeapons.halberd, steeleafWeapons.spear);
            }

            ExtraArmory.LOG.info("Successfully added Twilight Forest weapons to TFTreasure chest pools.");
        } catch (Throwable t) {
            ExtraArmory.LOG.error("Failed to inject Twilight Forest weapons into TFTreasure chests", t);
        }
    }

    private static void addPreEnchantedIronwood(Object table, Method addStackMethod, Item... items) throws Exception {
        if (table == null) return;
        for (Item item : items) {
            if (item != null) {
                ItemStack stack = new ItemStack(item);
                stack.addEnchantment(Enchantment.knockback, 1);
                addStackMethod.invoke(table, stack);
            }
        }
    }

    private static void addRandomEnchantedItems(Object table, Method addRandomEnchantedMethod, int level, Item... items) throws Exception {
        if (table == null) return;
        for (Item item : items) {
            if (item != null) {
                addRandomEnchantedMethod.invoke(table, item, level);
            }
        }
    }

    private static void registerFieryRecipes(WeaponCollection weapons) {
        WeaponModConfig config = BalkonsWeaponMod.instance.modConfig;
        Object rod = Items.blaze_rod;
        Object powder = Items.blaze_powder;
        Object ingot = "ingotFiery";

        if (config.isEnabled("battleaxe") && weapons.battleaxe != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.battleaxe, "###", "#X#", " X ", 'X', rod, '#', ingot));

        if (config.isEnabled("boomerang") && weapons.boomerang != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.boomerang, "RP#", "  P", "  R", 'R', rod, 'P', powder, '#', ingot));
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.boomerang, "PR#", "  R", "  P", 'R', rod, 'P', powder, '#', ingot));
        }

        if (config.isEnabled("flail") && weapons.flail != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.flail, "  O", " XO", "X #", 'X', rod, 'O', powder, '#', ingot));

        if (config.isEnabled("halberd") && weapons.halberd != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.halberd, " ##", " X#", "X  ", 'X', rod, '#', ingot));

        if (config.isEnabled("katana") && weapons.katana != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.katana, "  #", " # ", "X  ", 'X', rod, '#', ingot));

        if (config.isEnabled("knife") && weapons.knife != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.knife, "#X", 'X', rod, '#', ingot));
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.knife, "#", "X", 'X', rod, '#', ingot));

            if (config.isEnabled("musket") && weapons.musketWithBayonet != null)
                GameRegistry.addShapelessRecipe(new ItemStack(weapons.musketWithBayonet), weapons.knife, BalkonsWeaponMod.musket);
        }

        if (config.isEnabled("spear") && weapons.spear != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.spear, "  #", " X ", "X  ", 'X', rod, '#', ingot));

        if (config.isEnabled("warhammer") && weapons.warhammer != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.warhammer, "#X#", "#X#", " X ", 'X', rod, '#', ingot));
    }

    private static void registerOreDictFallbacks() {
        registerIfFound("TwilightForest", "item.steeleafIngot", "ingotSteeleaf");
        registerIfFound("TwilightForest", "item.ironwoodIngot", "ingotIronwood");
        registerIfFound("TwilightForest", "item.knightmetalIngot", "ingotKnightmetal");
        registerIfFound("TwilightForest", "item.fieryIngot", "ingotFiery");
    }

    private static void registerIfFound(String modId, String itemName, String oreDict) {
        if (OreDictionary.getOres(oreDict).isEmpty()) {
            Item item = GameRegistry.findItem(modId, itemName);
            if (item != null) {
                OreDictionary.registerOre(oreDict, item);
            }
        }
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e) {
            return EnumHelper.addToolMaterial("TF_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}
