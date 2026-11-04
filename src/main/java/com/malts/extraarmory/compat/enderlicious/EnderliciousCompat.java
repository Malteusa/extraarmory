package com.malts.extraarmory.compat.enderlicious;

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

public class EnderliciousCompat implements ICompatModule {

    public static WeaponCollection sugiliteWeapons;

    private static Item sugiliteSword = null;
    private static boolean searchedSword = false;

    public static boolean isSugiliteSword(Item item) {
        if (item == null) return false;
        if (!searchedSword) {
            searchedSword = true;
            sugiliteSword = GameRegistry.findItem("enderlicious", "SugiliteSword");
        }
        return item == sugiliteSword;
    }

    @Override
    public String getModId() {
        return "enderlicious";
    }

    @Override
    public String getName() {
        return "Enderlicious";
    }

    @Override
    public void init() {
        if (!ExtraArmoryConfig.enableEnderlicious) return;

        Item materialItem = GameRegistry.findItem("enderlicious", "material");
        if (materialItem != null && OreDictionary.getOres("gemSugilite").isEmpty()) {
            OreDictionary.registerOre("gemSugilite", new ItemStack(materialItem, 1, 0));
        }

        Item.ToolMaterial matSugilite = getMaterial("SUGILITE", 3, 1230, 8.0F, 3.0F, 22);

        sugiliteWeapons = WeaponFactory.createWeaponSet(
                "sugilite", matSugilite, "gemSugilite", 0x7C35B4,
                SpecialEffectType.SUGILITE, null, 0, false, null
        );

        WeaponFactory.registerStandardRecipes(sugiliteWeapons, Items.iron_ingot, "gemSugilite", Items.iron_ingot);
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e) {
            return EnumHelper.addToolMaterial("ENDER_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}