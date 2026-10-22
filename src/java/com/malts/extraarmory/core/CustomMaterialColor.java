package com.malts.extraarmory.core;

import ckathode.weaponmod.entity.projectile.ICustomProjectileMaterials;
import com.malts.extraarmory.items.IWeaponMaterialCheck;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CustomMaterialColor implements ICustomProjectileMaterials {

    public final Item.ToolMaterial toolMaterial;
    public final float[] entityColorF;

    public CustomMaterialColor(Item.ToolMaterial material, int hexColorRgb) {
        this.toolMaterial = material;
        this.entityColorF = new float[3];
        this.entityColorF[0] = ((hexColorRgb >> 16) & 0xFF) / 255.0F;
        this.entityColorF[1] = ((hexColorRgb >> 8) & 0xFF) / 255.0F;
        this.entityColorF[2] = (hexColorRgb & 0xFF) / 255.0F;
    }

    @Override
    public int[] getAllMaterialIDs() {
        return new int[]{ toolMaterial.ordinal() };
    }

    @Override
    public int getMaterialID(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof IWeaponMaterialCheck) {
            return ((IWeaponMaterialCheck) stack.getItem()).getMaterialID();
        }
        return 0;
    }

    @Override
    public float[] getColorFromMaterialID(int materialId) {
        return entityColorF;
    }
}