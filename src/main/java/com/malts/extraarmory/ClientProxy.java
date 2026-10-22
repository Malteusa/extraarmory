package com.malts.extraarmory;

import ckathode.weaponmod.render.RenderLongItem;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerLongWeaponRenderer(Item item) {
        if (item != null) {
            MinecraftForgeClient.registerItemRenderer(item, RenderLongItem.INSTANCE);
        }
    }
}