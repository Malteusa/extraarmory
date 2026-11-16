package com.malts.extraarmory.event;

import ckathode.weaponmod.BalkonsWeaponMod;
import ckathode.weaponmod.PlayerWeaponData;
import ckathode.weaponmod.WeaponDamageSource;
import ckathode.weaponmod.WeaponModConfig;
import ckathode.weaponmod.entity.projectile.EntityMaterialProjectile;
import ckathode.weaponmod.entity.projectile.EntityMusketBullet;
import ckathode.weaponmod.entity.projectile.EntityProjectile;
import ckathode.weaponmod.item.ItemFlail;
import com.malts.extraarmory.compat.SpecialEffectHelper;
import com.malts.extraarmory.compat.SpecialEffectType;
import com.malts.extraarmory.compat.aquaculture.AquacultureCompat;
import com.malts.extraarmory.compat.bop.BOPCompat;
import com.malts.extraarmory.compat.enderlicious.EnderliciousCompat;
import com.malts.extraarmory.compat.erebus.ErebusCompat;
import com.malts.extraarmory.compat.twilightforest.TwilightForestCompat;
import com.malts.extraarmory.config.ExtraArmoryConfig;
import com.malts.extraarmory.items.ItemCustomFlail;
import com.malts.extraarmory.items.ItemCustomMelee;
import com.malts.extraarmory.items.ItemCustomMusket;
import com.malts.extraarmory.items.WeaponCollection;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ExtraArmoryEventHandler {

    private static final String NBT_MOB_EQUIPPED = "ea_equipped";

    private static final Set<EntityLivingBase> PENDING_GRAVITITE_FLINGS = new LinkedHashSet<>();
    private static final Map<EntityMusketBullet, ItemStack> BULLET_TO_WEAPON = Collections.synchronizedMap(new WeakHashMap<>());
    private static boolean isApplyingAoE = false;
    private static Boolean tfLoaded = null;

    private static boolean isTwilightForestActive() {
        if (!ExtraArmoryConfig.enableTwilightForest) return false;
        if (tfLoaded == null) {
            tfLoaded = Loader.isModLoaded("TwilightForest");
        }
        return tfLoaded;
    }

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
            return;
        }

        if (ExtraArmoryConfig.enableMusketBulletEffects && !event.world.isRemote && event.entity instanceof EntityMusketBullet) {
            EntityMusketBullet bullet = (EntityMusketBullet) event.entity;
            Entity shooter = bullet.getThrower();
            if (shooter instanceof EntityLivingBase) {
                ItemStack held = ((EntityLivingBase) shooter).getHeldItem();
                if (held != null && held.getItem() instanceof ItemCustomMusket) {
                    BULLET_TO_WEAPON.put(bullet, held.copy());
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.entityLiving;
        if (living == null || living.worldObj.isRemote) return;
        if (living.ticksExisted > 1) return;
        if (!(living instanceof EntityLiving)) return;

        boolean isTF = isTwilightForestActive();
        if (!(living instanceof EntityZombie)
                && !(living instanceof EntitySkeleton)
                && !(isTF && (TwilightForestCompat.isMinotaur(living) || TwilightForestCompat.isSnowGuardian(living)))) {
            return;
        }

        handleMobSpawnEquipment((EntityLiving) living, living.worldObj, isTF);
    }

    private static void handleMobSpawnEquipment(EntityLiving entity, World world, boolean isTF) {
        NBTTagCompound data = entity.getEntityData();
        if (data.getBoolean(NBT_MOB_EQUIPPED)) return;
        data.setBoolean(NBT_MOB_EQUIPPED, true);

        if (entity instanceof EntityPigZombie) {
            if (ExtraArmoryConfig.enablePigZombieGoldWeapons) {
                equipPigZombieGoldWeapon((EntityPigZombie) entity);
            }
            return;
        }

        if (entity instanceof EntitySkeleton) {
            EntitySkeleton skeleton = (EntitySkeleton) entity;
            if (skeleton.getSkeletonType() == 1 && ExtraArmoryConfig.enableWitherSkeletonStoneWeapons) {
                equipWitherSkeletonStoneWeapon(skeleton);
            }
            return;
        }

        if (entity instanceof EntityZombie) {
            if (entity.getClass().getName().contains("LoyalZombie")) return;
            if (entity.getEquipmentInSlot(0) != null) return;

            if (isTF
                    && ExtraArmoryConfig.enableTFZombieWeapons
                    && TwilightForestCompat.isTwilightForestDimension(world)) {
                equipTwilightForestZombie((EntityZombie) entity);
            }
            return;
        }

        if (isTF) {
            if (ExtraArmoryConfig.enableTFMinotaurBattleaxe && TwilightForestCompat.isMinotaur(entity)) {
                equipMinotaurBattleaxe(entity);
            } else if (ExtraArmoryConfig.enableTFSnowGuardianWeapons && TwilightForestCompat.isSnowGuardian(entity)) {
                equipSnowGuardianWeapon(entity);
            }
        }
    }

    private static void equipPigZombieGoldWeapon(EntityPigZombie pigZombie) {
        if (ExtraArmoryConfig.pigZombieGoldWeaponChance <= 0.0F) return;
        ItemStack held = pigZombie.getEquipmentInSlot(0);
        if (held == null || held.getItem() != Items.golden_sword) return;

        Random rand = pigZombie.getRNG();
        if (rand.nextFloat() >= ExtraArmoryConfig.pigZombieGoldWeaponChance) return;

        WeaponModConfig modConfig = BalkonsWeaponMod.instance.modConfig;
        List<Item> candidates = new ArrayList<>(6);
        if (modConfig.isEnabled("battleaxe") && BalkonsWeaponMod.battleaxeGold != null) candidates.add(BalkonsWeaponMod.battleaxeGold);
        if (modConfig.isEnabled("katana") && BalkonsWeaponMod.katanaGold != null) candidates.add(BalkonsWeaponMod.katanaGold);
        if (modConfig.isEnabled("halberd") && BalkonsWeaponMod.halberdGold != null) candidates.add(BalkonsWeaponMod.halberdGold);
        if (modConfig.isEnabled("knife") && BalkonsWeaponMod.knifeGold != null) candidates.add(BalkonsWeaponMod.knifeGold);
        if (modConfig.isEnabled("spear") && BalkonsWeaponMod.spearGold != null) candidates.add(BalkonsWeaponMod.spearGold);
        if (modConfig.isEnabled("warhammer") && BalkonsWeaponMod.warhammerGold != null) candidates.add(BalkonsWeaponMod.warhammerGold);
        if (candidates.isEmpty()) return;

        Item chosen = candidates.get(rand.nextInt(candidates.size()));
        pigZombie.setCurrentItemOrArmor(0, new ItemStack(chosen));
    }

    private static void equipWitherSkeletonStoneWeapon(EntitySkeleton skeleton) {
        if (ExtraArmoryConfig.witherSkeletonStoneWeaponChance <= 0.0F) return;
        ItemStack held = skeleton.getEquipmentInSlot(0);
        if (held != null && held.getItem() != Items.stone_sword) return;

        Random rand = skeleton.getRNG();
        if (rand.nextFloat() >= ExtraArmoryConfig.witherSkeletonStoneWeaponChance) return;

        WeaponModConfig modConfig = BalkonsWeaponMod.instance.modConfig;
        List<Item> candidates = new ArrayList<>(6);
        if (modConfig.isEnabled("battleaxe") && BalkonsWeaponMod.battleaxeStone != null) candidates.add(BalkonsWeaponMod.battleaxeStone);
        if (modConfig.isEnabled("katana") && BalkonsWeaponMod.katanaStone != null) candidates.add(BalkonsWeaponMod.katanaStone);
        if (modConfig.isEnabled("halberd") && BalkonsWeaponMod.halberdStone != null) candidates.add(BalkonsWeaponMod.halberdStone);
        if (modConfig.isEnabled("knife") && BalkonsWeaponMod.knifeStone != null) candidates.add(BalkonsWeaponMod.knifeStone);
        if (modConfig.isEnabled("spear") && BalkonsWeaponMod.spearStone != null) candidates.add(BalkonsWeaponMod.spearStone);
        if (modConfig.isEnabled("warhammer") && BalkonsWeaponMod.warhammerStone != null) candidates.add(BalkonsWeaponMod.warhammerStone);
        if (candidates.isEmpty()) return;

        Item chosen = candidates.get(rand.nextInt(candidates.size()));
        skeleton.setCurrentItemOrArmor(0, new ItemStack(chosen));
    }

    private static void equipTwilightForestZombie(EntityZombie zombie) {
        Random rand = zombie.getRNG();
        float roll = rand.nextFloat();

        float steeleafThreshold = (ExtraArmoryConfig.enableSteeleaf
                && TwilightForestCompat.steeleafWeapons != null
                && ExtraArmoryConfig.tfSteeleafZombieChance > 0.0F)
                ? ExtraArmoryConfig.tfSteeleafZombieChance
                : 0.0F;

        if (roll < steeleafThreshold) {
            Item weapon = pickRandomMeleeWeapon(TwilightForestCompat.steeleafWeapons, rand);
            if (weapon != null) {
                ItemStack stack = new ItemStack(weapon);
                stack.addEnchantment(Enchantment.looting, 2);
                zombie.setCurrentItemOrArmor(0, stack);
                return;
            }
        }

        if (ExtraArmoryConfig.enableIronwood
                && TwilightForestCompat.ironwoodWeapons != null
                && ExtraArmoryConfig.tfIronwoodZombieChance > 0.0F
                && roll < steeleafThreshold + ExtraArmoryConfig.tfIronwoodZombieChance) {
            Item weapon = pickRandomMeleeWeapon(TwilightForestCompat.ironwoodWeapons, rand);
            if (weapon != null) {
                ItemStack stack = new ItemStack(weapon);
                stack.addEnchantment(Enchantment.knockback, 1);
                zombie.setCurrentItemOrArmor(0, stack);
            }
        }
    }

    private static void equipMinotaurBattleaxe(EntityLiving minotaur) {
        if (ExtraArmoryConfig.tfMinotaurBattleaxeChance <= 0.0F) return;
        if (!BalkonsWeaponMod.instance.modConfig.isEnabled("battleaxe") || BalkonsWeaponMod.battleaxeGold == null) return;
        ItemStack held = minotaur.getEquipmentInSlot(0);
        if (held == null || held.getItem() != Items.golden_axe) return;

        if (minotaur.getRNG().nextFloat() < ExtraArmoryConfig.tfMinotaurBattleaxeChance) {
            minotaur.setCurrentItemOrArmor(0, new ItemStack(BalkonsWeaponMod.battleaxeGold));
        }
    }

    private static void equipSnowGuardianWeapon(EntityLiving guardian) {
        if (ExtraArmoryConfig.tfSnowGuardianWeaponChance <= 0.0F) return;
        ItemStack held = guardian.getEquipmentInSlot(0);
        if (held == null) return;

        int tier = TwilightForestCompat.getSnowGuardianSwordTier(held.getItem());
        if (tier < 0) return;

        Random rand = guardian.getRNG();
        if (rand.nextFloat() >= ExtraArmoryConfig.tfSnowGuardianWeaponChance) return;

        if (tier == 0 && ExtraArmoryConfig.enableIronwood && TwilightForestCompat.ironwoodWeapons != null) {
            Item weapon = pickRandomMeleeWeapon(TwilightForestCompat.ironwoodWeapons, rand);
            if (weapon != null) {
                ItemStack stack = new ItemStack(weapon);
                stack.addEnchantment(Enchantment.knockback, 1);
                guardian.setCurrentItemOrArmor(0, stack);
            }
        } else if (tier == 1 && ExtraArmoryConfig.enableSteeleaf && TwilightForestCompat.steeleafWeapons != null) {
            Item weapon = pickRandomMeleeWeapon(TwilightForestCompat.steeleafWeapons, rand);
            if (weapon != null) {
                ItemStack stack = new ItemStack(weapon);
                stack.addEnchantment(Enchantment.looting, 2);
                guardian.setCurrentItemOrArmor(0, stack);
            }
        } else if (tier == 2 && ExtraArmoryConfig.enableKnightmetal && TwilightForestCompat.knightmetalWeapons != null) {
            Item weapon = pickRandomMeleeWeapon(TwilightForestCompat.knightmetalWeapons, rand);
            if (weapon != null) {
                guardian.setCurrentItemOrArmor(0, new ItemStack(weapon));
            }
        }
    }

    private static Item pickRandomMeleeWeapon(WeaponCollection weapons, Random rand) {
        if (weapons == null) return null;
        List<Item> candidates = new ArrayList<>(6);
        if (weapons.battleaxe != null) candidates.add(weapons.battleaxe);
        if (weapons.katana != null) candidates.add(weapons.katana);
        if (weapons.halberd != null) candidates.add(weapons.halberd);
        if (weapons.knife != null) candidates.add(weapons.knife);
        if (weapons.spear != null) candidates.add(weapons.spear);
        if (weapons.warhammer != null) candidates.add(weapons.warhammer);
        if (candidates.isEmpty()) return null;
        return candidates.get(rand.nextInt(candidates.size()));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemTooltip(ItemTooltipEvent event) {
        if (event.itemStack == null) return;

        if (ExtraArmoryConfig.enableJadeArthropodDamage && ErebusCompat.isErebusJadeSword(event.itemStack.getItem())) {
            event.toolTip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.jade"));
        }

        if (ExtraArmoryConfig.enableNeptuniumWaterDamage && AquacultureCompat.isNeptuniumTool(event.itemStack.getItem())) {
            event.toolTip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.neptunium"));
        }

        if (ExtraArmoryConfig.enableAmethystAoEDamage && BOPCompat.isAmethystSword(event.itemStack.getItem())) {
            event.toolTip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.extraarmory.amethyst"));
        }

        if (ExtraArmoryConfig.enableSugiliteTeleport && EnderliciousCompat.isSugiliteSword(event.itemStack.getItem())) {
            event.toolTip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocalFormatted("tooltip.extraarmory.sugilite", ExtraArmoryConfig.sugiliteTeleportChance));
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
        if (isApplyingAoE) return;

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

        if (ExtraArmoryConfig.enableNeptuniumWaterDamage) {
            boolean isNeptuniumWeapon = (effect == SpecialEffectType.NEPTUNIUM) || AquacultureCompat.isNeptuniumTool(context.weapon.getItem());
            if (isNeptuniumWeapon && SpecialEffectHelper.isNeptuniumBonusApplicable(event.entityLiving)) {
                float critBonus = Math.max(2.0F, event.ammount * 0.25F);
                event.ammount += critBonus;

                if (event.entityLiving.worldObj instanceof WorldServer) {
                    ((WorldServer) event.entityLiving.worldObj).func_147487_a(
                            "magicCrit",
                            event.entityLiving.posX,
                            event.entityLiving.posY + event.entityLiving.height * 0.5D,
                            event.entityLiving.posZ,
                            15, 0.35D, 0.35D, 0.35D, 0.1D
                    );
                }
            }
        }

        if (ExtraArmoryConfig.enableAmethystAoEDamage) {
            boolean isSneaking = (attacker != null && attacker.isSneaking());
            if (!isSneaking) {
                boolean isAmethystWeapon = (effect == SpecialEffectType.AMETHYST) || BOPCompat.isAmethystSword(context.weapon.getItem());
                if (isAmethystWeapon && !event.entityLiving.worldObj.isRemote) {
                    applyAmethystAoE(context.weapon, event.entityLiving, attacker, event.ammount * 0.5F);
                }
            }
        }

        boolean isFieryWeapon = (effect == SpecialEffectType.FIERY) || TwilightForestCompat.isFieryTool(context.weapon.getItem());
        if (isFieryWeapon) {
            if (event.entityLiving.worldObj instanceof WorldServer) {
                ((WorldServer) event.entityLiving.worldObj).func_147487_a(
                        "flame",
                        event.entityLiving.posX,
                        event.entityLiving.posY + event.entityLiving.height * 0.5D,
                        event.entityLiving.posZ,
                        12, 0.25D, 0.25D, 0.25D, 0.05D
                );
            }
        }

        if (ExtraArmoryConfig.enableSugiliteTeleport) {
            boolean isSneaking = (attacker != null && attacker.isSneaking());
            if (!isSneaking) {
                boolean isSugiliteWeapon = (effect == SpecialEffectType.SUGILITE) || EnderliciousCompat.isSugiliteSword(context.weapon.getItem());
                if (isSugiliteWeapon && !event.entityLiving.worldObj.isRemote) {
                    if (event.entityLiving.worldObj.rand.nextInt(100) < ExtraArmoryConfig.sugiliteTeleportChance) {
                        SpecialEffectHelper.teleportTarget(event.entityLiving, attacker);
                    }
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

    private static void applyAmethystAoE(ItemStack weapon, EntityLivingBase victim, Entity attacker, float aoeDamage) {
        if (aoeDamage <= 0.5F) return;
        World world = victim.worldObj;
        if (world.isRemote) return;

        double radius = 3.0D;
        double radiusSq = radius * radius;

        if (world instanceof WorldServer) {
            WorldServer ws = (WorldServer) world;
            ws.func_147487_a("witchMagic", victim.posX, victim.posY + victim.height * 0.5D, victim.posZ, 20, 0.6D, 0.3D, 0.6D, 0.1D);
            ws.func_147487_a("portal", victim.posX, victim.posY + victim.height * 0.5D, victim.posZ, 25, 0.6D, 0.4D, 0.6D, 0.5D);
        }

        @SuppressWarnings("unchecked")
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(
                attacker,
                victim.boundingBox.expand(radius, 1.5D, radius)
        );

        DamageSource source = (attacker instanceof EntityPlayer)
                ? DamageSource.causePlayerDamage((EntityPlayer) attacker)
                : (attacker instanceof EntityLivingBase)
                    ? DamageSource.causeMobDamage((EntityLivingBase) attacker)
                    : DamageSource.generic;

        try {
            isApplyingAoE = true;
            for (Entity entity : list) {
                if (entity instanceof EntityLivingBase && entity != attacker && entity != victim && entity.isEntityAlive()) {
                    EntityLivingBase target = (EntityLivingBase) entity;

                    if (target instanceof EntityTameable && attacker instanceof EntityPlayer) {
                        EntityTameable pet = (EntityTameable) target;
                        if (pet.isTamed() && ((EntityPlayer) attacker).getUniqueID().toString().equals(pet.func_152113_b())) {
                            continue;
                        }
                    }

                    if (target.getDistanceSqToEntity(victim) <= radiusSq) {
                        target.hurtResistantTime = 0;
                        target.attackEntityFrom(source, aoeDamage);
                    }
                }
            }
        } finally {
            isApplyingAoE = false;
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
            if (ExtraArmoryConfig.enableMusketBulletEffects && proj instanceof EntityMusketBullet) {
                ItemStack weapon = BULLET_TO_WEAPON.get((EntityMusketBullet) proj);
                if (weapon != null) {
                    return new AttackContext(weapon, true);
                }
            }
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof EntityLivingBase) {
            ItemStack held = ((EntityLivingBase) attacker).getHeldItem();
            boolean isFlailProjectile = false;

            if (held != null && held.getItem() instanceof ItemFlail) {
                if ("mob".equals(source.damageType)) {
                    isFlailProjectile = true;
                } else if (attacker instanceof EntityPlayer && PlayerWeaponData.isFlailThrown((EntityPlayer) attacker)) {
                    isFlailProjectile = true;
                }
            }

            return new AttackContext(held, isFlailProjectile);
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
