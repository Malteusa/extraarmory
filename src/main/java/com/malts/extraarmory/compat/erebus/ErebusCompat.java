package com.malts.extraarmory.compat.erebus;

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

public class ErebusCompat implements ICompatModule {

    public static WeaponCollection jadeWeapons;

    @Override
    public String getModId() {
        return "erebus";
    }

    @Override
    public String getName() {
        return "The Erebus";
    }

    @Override
    public void init() {
        if (!ExtraArmoryConfig.enableErebus) return;

        Item erebusMaterials = GameRegistry.findItem("erebus", "materials");
        if (erebusMaterials == null) return;

        if (OreDictionary.getOres("gemJade").isEmpty()) {
            OreDictionary.registerOre("gemJade", new ItemStack(erebusMaterials, 1, 1));
        }

        Item.ToolMaterial matJade = getMaterial("JADE", 2, 863, 7.0F, 2.0F, 18);

        jadeWeapons = WeaponFactory.createWeaponSet(
                "jade", matJade, "gemJade", 0x4BA65E,
                SpecialEffectType.NONE, null, 0, false, null
        );

        WeaponFactory.registerStandardRecipes(jadeWeapons, "stickWood", "gemJade");
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e) {
            return EnumHelper.addToolMaterial("EREBUS_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}