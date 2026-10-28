package com.malts.extraarmory.items;

import ckathode.weaponmod.WeaponModAttributes;
import ckathode.weaponmod.item.IExtendedReachItem;
import ckathode.weaponmod.item.IItemWeapon;
import ckathode.weaponmod.item.MeleeComponent;
import com.google.common.collect.Multimap;
import com.malts.extraarmory.compat.SpecialEffectHelper;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.compat.aether.AetherCompat;
import com.malts.extraarmory.config.ExtraArmoryConfig;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemCustomMelee extends ItemExtraMelee implements IExtendedReachItem {

    private final SpecialEffectType specialEffect;
    private final Enchantment preEnchantment;
    private final int preEnchantLevel;
    private final float extendedReach;
    private final boolean isFireproof;
    private final EnumChatFormatting nameColor;

    public ItemCustomMelee(String id, MeleeComponent meleeComponent, String repairOreDict,
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
        addSpecialTooltips(stack, specialEffect, isFireproof, list);
    }

    public static void addSpecialTooltips(ItemStack stack, SpecialEffectType effect, boolean isFireproof, List list) {
        switch (effect) {
            case KNIGHTMETAL:
                if (SpecialEffectHelper.isKnightmetalTargetsArmored(stack)) {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.knightmetal"));
                } else {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.knightmetal_unarmored"));
                }
                break;
            case JADE:
                if (ExtraArmoryConfig.enableJadeArthropodDamage) {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.jade"));
                }
                break;
            case NEPTUNIUM:
                if (ExtraArmoryConfig.enableNeptuniumWaterDamage) {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.neptunium"));
                }
                break;
            case AMETHYST:
                if (ExtraArmoryConfig.enableAmethystAoEDamage) {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.amethyst"));
                }
                break;
            case FIERY:
                list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.fiery"));
                break;
            case SKYROOT:
                if (AetherCompat.isDeparture()) {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.skyroot"));
                }
                break;
            case HOLYSTONE:
                if (AetherCompat.isDeparture()) {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.holystone"));
                }
                break;
            case ZANITE:
                if (AetherCompat.isDeparture()) {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.zanite"));
                }
                break;
            case GRAVITITE:
                if (AetherCompat.isDeparture()) {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.gravitite"));
                }
                break;
            case CONTINUUM:
                if (AetherCompat.isDeparture()) {
                    list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.continuum"));
                }
                break;
            default:
                break;
        }
        if (isFireproof) {
            list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tooltip.extraarmory.fireproof"));
        }
    }
}
