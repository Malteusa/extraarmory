package com.malts.extraarmory.compat;

import ckathode.weaponmod.item.MeleeCompBoomerang;
import ckathode.weaponmod.item.MeleeCompHalberd;
import ckathode.weaponmod.item.MeleeCompWarhammer;
import ckathode.weaponmod.item.MeleeComponent;
import com.malts.extraarmory.compat.aether.AetherCompat;
import com.malts.extraarmory.event.ExtraArmoryEventHandler;
import com.malts.extraarmory.items.ItemCustomFlail;
import com.malts.extraarmory.items.ItemCustomMelee;
import com.malts.extraarmory.items.ItemCustomMusket;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.oredict.OreDictionary;

public class SpecialEffectHelper {

    private static ItemStack ambrosiumCache = null;
    private static boolean ambrosiumSearched = false;

    public static SpecialEffectType getEffectType(ItemStack stack) {
        if (stack == null) return SpecialEffectType.NONE;
        Item item = stack.getItem();
        if (item instanceof ItemCustomMelee) {
            return ((ItemCustomMelee) item).getSpecialEffect();
        } else if (item instanceof ItemCustomFlail) {
            return ((ItemCustomFlail) item).getSpecialEffect();
        } else if (item instanceof ItemCustomMusket) {
            return ((ItemCustomMusket) item).getSpecialEffect();
        }
        return SpecialEffectType.NONE;
    }

    public static boolean isKnightmetalTargetsArmored(ItemStack stack) {
        if (stack == null) return true;
        Item item = stack.getItem();

        if (item instanceof ItemCustomFlail) {
            return false;
        }

        if (item instanceof ItemCustomMelee) {
            MeleeComponent comp = ((ItemCustomMelee) item).meleeComponent;
            if (comp instanceof MeleeCompHalberd) {
                return MeleeCompHalberd.getHalberdState(stack);
            }
            if (comp instanceof MeleeCompWarhammer || comp instanceof MeleeCompBoomerang) {
                return false;
            }
        }

        return true;
    }

    public static boolean isNeptuniumBonusApplicable(EntityLivingBase victim) {
        if (victim == null) return false;

        if (victim.isInWater() || victim.isInsideOfMaterial(Material.water)) {
            return true;
        }

        if (isAquaticMob(victim)) {
            return true;
        }

        World world = victim.worldObj;
        if (world.isRaining() && !world.provider.hasNoSky) {
            int x = MathHelper.floor_double(victim.posX);
            int y = MathHelper.floor_double(victim.posY);
            int z = MathHelper.floor_double(victim.posZ);
            int topY = MathHelper.floor_double(victim.posY + victim.height);

            if (world.canLightningStrikeAt(x, y, z) || world.canLightningStrikeAt(x, topY, z)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAquaticMob(EntityLivingBase entity) {
        if (entity instanceof EntityWaterMob) {
            return true;
        }
        EnumCreatureAttribute attr = entity.getCreatureAttribute();
        return attr != null && ("WATER".equals(attr.name()) || "AQUATIC".equals(attr.name()));
    }

    public static void applySpecialEffect(ItemStack stack, EntityLivingBase victim, Entity attacker) {
        if (stack == null || victim == null) return;
        SpecialEffectType effect = getEffectType(stack);

        switch (effect) {
            case FIERY:
                victim.setFire(15);
                if (victim.worldObj instanceof WorldServer) {
                    ((WorldServer) victim.worldObj).func_147487_a("flame", victim.posX, victim.posY + victim.height * 0.5D, victim.posZ, 12, 0.25D, 0.25D, 0.25D, 0.05D);
                }
                break;

            case HOLYSTONE:
                if (!victim.worldObj.isRemote && victim.worldObj.rand.nextInt(20) == 0) {
                    ItemStack shard = getAmbrosiumShard();
                    if (shard != null) {
                        victim.entityDropItem(shard.copy(), 0.5F);
                        victim.worldObj.playSoundAtEntity(victim, "random.pop", 0.5F, 1.2F);
                    }
                }
                break;

            case GRAVITITE:
                applyGravititeFling(victim);
                ExtraArmoryEventHandler.queueGravititeFling(victim);
                break;

            case KNIGHTMETAL:
            case ZANITE:
            case JADE:
            case NEPTUNIUM:
            case CONTINUUM:
            case SKYROOT:
            case NONE:
            default:
                break;
        }
    }

    public static void applyGravititeFling(EntityLivingBase victim) {
        if (victim == null || !victim.isEntityAlive()) return;

        boolean departure = AetherCompat.isDeparture();
        victim.motionY = departure ? 1.0D : 1.5D;
        victim.isAirBorne = true;
        victim.onGround = false;
        victim.velocityChanged = true;

        if (victim instanceof EntityPlayerMP) {
            ((EntityPlayerMP) victim).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(victim));
        }

        if (departure && victim.worldObj instanceof WorldServer) {
            ((WorldServer) victim.worldObj).func_147487_a("cloud", victim.posX, victim.posY + 0.5D, victim.posZ, 20, 0.4D, 0.2D, 0.4D, 0.05D);
        }

        victim.worldObj.playSoundAtEntity(victim, "random.bow", 0.8F, 0.5F);
    }

    private static ItemStack getAmbrosiumShard() {
        if (!ambrosiumSearched) {
            ambrosiumSearched = true;
            List<ItemStack> ores = OreDictionary.getOres("shardAmbrosium");
            if (ores.isEmpty()) ores = OreDictionary.getOres("gemAmbrosium");

            if (!ores.isEmpty()) {
                ambrosiumCache = ores.get(0).copy();
                ambrosiumCache.stackSize = 1;
            } else {
                Item item = GameRegistry.findItem("aether", "ambrosium_shard");
                if (item == null) item = GameRegistry.findItem("aether_legacy", "ambrosium_shard");
                if (item == null) item = GameRegistry.findItem("aether", "ambrosiumShard");

                if (item != null) {
                    ambrosiumCache = new ItemStack(item, 1, 0);
                }
            }
        }
        return ambrosiumCache;
    }
}
