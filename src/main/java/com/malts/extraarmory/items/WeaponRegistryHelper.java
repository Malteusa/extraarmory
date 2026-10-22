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
import com.malts.extraarmory.core.CustomMaterialColor;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class WeaponRegistryHelper {

    public static WeaponCollection registerWeaponSet(String materialName, Item.ToolMaterial material, String repairOreDict, int projectileColorHex) {
        MaterialRegistry.registerCustomProjectileMaterial(new CustomMaterialColor(material, projectileColorHex));

        Item battleaxe = null;
        Item boomerang = null;
        Item flail = null;
        Item halberd = null;
        Item katana = null;
        Item knife = null;
        Item musketWithBayonet = null;
        Item spear = null;
        Item warhammer = null;

        WeaponModConfig config = BalkonsWeaponMod.instance.modConfig;

        if (config.isEnabled("battleaxe")) {
            battleaxe = new ItemExtraMelee("battleaxe." + materialName, new MeleeCompBattleaxe(material), repairOreDict)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/battleaxe." + materialName);
        }
        if (config.isEnabled("boomerang")) {
            boomerang = new ItemExtraMelee("boomerang." + materialName, new MeleeCompBoomerang(material), repairOreDict)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/boomerang." + materialName);
        }
        if (config.isEnabled("flail")) {
            flail = new ItemExtraFlail("flail." + materialName, new MeleeCompNone(material), repairOreDict)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/flail." + materialName);
        }
        if (config.isEnabled("halberd")) {
            halberd = new ItemExtraMelee("halberd." + materialName, new MeleeCompHalberd(material), repairOreDict)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/halberd." + materialName);
            ExtraArmory.proxy.registerLongWeaponRenderer(halberd);
        }
        if (config.isEnabled("katana")) {
            katana = new ItemExtraMelee("katana." + materialName, new MeleeComponent(MeleeComponent.MeleeSpecs.KATANA, material), repairOreDict)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/katana." + materialName);
        }
        if (config.isEnabled("knife")) {
            knife = new ItemExtraMelee("knife." + materialName, new MeleeCompKnife(material), repairOreDict)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/knife." + materialName);

            if (config.isEnabled("musket")) {
                musketWithBayonet = new ItemExtraMusket("musketbayonet." + materialName, new MeleeCompKnife(material), (ItemExtraMelee) knife, repairOreDict)
                        .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/musketbayonet." + materialName);
            }
        }
        if (config.isEnabled("spear")) {
            spear = new ItemExtraMelee("spear." + materialName, new MeleeCompSpear(material), repairOreDict)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/spear." + materialName);
            ExtraArmory.proxy.registerLongWeaponRenderer(spear);
        }
        if (config.isEnabled("warhammer")) {
            warhammer = new ItemExtraMelee("warhammer." + materialName, new MeleeCompWarhammer(material), repairOreDict)
                    .setTextureName(ExtraArmory.MOD_ID + ":" + materialName + "/warhammer." + materialName);
        }

        return new WeaponCollection(battleaxe, boomerang, flail, halberd, katana, knife, musketWithBayonet, spear, warhammer);
    }

    public static void createStandardRecipes(WeaponCollection weapons, Object handleItem, Object headOreDict) {
        WeaponModConfig config = BalkonsWeaponMod.instance.modConfig;

        if (config.isEnabled("battleaxe") && weapons.battleaxe != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.battleaxe, "###", "#X#", " X ", 'X', handleItem, '#', headOreDict));
        }
        if (config.isEnabled("boomerang") && weapons.boomerang != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.boomerang, "XX#", "  X", "  X", 'X', "plankWood", '#', headOreDict));
        }
        if (config.isEnabled("flail") && weapons.flail != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.flail, "  O", " XO", "X #", 'X', handleItem, 'O', Items.string, '#', headOreDict));
        }
        if (config.isEnabled("halberd") && weapons.halberd != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.halberd, " ##", " X#", "X  ", 'X', handleItem, '#', headOreDict));
        }
        if (config.isEnabled("katana") && weapons.katana != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.katana, "  #", " # ", "X  ", 'X', handleItem, '#', headOreDict));
        }
        if (config.isEnabled("knife") && weapons.knife != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.knife, "#X", 'X', handleItem, '#', headOreDict));
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.knife, "#", "X", 'X', handleItem, '#', headOreDict));

            if (config.isEnabled("musket") && weapons.musketWithBayonet != null) {
                GameRegistry.addShapelessRecipe(new ItemStack(weapons.musketWithBayonet), weapons.knife, BalkonsWeaponMod.musket);
            }
        }
        if (config.isEnabled("spear") && weapons.spear != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.spear, "  #", " X ", "X  ", 'X', handleItem, '#', headOreDict));
        }
        if (config.isEnabled("warhammer") && weapons.warhammer != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(weapons.warhammer, "#X#", "#X#", " X ", 'X', handleItem, '#', headOreDict));
        }
    }
}