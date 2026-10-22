package com.malts.extraarmory.compat.aquaculture;

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

public class AquacultureCompat implements ICompatModule {

    public static WeaponCollection neptuniumWeapons;

    @Override
    public String getModId() {
        return "Aquaculture";
    }

    @Override
    public String getName() {
        return "Aquaculture";
    }

    @Override
    public void init() {
        if (!ExtraArmoryConfig.enableAquaculture) return;

        Item lootItem = GameRegistry.findItem("Aquaculture", "item.loot");
        if (lootItem == null) return;

        if (OreDictionary.getOres("ingotNeptunium").isEmpty()) {
            OreDictionary.registerOre("ingotNeptunium", new ItemStack(lootItem, 1, 12));
        }

        Item.ToolMaterial matNeptunium = getMaterial("Neptunium", 3, 2500, 9.0F, 6.0F, 15);

        neptuniumWeapons = WeaponFactory.createWeaponSet(
                "neptunium", matNeptunium, "ingotNeptunium", 0x3EF7C4,
                SpecialEffectType.NONE, null, 0, false, null
        );

        WeaponFactory.registerStandardRecipes(neptuniumWeapons, "stickWood", "ingotNeptunium");
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e) {
            return EnumHelper.addToolMaterial("AQUA_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}