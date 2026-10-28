package com.malts.extraarmory.compat.aquaculture;

import com.malts.extraarmory.ExtraArmory;
import com.malts.extraarmory.compat.ICompatModule;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.WeaponCollection;
import com.malts.extraarmory.items.WeaponFactory;
import cpw.mods.fml.common.registry.GameRegistry;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

public class AquacultureCompat implements ICompatModule {

    public static WeaponCollection neptuniumWeapons;

    private static final Set<Item> NEPTUNIUM_BASE_TOOLS = new HashSet<>();
    private static boolean searchedTools = false;

    public static boolean isNeptuniumTool(Item item) {
        if (item == null) return false;
        if (!searchedTools) {
            searchedTools = true;
            String[] toolNames = new String[]{
                    "item.NeptuniumSword", "neptunium_sword",
                    "item.NeptuniumAxe", "neptunium_axe",
                    "item.NeptuniumShovel", "neptunium_shovel",
                    "item.NeptuniumPickaxe", "neptunium_pickaxe",
                    "item.NeptuniumHoe", "neptunium_hoe"
            };
            for (String name : toolNames) {
                Item found = GameRegistry.findItem("Aquaculture", name);
                if (found != null) {
                    NEPTUNIUM_BASE_TOOLS.add(found);
                }
            }
        }
        return NEPTUNIUM_BASE_TOOLS.contains(item);
    }

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
                SpecialEffectType.NEPTUNIUM, null, 0, false, null
        );

        WeaponFactory.registerStandardRecipes(neptuniumWeapons, "stickWood", "ingotNeptunium");

        if (ExtraArmoryConfig.enableNeptunesBountyLoot) {
            addNeptuniumWeaponsToBounty(neptuniumWeapons);
        }
    }

    private static void addNeptuniumWeaponsToBounty(WeaponCollection weapons) {
        if (weapons == null) return;

        Item[] bountyItems = new Item[]{
                weapons.battleaxe,
                weapons.boomerang,
                weapons.flail,
                weapons.halberd,
                weapons.katana,
                weapons.knife,
                weapons.spear,
                weapons.warhammer
        };

        try {
            Class<?> aiClass = Class.forName("rebelkeithy.mods.aquaculture.items.AquacultureItems");
            Object bountyObj = null;

            try {
                Field f = aiClass.getField("neptunesBounty");
                bountyObj = f.get(null);
            } catch (NoSuchFieldException ignored) {
                for (Field f : aiClass.getFields()) {
                    Object val = f.get(null);
                    if (val != null && val.getClass().getName().contains("ItemNeptunesBounty")) {
                        bountyObj = val;
                        break;
                    }
                }
            }

            if (bountyObj == null) return;

            Field lootField = null;
            Class<?> currentClass = bountyObj.getClass();
            while (currentClass != null && lootField == null) {
                try {
                    lootField = currentClass.getDeclaredField("loot");
                } catch (NoSuchFieldException e) {
                    currentClass = currentClass.getSuperclass();
                }
            }

            if (lootField == null) return;

            lootField.setAccessible(true);
            Object lootSet = lootField.get(bountyObj);

            if (lootSet == null) {
                Method initLootMethod = bountyObj.getClass().getMethod("initLoot");
                initLootMethod.invoke(bountyObj);
                lootSet = lootField.get(bountyObj);
            }

            if (lootSet == null) return;

            Method addLootMethod = null;
            for (Method m : lootSet.getClass().getMethods()) {
                if (m.getName().equals("addLoot")) {
                    Class<?>[] params = m.getParameterTypes();
                    if (params.length == 4 && params[1] == int.class && params[2] == int.class && params[3] == int.class) {
                        if (params[0] == Item.class || params[0] == ItemStack.class) {
                            addLootMethod = m;
                            break;
                        }
                    }
                }
            }

            if (addLootMethod == null) return;

            boolean takesItemStack = addLootMethod.getParameterTypes()[0] == ItemStack.class;
            for (Item item : bountyItems) {
                if (item != null) {
                    if (takesItemStack) {
                        addLootMethod.invoke(lootSet, new ItemStack(item), 1, 1, 1);
                    } else {
                        addLootMethod.invoke(lootSet, item, 1, 1, 1);
                    }
                }
            }

            ExtraArmory.LOG.info("Successfully added Neptunium weapons to Neptune's Bounty.");
        } catch (Throwable t) {
            ExtraArmory.LOG.error("Failed to inject Neptunium weapons into Neptune's Bounty", t);
        }
    }

    private static Item.ToolMaterial getMaterial(String enumName, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        try {
            return Item.ToolMaterial.valueOf(enumName);
        } catch (Exception e) {
            return EnumHelper.addToolMaterial("AQUA_" + enumName, harvestLevel, maxUses, efficiency, damage, enchantability);
        }
    }
}
