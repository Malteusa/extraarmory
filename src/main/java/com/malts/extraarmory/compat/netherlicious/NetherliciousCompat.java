package com.malts.extraarmory.compat.netherlicious;

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

public class NetherliciousCompat implements ICompatModule {

    public static WeaponCollection efrineWeapons;

    @Override
    public String getModId() {
        return "netherlicious";
    }

    @Override
    public String getName() {
        return "Netherlicious";
    }

    @Override
    public void init() {
        if (!ExtraArmoryConfig.enableNetherlicious) return;

        Item efrineIngot = GameRegistry.findItem("netherlicious", "Ingot");
        if (efrineIngot != null && OreDictionary.getOres("ingotEfrine").isEmpty()) {
            OreDictionary.registerOre("ingotEfrine", new ItemStack(efrineIngot, 1, 0));
        }

        Item.ToolMaterial matEfrine = getMaterial("EFRINE", 2, 250, 12.0F, 2.0F, 22);

        efrineWeapons = WeaponFactory.createWeaponSet(
                "efrine", matEfrine, "ingotEfrine", 0x506B48,
                SpecialEffectType.NONE, null, 0, true, null
        );

        WeaponFactory.registerStandardRecipes(efrineWeapons, "stickWood", "ingotEfrine");
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e) {
            return EnumHelper.addToolMaterial("EA_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}