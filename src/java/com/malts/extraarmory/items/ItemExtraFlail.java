package com.malts.extraarmory.items;

import ckathode.weaponmod.item.ItemFlail;
import ckathode.weaponmod.item.MeleeComponent;
import com.malts.extraarmory.ExtraArmory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Objects;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

public class ItemExtraFlail extends ItemFlail implements IWeaponMaterialCheck {

    private final int materialId;
    private final String repairOreDict;

    public ItemExtraFlail(String id, MeleeComponent meleeComponent, String repairOreDict) {
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

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        return this.itemIcon;
    }
}