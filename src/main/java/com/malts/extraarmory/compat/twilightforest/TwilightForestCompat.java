package com.malts.extraarmory.compat.twilightforest;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.WeaponModConfig;
import com.malts.extraarmory.compat.ICompatModule;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.WeaponCollection;
import com.malts.extraarmory.items.WeaponFactory;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class TwilightForestCompat implements ICompatModule {

    public static WeaponCollection ironwoodWeapons;
    public static WeaponCollection steeleafWeapons;
    public static WeaponCollection knightmetalWeapons;
    public static WeaponCollection fieryWeapons;

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