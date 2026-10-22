package com.malts.extraarmory.items;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.WeaponModConfig;
import ckathode.weaponmod.entity.projectile.MaterialRegistry;
import ckathode.weaponmod.item.MeleeCompBattleaxe;
import ckathode.weaponmod.item.MeleeCompBoomerang;
import ckathode.weaponmod.item.MeleeCompHalberd;
import ckathode.weaponmod.item.MeleeCompKnife;
import ckathode.weaponmod.item.MeleeCompNone;
import ckathode.weaponmod.item.MeleeCompSpear;
import ckathode.weaponmod.item.MeleeCompWarhammer;
import ckathode.weaponmod.item.MeleeComponent;
import com.malts.extraarmory.ExtraArmory;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.core.CustomMaterialColor;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class WeaponFactory {

    public static WeaponCollection createWeaponSet(
            String materialName, Item.ToolMaterial material, String repairOreDict, int projectileColorHex,
            SpecialEffectType effect, Enchantment preEnchant, int preEnchantLevel,
            EnumChatFormatting nameColor
    ) {
        return createWeaponSet(materialName, material, repairOreDict, projectileColorHex, effect, preEnchant, preEnchantLevel, false, 3.0F, 4.0F, nameColor);
    }

    public static WeaponCollection createWeaponSet(
            String materialName, Item.ToolMaterial material, String repairOreDict, int projectileColorHex,
            SpecialEffectType effect, Enchantment preEnchant, int preEnchantLevel, boolean isFireproof,
            EnumChatFormatting nameColor
    ) {
        return createWeaponSet(materialName, material, repairOreDict, projectileColorHex, effect, preEnchant, preEnchantLevel, isFireproof, 3.0F, 4.0F, nameColor);
    }

    public static WeaponCollection createWeaponSet(
            String materialName, Item.ToolMaterial material, String repairOreDict, int projectileColorHex,
            SpecialEffectType effect, Enchantment preEnchant, int preEnchantLevel,
            float baseReach, float polearmReach, EnumChatFormatting nameColor
    ) {
        return createWeaponSet(materialName, material, repairOreDict, projectileColorHex, effect, preEnchant, preEnchantLevel, false, baseReach, polearmReach, nameColor);
    }

    public static WeaponCollection createWeaponSet(
            String materialName, Item.ToolMaterial material, String repairOreDict, int projectileColorHex,
            SpecialEffectType effect, Enchantment preEnchant, int preEnchantLevel,
            boolean isFireproof, float baseReach, float polearmReach, EnumChatFormatting nameColor
    ) {
        MaterialRegistry.registerCustomProjectileMaterial(new CustomMaterialColor(material, projectileColorHex));
        WeaponModConfig config = BalkonsWeaponMod.instance.modConfig;

        Item battleaxe = null;
        Item boomerang = null;
        Item flail = null;
        Item halberd = null;
        Item katana = null;
        Item knife = null;
        Item musketWithBayonet = null;
        Item spear = null;
        Item warhammer = null;

        // 1. Battleaxe
        if (config.isEnabled("battleaxe")) {
            battleaxe = new ItemCustomMelee("battleaxe." + materialName, new MeleeCompBattleaxe(material), repairOreDict, effect, preEnchant, preEnchantLevel, baseReach, isFireproof, nameColor)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/battleaxe." + materialName);
        }

        // 2. Boomerang
        if (config.isEnabled("boomerang")) {
            boomerang = new ItemCustomMelee("boomerang." + materialName, new MeleeCompBoomerang(material), repairOreDict, effect, preEnchant, preEnchantLevel, baseReach, isFireproof, nameColor)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/boomerang." + materialName);
        }

        // 3. Flail
        if (config.isEnabled("flail")) {
            flail = new ItemCustomFlail("flail." + materialName, new MeleeCompNone(material), repairOreDict, effect, preEnchant, preEnchantLevel, baseReach, isFireproof, nameColor)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/flail." + materialName);
        }

        // 4. Halberd
        if (config.isEnabled("halberd")) {
            halberd = new ItemCustomMelee("halberd." + materialName, new MeleeCompHalberd(material), repairOreDict, effect, preEnchant, preEnchantLevel, polearmReach, isFireproof, nameColor)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/halberd." + materialName);
            ExtraArmory.proxy.registerLongWeaponRenderer(halberd);
        }

        // 5. Katana
        if (config.isEnabled("katana")) {
            katana = new ItemCustomMelee("katana." + materialName, new MeleeComponent(MeleeComponent.MeleeSpecs.KATANA, material), repairOreDict, effect, preEnchant, preEnchantLevel, baseReach, isFireproof, nameColor)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/katana." + materialName);
        }

        // 6. Knife
        if (config.isEnabled("knife")) {
            knife = new ItemCustomMelee("knife." + materialName, new MeleeCompKnife(material), repairOreDict, effect, preEnchant, preEnchantLevel, baseReach, isFireproof, nameColor)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/knife." + materialName);

            // 7. Musket with Bayonet
            if (config.isEnabled("musket")) {
                musketWithBayonet = new ItemCustomMusket("musketbayonet." + materialName, new MeleeCompKnife(material), (ItemCustomMelee) knife, repairOreDict, effect, preEnchant, preEnchantLevel, baseReach, isFireproof, nameColor)
                        .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/musketbayonet." + materialName);
            }
        }

        // 8. Spear
        if (config.isEnabled("spear")) {
            spear = new ItemCustomMelee("spear." + materialName, new MeleeCompSpear(material), repairOreDict, effect, preEnchant, preEnchantLevel, polearmReach, isFireproof, nameColor)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/spear." + materialName);
            ExtraArmory.proxy.registerLongWeaponRenderer(spear);
        }

        // 9. Warhammer
        if (config.isEnabled("warhammer")) {
            warhammer = new ItemCustomMelee("warhammer." + materialName, new MeleeCompWarhammer(material), repairOreDict, effect, preEnchant, preEnchantLevel, baseReach, isFireproof, nameColor)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/warhammer." + materialName);
        }

        return new WeaponCollection(battleaxe, boomerang, flail, halberd, katana, knife, musketWithBayonet, spear, warhammer);
    }

    public static void registerStandardRecipes(WeaponCollection weapons, Object handleItem, Object headItem) {
        registerStandardRecipes(weapons, handleItem, headItem, "plankWood");
    }

    public static void registerStandardRecipes(WeaponCollection weapons, Object handleItem, Object headItem, Object boomerangPlank) {
        WeaponModConfig config = BalkonsWeaponMod.instance.modConfig;

        if (config.isEnabled("battleaxe") && weapons.battleaxe != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.battleaxe, "###", "#X#", " X ", 'X', handleItem, '#', headItem));

        if (config.isEnabled("boomerang") && weapons.boomerang != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.boomerang, "XX#", "  X", "  X", 'X', boomerangPlank, '#', headItem));

        if (config.isEnabled("flail") && weapons.flail != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.flail, "  O", " XO", "X #", 'X', handleItem, 'O', Items.string, '#', headItem));

        if (config.isEnabled("halberd") && weapons.halberd != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.halberd, " ##", " X#", "X  ", 'X', handleItem, '#', headItem));

        if (config.isEnabled("katana") && weapons.katana != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.katana, "  #", " # ", "X  ", 'X', handleItem, '#', headItem));

        if (config.isEnabled("knife") && weapons.knife != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.knife, "#X", 'X', handleItem, '#', headItem));
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.knife, "#", "X", 'X', handleItem, '#', headItem));

            if (config.isEnabled("musket") && weapons.musketWithBayonet != null)
                GameRegistry.addShapelessRecipe(new ItemStack(weapons.musketWithBayonet), weapons.knife, BalkonsWeaponMod.musket);
        }

        if (config.isEnabled("spear") && weapons.spear != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.spear, "  #", " X ", "X  ", 'X', handleItem, '#', headItem));

        if (config.isEnabled("warhammer") && weapons.warhammer != null)
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.warhammer, "#X#", "#X#", " X ", 'X', handleItem, '#', headItem));
    }
}