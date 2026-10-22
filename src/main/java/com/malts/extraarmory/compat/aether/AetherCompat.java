package com.malts.extraarmory.compat.aether;

import com.malts.extraarmory.compat.ICompatModule;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.WeaponCollection;
import com.malts.extraarmory.items.WeaponFactory;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

public class AetherCompat implements ICompatModule {

    public static WeaponCollection skyrootWeapons;
    public static WeaponCollection holystoneWeapons;
    public static WeaponCollection zaniteWeapons;
    public static WeaponCollection arkeniumWeapons;
    public static WeaponCollection continuumWeapons;
    public static WeaponCollection gravititeWeapons;

    private static boolean isDeparture = false;

    public static boolean isDeparture() {
        return isDeparture;
    }

    @Override
    public String getModId() {
        return Loader.isModLoaded("aether_legacy") ? "aether_legacy" : "aether";
    }

    @Override
    public String getName() {
        return "Aether Compatibility";
    }

    @Override
    public void init() {
        if (!ExtraArmoryConfig.enableAether) return;

        registerOreDictFallbacks();

        boolean hasArkenium = !OreDictionary.getOres("ingotArkenium").isEmpty();
        boolean hasContinuum = !OreDictionary.getOres("gemContinuum").isEmpty();

        isDeparture = hasArkenium || hasContinuum;

        Object handle = OreDictionary.getOres("stickSkyroot").isEmpty() ? "stickWood" : "stickSkyroot";
        Object plank = OreDictionary.getOres("plankSkyroot").isEmpty() ? "plankWood" : "plankSkyroot";

        if (ExtraArmoryConfig.enableSkyroot) {
            Item.ToolMaterial matSkyroot = getMaterial("SKYROOT", 0, 59, 2.0F, 0.0F, 15);
            skyrootWeapons = WeaponFactory.createWeaponSet(
                    "skyroot", matSkyroot, "plankSkyroot", 0x806F4D,
                    SpecialEffectType.SKYROOT, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(skyrootWeapons, handle, "plankSkyroot", plank);
        }

        if (ExtraArmoryConfig.enableHolystone) {
            Item.ToolMaterial matHolystone = getMaterial("HOLYSTONE", 1, 131, 4.0F, 1.0F, 5);
            holystoneWeapons = WeaponFactory.createWeaponSet(
                    "holystone", matHolystone, "cobblestoneHolystone", 0x757575,
                    SpecialEffectType.HOLYSTONE, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(holystoneWeapons, handle, "cobblestoneHolystone", plank);
        }

        if (ExtraArmoryConfig.enableZanite) {
            Item.ToolMaterial matZanite = getMaterial("ZANITE", 2, 250, 6.0F, 2.0F, 14);
            zaniteWeapons = WeaponFactory.createWeaponSet(
                    "zanite", matZanite, "gemZanite", 0x711AE8,
                    SpecialEffectType.ZANITE, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(zaniteWeapons, handle, "gemZanite", plank);
        }

        if (isDeparture && ExtraArmoryConfig.enableArkenium && hasArkenium) {
            Item.ToolMaterial matArkenium = getMaterial("ARKENIUM", 2, 500, 6.5F, 2.0F, 14);
            arkeniumWeapons = WeaponFactory.createWeaponSet(
                    "arkenium", matArkenium, "ingotArkenium", 0x464646,
                    SpecialEffectType.NONE, null, 0, false, 4.0F, 5.0F, null
            );
            WeaponFactory.registerStandardRecipes(arkeniumWeapons, handle, "ingotArkenium", plank);
        }

        if (isDeparture && ExtraArmoryConfig.enableContinuum && hasContinuum) {
            Item.ToolMaterial matContinuum = getMaterial("CONTINUUM", 3, 900, 7.5F, 2.5F, 12);
            continuumWeapons = WeaponFactory.createWeaponSet(
                    "continuum", matContinuum, "gemContinuum", 0xEB5620,
                    SpecialEffectType.CONTINUUM, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(continuumWeapons, handle, "gemContinuum", plank);
        }

        if (ExtraArmoryConfig.enableGravitite) {
            Item.ToolMaterial matGravitite = getMaterial("GRAVITITE", 3, 1561, 8.0F, 3.0F, 10);
            gravititeWeapons = WeaponFactory.createWeaponSet(
                    "gravitite", matGravitite, "ingotGravitite", 0xA03097,
                    SpecialEffectType.GRAVITITE, null, 0, false, null
            );
            WeaponFactory.registerStandardRecipes(gravititeWeapons, handle, "ingotGravitite", plank);
        }
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e1) {
            try {
                return Item.ToolMaterial.valueOf("AETHER_" + enumName);
            } catch (Exception e2) {
                return EnumHelper.addToolMaterial("EA_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
            }
        }
    }

    private static void registerOreDictFallbacks() {
        String modId = Loader.isModLoaded("aether_legacy") ? "aether_legacy" : "aether";

        registerIfFound(modId, "skyroot_stick", "stickSkyroot");
        registerIfFound(modId, "skyroot_planks", "plankSkyroot");
        registerIfFound(modId, "skyroot_plank", "plankSkyroot");
        registerIfFound(modId, "holystone", "cobblestoneHolystone");
        registerIfFound(modId, "zanite_gemstone", "gemZanite");
        registerIfFound(modId, "gravitite_ingot", "ingotGravitite");
        registerIfFound(modId, "enchanted_gravitite", "ingotGravitite");
        registerIfFound(modId, "arkenium_ingot", "ingotArkenium");
        registerIfFound(modId, "continuum_gem", "gemContinuum");
        registerIfFound(modId, "ambrosium_shard", "shardAmbrosium");
    }

    private static void registerIfFound(String modId, String name, String oreDict) {
        if (OreDictionary.getOres(oreDict).isEmpty()) {
            Item item = GameRegistry.findItem(modId, name);
            if (item == null) {
                Block block = GameRegistry.findBlock(modId, name);
                if (block != null) {
                    item = Item.getItemFromBlock(block);
                }
            }
            if (item != null) {
                OreDictionary.registerOre(oreDict, item);
            }
        }
    }
}