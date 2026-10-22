package com.malts.extraarmory.compat.bop;

import com.malts.extraarmory.compat.ICompatModule;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.WeaponCollection;
import com.malts.extraarmory.items.WeaponFactory;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

public class BOPCompat implements ICompatModule {

    public static WeaponCollection amethystWeapons;

    @Override
    public String getModId() {
        return "BiomesOPlenty";
    }

    @Override
    public String getName() {
        return "Biomes O' Plenty";
    }

    @Override
    public void init() {
        if (!ExtraArmoryConfig.enableBOP) return;

        Item bopGems = GameRegistry.findItem("BiomesOPlenty", "gems");
        if (bopGems == null) return;

        if (OreDictionary.getOres("gemEndAmethyst").isEmpty()) {
            OreDictionary.registerOre("gemEndAmethyst", new ItemStack(bopGems, 1, 0));
        }

        Item.ToolMaterial matAmethyst = getMaterial("AMETHYST", 3, 2013, 8.5F, 5.0F, 16);

        amethystWeapons = WeaponFactory.createWeaponSet(
                "end_amethyst", matAmethyst, "gemEndAmethyst", 0xDE46DD,
                SpecialEffectType.NONE, null, 0, false, null
        );

        WeaponFactory.registerStandardRecipes(amethystWeapons, Items.iron_ingot, "gemEndAmethyst", Items.iron_ingot);
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e1) {
            return EnumHelper.addToolMaterial("BOP_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}