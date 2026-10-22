package com.malts.extraarmory.items;

import com.malts.extraarmory.compat.SpecialEffectType;
import java.util.function.Consumer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

public class MaterialDefinition {

    public final String name;
    public final Item.ToolMaterial toolMaterial;
    public final String repairOreDict;
    public final int colorHex;
    public final SpecialEffectType effect;
    public final Enchantment preEnchant;
    public final int preEnchantLevel;
    public final boolean isFireproof;
    public final float baseReach;
    public final float polearmReach;
    public final EnumChatFormatting nameColor;
    public final Consumer<WeaponCollection> recipeRegisterer;

    public MaterialDefinition(
            String name, Item.ToolMaterial toolMaterial, String repairOreDict, int colorHex,
            SpecialEffectType effect, Enchantment preEnchant, int preEnchantLevel,
            boolean isFireproof, float baseReach, float polearmReach, EnumChatFormatting nameColor,
            Consumer<WeaponCollection> recipeRegisterer
    ) {
        this.name = name;
        this.toolMaterial = toolMaterial;
        this.repairOreDict = repairOreDict;
        this.colorHex = colorHex;
        this.effect = effect;
        this.preEnchant = preEnchant;
        this.preEnchantLevel = preEnchantLevel;
        this.isFireproof = isFireproof;
        this.baseReach = baseReach;
        this.polearmReach = polearmReach;
        this.nameColor = nameColor;
        this.recipeRegisterer = recipeRegisterer;
    }
}