package com.malts.extraarmory.compat;

import com.malts.extraarmory.ExtraArmory;
import com.malts.extraarmory.compat.aether.AetherCompat;
import com.malts.extraarmory.compat.aquaculture.AquacultureCompat;
import com.malts.extraarmory.compat.bop.BOPCompat;
import com.malts.extraarmory.compat.erebus.ErebusCompat;
import com.malts.extraarmory.compat.generic.GenericCompat;
import com.malts.extraarmory.compat.netherlicious.NetherliciousCompat;
import com.malts.extraarmory.compat.twilightforest.TwilightForestCompat;
import cpw.mods.fml.common.Loader;
import java.util.ArrayList;
import java.util.List;

public class CompatManager {

    private static final List<ICompatModule> MODULES = new ArrayList<>();

    public static void registerModule(ICompatModule module) {
        MODULES.add(module);
    }

    static {
        // Registration Order
        registerModule(new GenericCompat());
        registerModule(new TwilightForestCompat());
        registerModule(new AetherCompat());
        registerModule(new BOPCompat());
        registerModule(new ErebusCompat());
        registerModule(new AquacultureCompat());
        registerModule(new NetherliciousCompat());
    }

    public static void preInit() {
        for (ICompatModule module : MODULES) {
            if (shouldLoad(module)) {
                ExtraArmory.LOG.info("Pre-Initializing compatibility module for: {}", module.getName());
                try {
                    module.preInit();
                } catch (Throwable t) {
                    ExtraArmory.LOG.error("Failed to pre-init compatibility module: " + module.getName(), t);
                }
            }
        }
    }

    public static void init() {
        for (ICompatModule module : MODULES) {
            if (shouldLoad(module)) {
                ExtraArmory.LOG.info("Initializing compatibility module for: {}", module.getName());
                try {
                    module.init();
                } catch (Throwable t) {
                    ExtraArmory.LOG.error("Failed to init compatibility module: " + module.getName(), t);
                }
            }
        }
    }

    public static void postInit() {
        for (ICompatModule module : MODULES) {
            if (shouldLoad(module)) {
                ExtraArmory.LOG.info("Post-Initializing compatibility module for: {}", module.getName());
                try {
                    module.postInit();
                } catch (Throwable t) {
                    ExtraArmory.LOG.error("Failed to post-init compatibility module: " + module.getName(), t);
                }
            }
        }
    }

    private static boolean shouldLoad(ICompatModule module) {
        return "Forge".equals(module.getModId()) || Loader.isModLoaded(module.getModId());
    }
}