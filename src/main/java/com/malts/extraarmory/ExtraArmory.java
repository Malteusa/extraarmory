package com.malts.extraarmory;

import com.malts.extraarmory.compat.CompatManager;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ExtraArmory.MOD_ID,
        name = ExtraArmory.MOD_NAME,
        version = Tags.VERSION,
        acceptedMinecraftVersions = "[1.7.10]",
        dependencies = "required-after:Forge@[10.13.4.1614,);required-after:weaponmod;after:ExpandedArmory;"
)
public class ExtraArmory {

    public static final String MOD_ID = "extraarmory";
    public static final String MOD_NAME = "Extra Armory";
    public static final Logger LOG = LogManager.getLogger(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static ExtraArmory instance;

    @SidedProxy(
            clientSide = "com.malts.extraarmory.ClientProxy",
            serverSide = "com.malts.extraarmory.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ExtraArmoryConfig.init(event.getSuggestedConfigurationFile());
        proxy.preInit(event);
        CompatManager.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        CompatManager.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        CompatManager.postInit();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}