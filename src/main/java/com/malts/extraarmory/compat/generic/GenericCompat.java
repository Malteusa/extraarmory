package com.malts.extraarmory.compat.generic;

import com.malts.extraarmory.compat.ICompatModule;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.WeaponCollection;
import com.malts.extraarmory.items.WeaponFactory;
import net.minecraft.item.Item;
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
        }

        if (ExtraArmoryConfig.enableTin && !OreDictionary.getOres("ingotTin").isEmpty()) {
            Item.ToolMaterial matTin = getMaterial("TIN", 1, 220, 3.5F, 1.0F, 8);
            tinWeapons = WeaponFactory.createWeaponSet(
                    "tin", matTin, "ingotTin", 0xC0D1D1,
                    SpecialEffectType.NONE, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(tinWeapons, "stickWood", "ingotTin");
        }
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e) {
            return EnumHelper.addToolMaterial("EA_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}