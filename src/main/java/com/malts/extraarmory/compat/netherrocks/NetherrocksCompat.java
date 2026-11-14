package com.malts.extraarmory.compat.netherrocks;

import com.malts.extraarmory.compat.ICompatModule;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.WeaponCollection;
import com.malts.extraarmory.items.WeaponFactory;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

public class NetherrocksCompat implements ICompatModule {

    public static WeaponCollection fyriteWeapons;
    public static WeaponCollection illumeniteWeapons;

    @Override
    public String getModId() {
        return "netherrocks";
    }

    @Override
    public String getName() {
        return "Netherrocks";
    }

    @Override
    public void init() {
        if (!ExtraArmoryConfig.enableNetherrocks) return;

        registerOreDictFallbacks();

        if (ExtraArmoryConfig.enableFyrite) {
            Item.ToolMaterial matFyrite = getMaterial("FYRITE", 3, 150, 8.0F, 4.0F, 7);
            fyriteWeapons = WeaponFactory.createWeaponSet(
                    "fyrite", matFyrite, "ingotFyrite", 0xFF880E,
                    SpecialEffectType.FYRITE, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(fyriteWeapons, "stickWood", "ingotFyrite");
        }

        if (ExtraArmoryConfig.enableIllumenite) {
            Item.ToolMaterial matIllumenite = getMaterial("ILLUMENITE", 3, 700, 7.0F, 4.0F, 7);
            illumeniteWeapons = WeaponFactory.createWeaponSet(
                    "illumenite", matIllumenite, "ingotIllumenite", 0xF5FA9F,
                    SpecialEffectType.ILLUMENITE, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(illumeniteWeapons, "stickWood", "ingotIllumenite");
        }
    }

    private static void registerOreDictFallbacks() {
        Item ingotFyrite = GameRegistry.findItem("netherrocks", "fyrite_ingot");
        if (ingotFyrite != null && OreDictionary.getOres("ingotFyrite").isEmpty()) {
            OreDictionary.registerOre("ingotFyrite", new ItemStack(ingotFyrite, 1, 0));
        }

        Item ingotIllumenite = GameRegistry.findItem("netherrocks", "illumenite_ingot");
        if (ingotIllumenite != null && OreDictionary.getOres("ingotIllumenite").isEmpty()) {
            OreDictionary.registerOre("ingotIllumenite", new ItemStack(ingotIllumenite, 1, 0));
        }
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e) {
            return EnumHelper.addToolMaterial("NR_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}