package com.malts.extraarmory.compat.generic;

import ckathode.weaponmod.WMMobEquipment;
import com.malts.extraarmory.compat.ICompatModule;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.WeaponCollection;
import com.malts.extraarmory.items.WeaponFactory;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

public class GenericCompat implements ICompatModule {

    public static WeaponCollection copperWeapons;
    public static WeaponCollection tinWeapons;

    @Override
    public String getModId() {
        return "Forge";
    }

    @Override
    public String getName() {
        return "Generic Metals (Copper & Tin)";
    }

    @Override
    public void init() {
        if (ExtraArmoryConfig.enableCopper && !OreDictionary.getOres("ingotCopper").isEmpty()) {
            Item.ToolMaterial matCopper = getMaterial("COPPER", 1, 185, 4.0F, 1.0F, 8);
            copperWeapons = WeaponFactory.createWeaponSet(
                    "copper", matCopper, "ingotCopper", 0xC86432,
                    SpecialEffectType.NONE, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(copperWeapons, "stickWood", "ingotCopper");

            if (ExtraArmoryConfig.enableCopperChestLoot) {
                registerVanillaChestLoot(copperWeapons);
            }
            if (ExtraArmoryConfig.enableCopperZombieWeapons) {
                registerZombieWeapons(copperWeapons);
            }
        }

        if (ExtraArmoryConfig.enableTin && !OreDictionary.getOres("ingotTin").isEmpty()) {
            Item.ToolMaterial matTin = getMaterial("TIN", 1, 220, 3.5F, 1.0F, 8);
            tinWeapons = WeaponFactory.createWeaponSet(
                    "tin", matTin, "ingotTin", 0xC0D1D1,
                    SpecialEffectType.NONE, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(tinWeapons, "stickWood", "ingotTin");

            if (ExtraArmoryConfig.enableTinChestLoot) {
                registerVanillaChestLoot(tinWeapons);
            }
            if (ExtraArmoryConfig.enableTinZombieWeapons) {
                registerZombieWeapons(tinWeapons);
            }
        }
    }

    private static void registerVanillaChestLoot(WeaponCollection weapons) {
        if (weapons == null) return;
        Item[] items = new Item[]{
                weapons.battleaxe,
                weapons.boomerang,
                weapons.flail,
                weapons.katana,
                weapons.halberd,
                weapons.knife,
                weapons.musketWithBayonet,
                weapons.spear,
                weapons.warhammer
        };
        for (Item item : items) {
            if (item != null) {
                ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR,
                        new WeightedRandomChestContent(item, 0, 1, 1, 5));
                ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH,
                        new WeightedRandomChestContent(item, 0, 1, 1, 5));
            }
        }
    }

    private static void registerZombieWeapons(WeaponCollection weapons) {
        if (weapons == null) return;
        if (weapons.battleaxe != null) WMMobEquipment.registerZombieWeapon(weapons.battleaxe, "battleaxe");
        if (weapons.katana != null) WMMobEquipment.registerZombieWeapon(weapons.katana, "katana");
        if (weapons.halberd != null) WMMobEquipment.registerZombieWeapon(weapons.halberd, "halberd");
        if (weapons.knife != null) WMMobEquipment.registerZombieWeapon(weapons.knife, "knife");
        if (weapons.spear != null) WMMobEquipment.registerZombieWeapon(weapons.spear, "spear");
        if (weapons.warhammer != null) WMMobEquipment.registerZombieWeapon(weapons.warhammer, "warhammer");
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e) {
            return EnumHelper.addToolMaterial("EA_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}
