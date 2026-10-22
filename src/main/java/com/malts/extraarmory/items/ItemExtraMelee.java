package com.malts.extraarmory.items;

import ckathode.weaponmod.item.ItemMelee;
import ckathode.weaponmod.item.MeleeComponent;
import com.malts.extraarmory.ExtraArmory;
import java.util.Objects;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemExtraMelee extends ItemMelee implements IWeaponMaterialCheck {

    private final int materialId;
    private final String repairOreDict;

    public ItemExtraMelee(String id, MeleeComponent meleeComponent, String repairOreDict) {
        super(ExtraArmory.MOD_ID, id, meleeComponent);
        this.materialId = (meleeComponent.weaponMaterial != null) ? meleeComponent.weaponMaterial.ordinal() : 0;
        this.repairOreDict = repairOreDict;
        setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public int getMaterialID() {
        return materialId;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToBeRepaired, ItemStack repairMaterial) {
        if (repairOreDict != null && repairMaterial != null) {
            for (int oreId : OreDictionary.getOreIDs(repairMaterial)) {
                if (Objects.equals(OreDictionary.getOreName(oreId), repairOreDict)) {
                    return true;
                }
            }
        }
        return super.getIsRepairable(itemToBeRepaired, repairMaterial);
    }
}