package com.malts.extraarmory.event;

import ckathode.weaponmod.PlayerWeaponData;
import ckathode.weaponmod.WeaponDamageSource;
import ckathode.weaponmod.entity.projectile.EntityFlail;
import ckathode.weaponmod.entity.projectile.EntityMaterialProjectile;
import ckathode.weaponmod.entity.projectile.EntityProjectile;
import com.malts.extraarmory.compat.SpecialEffectHelper;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.compat.erebus.ErebusCompat;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.ItemCustomFlail;
import com.malts.extraarmory.items.ItemCustomMelee;
import com.malts.extraarmory.items.ItemCustomMusket;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ExtraArmoryEventHandler {

    private static final Set<EntityLivingBase> PENDING_GRAVITITE_FLINGS = new LinkedHashSet<>();

    public static void queueGravititeFling(EntityLivingBase victim) {
        if (victim != null && victim.isEntityAlive()) {
            PENDING_GRAVITITE_FLINGS.add(victim);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityItem) {
            EntityItem item = (EntityItem) event.entity;
            ItemStack stack = item.getEntityItem();
            if (stack != null) {
                if ((stack.getItem() instanceof ItemCustomMelee && ((ItemCustomMelee) stack.getItem()).isFireproof())
                        || (stack.getItem() instanceof ItemCustomFlail && ((ItemCustomFlail) stack.getItem()).isFireproof())
                        || (stack.getItem() instanceof ItemCustomMusket && ((ItemCustomMusket) stack.getItem()).isFireproof())) {
                    EntityHelper.setImmuneToFire(item, true);
                    item.extinguish();
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemTooltip(ItemTooltipEvent event) {
        if (ExtraArmoryConfig.enableJadeArthropodDamage && event.itemStack != null && ErebusCompat.isErebusJadeSword(event.itemStack.getItem())) {
            event.toolTip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.jade"));
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !PENDING_GRAVITITE_FLINGS.isEmpty()) {
            for (EntityLivingBase victim : PENDING_GRAVITITE_FLINGS) {
                if (victim != null && victim.isEntityAlive()) {
                    SpecialEffectHelper.applyGravititeFling(victim);
                }
            }
            PENDING_GRAVITITE_FLINGS.clear();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (!ExtraArmoryConfig.enableCustomHitEffectsFix) return;
        if (event.entityLiving == null || event.source == null) return;

        AttackContext context = resolveAttackContext(event.source, event.entityLiving);
        if (context.weapon == null) return;

        SpecialEffectType effect = SpecialEffectHelper.getEffectType(context.weapon);
        Entity attacker = event.source.getEntity();

        if (effect == SpecialEffectType.KNIGHTMETAL) {
            boolean targetsArmored = SpecialEffectHelper.isKnightmetalTargetsArmored(context.weapon);
            boolean qualifies = targetsArmored
                    ? (event.entityLiving.getTotalArmorValue() > 0)
                    : (event.entityLiving.getTotalArmorValue() == 0);

            if (qualifies) {
                float critBonus = Math.max(2.0F, event.ammount * 0.25F);
                event.ammount += critBonus;

                if (attacker instanceof EntityPlayer) {
                    ((EntityPlayer) attacker).onCriticalHit(event.entityLiving);
                }
                if (event.entityLiving.worldObj instanceof WorldServer) {
                    ((WorldServer) event.entityLiving.worldObj).func_147487_a(
                            "crit",
                            event.entityLiving.posX,
                            event.entityLiving.posY + event.entityLiving.height * 0.5D,
                            event.entityLiving.posZ,
                            15, 0.3D, 0.3D, 0.3D, 0.1D
                    );
                }
            }
        }

        if (ExtraArmoryConfig.enableJadeArthropodDamage) {
            boolean isJadeWeapon = (effect == SpecialEffectType.JADE) || ErebusCompat.isErebusJadeSword(context.weapon.getItem());
            if (isJadeWeapon && event.entityLiving.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
                float critBonus = Math.max(2.0F, event.ammount * 0.25F);
                event.ammount += critBonus;

                if (event.entityLiving.worldObj instanceof WorldServer) {
                    ((WorldServer) event.entityLiving.worldObj).func_147487_a(
                            "happyVillager",
                            event.entityLiving.posX,
                            event.entityLiving.posY + event.entityLiving.height * 0.5D,
                            event.entityLiving.posZ,
                            15, 0.35D, 0.35D, 0.35D, 0.1D
                    );
                }
            }
        }

        if (effect == SpecialEffectType.ZANITE && context.weapon.getMaxDamage() > 0) {
            float durabilityRatio = (float) context.weapon.getItemDamage() / (float) context.weapon.getMaxDamage();
            float extraDamage = durabilityRatio * 4.0F;
            if (extraDamage > 0.1F) {
                event.ammount += extraDamage;
            }
        }

        if (context.isProjectile) {
            SpecialEffectHelper.applySpecialEffect(context.weapon, event.entityLiving, attacker);
        }
    }

    private static class AttackContext {
        final ItemStack weapon;
        final boolean isProjectile;

        AttackContext(ItemStack weapon, boolean isProjectile) {
            this.weapon = weapon;
            this.isProjectile = isProjectile;
        }
    }

    private static AttackContext resolveAttackContext(DamageSource source, EntityLivingBase victim) {
        if (source instanceof WeaponDamageSource) {
            EntityProjectile proj = ((WeaponDamageSource) source).getProjectile();
            if (proj instanceof EntityMaterialProjectile) {
                return new AttackContext(((EntityMaterialProjectile) proj).getWeapon(), true);
            }
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof EntityLivingBase) {
            if (attacker instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) attacker;
                if (PlayerWeaponData.isFlailThrown(player)) {
                    Entity flailEnt = player.worldObj.getEntityByID(PlayerWeaponData.getFlailEntityId(player));
                    if (flailEnt instanceof EntityFlail) {
                        EntityFlail flail = (EntityFlail) flailEnt;
                        if (flail.getDistanceSqToEntity(victim) < 36.0D) {
                            return new AttackContext(flail.getWeapon(), true);
                        }
                    }
                }
            } else if (victim != null && victim.worldObj != null) {
                @SuppressWarnings("unchecked")
                List<EntityFlail> flails = victim.worldObj.getEntitiesWithinAABB(
                        EntityFlail.class,
                        victim.boundingBox.expand(3.5D, 3.5D, 3.5D)
                );
                for (EntityFlail flail : flails) {
                    if (flail.shootingEntity == attacker) {
                        return new AttackContext(flail.getWeapon(), true);
                    }
                }
            }

            return new AttackContext(((EntityLivingBase) attacker).getHeldItem(), false);
        }

        return new AttackContext(null, false);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.entityLiving == null || event.entityLiving.worldObj.isRemote || event.source == null) return;

        AttackContext context = resolveAttackContext(event.source, event.entityLiving);
        if (context.weapon != null && SpecialEffectHelper.getEffectType(context.weapon) == SpecialEffectType.SKYROOT) {
            List<EntityItem> duplicateDrops = new ArrayList<>();
            for (EntityItem drop : event.drops) {
                ItemStack stack = drop.getEntityItem();
                if (stack != null && stack.stackSize > 0) {
                    EntityItem extra = new EntityItem(
                            event.entityLiving.worldObj,
                            drop.posX, drop.posY, drop.posZ,
                            stack.copy()
                    );
                    extra.delayBeforeCanPickup = drop.delayBeforeCanPickup;
                    duplicateDrops.add(extra);
                }
            }
            event.drops.addAll(duplicateDrops);
        }
    }
}
