package com.ans.client.molang;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.Time;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState.CompassTarget;
import net.minecraft.client.renderer.item.properties.numeric.Time.TimeSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class EngineQueries {
    private EngineQueries() {
    }

    public static void install() {
        bones();
        items();
        vehicles();
        objects();
    }

    private static void bones() {
        MolangQueries.registerString("bone_orientation_trs", a -> new MolangQueries.Bound() {
            @Override
            public double eval(MolangContext c) {
                return 0.0;
            }

            @Override
            public Object value(MolangContext c) {
                double[] v = a.length > 0 && c.owner instanceof MolangContext.BoneSource src ? src.boneOrientationTrs(MolangQueries.string(a[0], c)) : null;
                return MolangStruct.trs(v != null ? v : new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0});
            }
        });
        MolangQueries.registerString("bone_origin", a -> new MolangQueries.Bound() {
            @Override
            public double eval(MolangContext c) {
                return 0.0;
            }

            @Override
            public Object value(MolangContext c) {
                double[] v = a.length > 0 && c.owner instanceof MolangContext.BoneSource src ? src.boneOrigin(MolangQueries.string(a[0], c)) : null;
                if (v == null) {
                    v = new double[3];
                }

                return new MolangStruct().put("x", v[0]).put("y", v[1]).put("z", v[2]);
            }
        });
    }

    private static ItemStack mainHand(MolangContext c) {
        return c.entity == null ? ItemStack.EMPTY : c.entity.getMainHandItem();
    }

    private static void items() {
        MolangQueries.simple("item_is_charged", c -> {
            ItemStack s = mainHand(c);
            return s.is(Items.CROSSBOW) && CrossbowItem.isCharged(s) ? 1.0 : 0.0;
        });
        MolangQueries.simple("get_animation_frame", c -> {
            LivingEntity e = c.entity;
            if (e == null) {
                return 0.0;
            }

            ItemStack s = c.attachable != null ? e.getItemBySlot(attachableSlot(c.attachable.itemSlot())) : e.getMainHandItem();
            if (s.is(Items.CLOCK) || s.is(Items.COMPASS) || s.is(Items.RECOVERY_COMPASS)) {
                return EngineQueries.ItemFrames.frame(s, e);
            }

            if (s.is(Items.FISHING_ROD)) {
                return fishingRodCast(s, e) ? 1.0 : 0.0;
            }

            if (!s.is(Items.CROSSBOW)) {
                return 0.0;
            }

            if (!CrossbowItem.isCharged(s)) {
                if (e.isUsingItem() && e.getUseItem() == s) {
                    float pull = e.getTicksUsingItem(c.partialTick) / Math.max(1, CrossbowItem.getChargeDuration(s, e));
                    return pull < 0.58F ? 1.0 : (pull < 1.0F ? 2.0 : 3.0);
                } else {
                    return 0.0;
                }
            } else {
                ChargedProjectiles p = s.get(DataComponents.CHARGED_PROJECTILES);
                return p != null && p.contains(Items.FIREWORK_ROCKET) ? 5.0 : 4.0;
            }
        });
        MolangQueries.register(
            "armor_texture_slot", a -> c -> c.entity != null && a.length != 0 ? armorTextureIndex(c.entity.getItemBySlot(armorSlot((int)a[0].eval(c)))) : 0.0
        );
        MolangQueries.register("armor_color_slot", a -> c -> {
            if (c.entity != null && a.length >= 2) {
                ItemStack s = c.entity.getItemBySlot(armorSlot((int)a[0].eval(c)));
                int argb = s.isEmpty() ? -1 : DyedItemColor.getOrDefault(s, dyeable(s) ? -6265536 : -1);
                int ch = (int)a[1].eval(c);
                int v = ch == 0 ? argb >> 16 & 0xFF : (ch == 1 ? argb >> 8 & 0xFF : (ch == 2 ? argb & 0xFF : 255));
                return v / 255.0;
            } else {
                return 0.0;
            }
        });
    }

    private static EquipmentSlot attachableSlot(String slot) {
        return switch (slot) {
            case "off_hand" -> EquipmentSlot.OFFHAND;
            case "head" -> EquipmentSlot.HEAD;
            case "chest", "torso" -> EquipmentSlot.CHEST;
            case "legs" -> EquipmentSlot.LEGS;
            case "feet" -> EquipmentSlot.FEET;
            default -> EquipmentSlot.MAINHAND;
        };
    }

    private static boolean fishingRodCast(ItemStack s, LivingEntity e) {
        return e instanceof Player p && p.fishing != null && e.getItemHeldByArm(FishingHookRenderer.getHoldingArm(p)) == s;
    }

    public static EquipmentSlot armorSlot(int n) {
        return switch (n) {
            case 0 -> EquipmentSlot.HEAD;
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            case 3 -> EquipmentSlot.FEET;
            default -> EquipmentSlot.BODY;
        };
    }

    private static boolean dyeable(ItemStack s) {
        return BuiltInRegistries.ITEM.getKey(s.getItem()).getPath().startsWith("leather_");
    }

    public static int armorTextureIndex(ItemStack s) {
        if (s.isEmpty()) {
            return 0;
        }

        String path = BuiltInRegistries.ITEM.getKey(s.getItem()).getPath();
        if (path.endsWith("nautilus_armor")) {
            if (path.startsWith("copper")) {
                return 1;
            } else if (path.startsWith("iron")) {
                return 2;
            } else if (path.startsWith("golden")) {
                return 3;
            } else if (path.startsWith("diamond")) {
                return 4;
            } else {
                return path.startsWith("netherite") ? 5 : 0;
            }
        } else if (path.startsWith("leather")) {
            return 1;
        } else if (path.startsWith("iron") || path.startsWith("chainmail")) {
            return 2;
        } else if (path.startsWith("golden")) {
            return 3;
        } else if (path.startsWith("diamond")) {
            return 4;
        } else if (path.startsWith("copper")) {
            return 5;
        } else {
            return path.startsWith("netherite") ? 6 : 0;
        }
    }

    private static void vehicles() {
        MolangQueries.actor("structural_integrity", e -> e instanceof VehicleEntity v ? 40.0F - v.getDamage() : 0.0);
        MolangQueries.flag(
            "has_dash_cooldown",
            e -> e instanceof PlayerRideableJumping j && j.getJumpCooldown() > 0
                || e instanceof Camel camel && camel.isDashing()
                || e instanceof AbstractNautilus n && n.isDashing()
        );
        MolangQueries.flag("is_shield_powered", e -> e instanceof WitherBoss w && w.isPowered());
    }

    private static void objects() {
        MolangQueries.actor("texture_frame_index", e -> e instanceof ExperienceOrb orb ? orb.getIcon() : 0.0);
        MolangQueries.simple("life_span", c -> {
            if (c.actor instanceof EvokerFangs fangs) {
                float p = fangs.getAnimationProgress(c.partialTick);
                return p <= 0.0F ? 0.0 : 20.0F * (1.0F - p) + 2.0F + (c.actor.tickCount + c.partialTick);
            } else {
                return 0.0;
            }
        });
    }

    private static final class ItemFrames {
        private static @Nullable Time daytime;
        private static @Nullable Time random;
        private static final Map<CompassTarget, CompassAngle> COMPASS = new EnumMap<>(CompassTarget.class);

        static int frame(ItemStack s, LivingEntity e) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                return 0;
            }

            try {
                if (s.is(Items.CLOCK)) {
                    boolean overworld = level.dimension() == Level.OVERWORLD;
                    if (daytime == null) {
                        daytime = new Time(true, TimeSource.DAYTIME);
                    }

                    if (random == null) {
                        random = new Time(true, TimeSource.RANDOM);
                    }

                    float v = (overworld ? daytime : random).get(s, level, e, e.getId());
                    return Math.floorMod(Math.round(v * 64.0F), 64);
                } else {
                    CompassTarget target = s.is(Items.RECOVERY_COMPASS)
                        ? CompassTarget.RECOVERY
                        : (s.has(DataComponents.LODESTONE_TRACKER) ? CompassTarget.LODESTONE : CompassTarget.SPAWN);
                    float v = COMPASS.computeIfAbsent(target, t -> new CompassAngle(true, t)).get(s, level, e, e.getId());
                    return Math.floorMod(Math.round(v * 32.0F), 32);
                }
            } catch (RuntimeException ex) {
                return 0;
            }
        }
    }
}
