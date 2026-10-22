package com.malts.extraarmory.items;

import ckathode.weaponmod.WeaponModAttributes;
import ckathode.weaponmod.item.IExtendedReachItem;
import ckathode.weaponmod.item.IItemWeapon;
import ckathode.weaponmod.item.MeleeComponent;
import com.google.common.collect.Multimap;
import com.malts.extraarmory.compat.SpecialEffectHelper;
import com.malts.extraarmory.compat.SpecialEffectType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemCustomFlail extends ItemExtraFlail implements IExtendedReachItem {

    private final SpecialEffectType specialEffect;
    private final Enchantment preEnchantment;
    private final int preEnchantLevel;
    private final float extendedReach;
    private final boolean isFireproof;
    private final EnumChatFormatting nameColor;

    public ItemCustomFlail(String id, MeleeComponent meleeComponent, String repairOreDict,
                           SpecialEffectType effect, Enchantment preEnchantment, int preEnchantLevel,
                           float extendedReach, boolean isFireproof, EnumChatFormatting nameColor) {
        super(id, meleeComponent, repairOreDict);
        this.specialEffect = effect;
        this.preEnchantment = preEnchantment;
        this.preEnchantLevel = preEnchantLevel;
        this.extendedReach = extendedReach;
        this.isFireproof = isFireproof;
        this.nameColor = nameColor;
    }

    public SpecialEffectType getSpecialEffect() {
        return specialEffect;
    }

    public boolean isFireproof() {
        return isFireproof;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase attacker) {
        boolean flag = super.hitEntity(stack, victim, attacker);
        SpecialEffectHelper.applySpecialEffect(stack, victim, attacker);
        return flag;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);
        applyPreEnchantment(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean flag) {
        super.onUpdate(stack, world, entity, i, flag);
        if (!world.isRemote && preEnchantment != null && (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("ench", 9))) {
            applyPreEnchantment(stack);
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (isFireproof) {
            EntityHelper.setImmuneToFire(entityItem, true);
            entityItem.extinguish();
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        ItemStack stack = new ItemStack(item);
        applyPreEnchantment(stack);
        list.add(stack);
    }

    private void applyPreEnchantment(ItemStack stack) {
        if (preEnchantment != null && !stack.isItemEnchanted()) {
            stack.addEnchantment(preEnchantment, preEnchantLevel);
        }
    }

    @Override
    public float getExtendedReach(World world, EntityLivingBase attacker, ItemStack stack) {
        return extendedReach;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(stack);
        if (extendedReach > 3.0F) {
            multimap.removeAll(WeaponModAttributes.WEAPON_REACH.getAttributeUnlocalizedName());
            multimap.put(
                    WeaponModAttributes.WEAPON_REACH.getAttributeUnlocalizedName(),
                    new AttributeModifier(IItemWeapon.REACH_MODIFIER, "Weapon reach modifier", extendedReach - 3.0F, 0)
            );
        }
        return multimap;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = super.getItemStackDisplayName(stack);
        return nameColor != null ? nameColor + name : name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        ItemCustomMelee.addSpecialTooltips(specialEffect, isFireproof, list);
    }
}