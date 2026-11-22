package com.ans.client.mob.player;

import com.ans.Ans;
import com.ans.client.equipment.EquipmentQueries;
import com.ans.client.molang.EngineVariables;
import com.ans.client.molang.MolangContext;
import com.ans.client.molang.MolangQueries;
import com.ans.client.molang.Node;
import com.ans.client.render.UiRender;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class PlayerQueries {
    static final boolean SLEEP_ROTATION = !"false".equalsIgnoreCase(System.getProperty("ans.bedrockSleepRotation", "true"));
    private static @Nullable Field cooldownsField;
    private static @Nullable Field tickCountField;
    private static @Nullable Field startField;
    private static @Nullable Field endField;
    private static boolean reflectionFailed;

    private PlayerQueries() {
    }

    static float sleepRotation(Direction bed) {
        return switch (bed) {
            case SOUTH -> 90.0F;
            case NORTH -> 270.0F;
            case EAST -> 180.0F;
            default -> 0.0F;
        };
    }

    public static void install() {
        generic();
        perType();
        engineVariables();
    }

    private static void generic() {
        MolangQueries.register("has_cape", var0 -> c -> c.actor instanceof AbstractClientPlayer p && hasCape(p) ? 1 : 0);
        MolangQueries.register("cooldown_time", a -> c -> cooldown(c, a, true));
        MolangQueries.register("cooldown_time_remaining", a -> c -> cooldown(c, a, false));
        MolangQueries.register("camera_distance_range_lerp", a -> c -> {
            if (c.actor != null && a.length >= 2) {
                double from = a[0].eval(c);
                double to = a[1].eval(c);
                if (to == from) {
                    return 0.0;
                }

                Vec3 cam = Minecraft.getInstance().gameRenderer.mainCamera().position();
                double d = cam.distanceTo(c.actor.getPosition(c.partialTick));
                return Math.clamp((d - from) / (to - from), 0.0, 1.0);
            } else {
                return 0.0;
            }
        });
        MolangQueries.register("get_root_locator_offset", var0 -> var0x -> 0.0);
    }

    static boolean hasCape(AbstractClientPlayer p) {
        boolean cape = p.getSkin().cape() != null || !PlayerRenderer.TEST_CAPES.isEmpty() && PlayerRenderer.TEST_CAPES.containsKey(p.getGameProfile().name());
        return cape && p.isModelPartShown(PlayerModelPart.CAPE);
    }

    private static void perType() {
        MolangQueries.registerFor(
            EntityTypes.PLAYER,
            "head_y_rotation",
            (Node[] var0) -> (MolangContext c) -> c.entity == null ? 0.0 : Mth.wrapDegrees(Mth.rotLerp(c.partialTick, c.entity.yHeadRotO, c.entity.yHeadRot))
        );
        MolangQueries.registerFor(EntityTypes.PLAYER, "ride_body_y_rotation", (Node[] var0) -> (MolangContext c) -> {
            Entity v = c.actor == null ? null : c.actor.getVehicle();
            return v == null ? 0.0 : (v instanceof LivingEntity l ? Mth.rotLerp(c.partialTick, l.yBodyRotO, l.yBodyRot) : v.getYRot(c.partialTick));
        });
        MolangQueries.registerFor(
            EntityTypes.PLAYER,
            "is_swimming",
            (Node[] var0) -> (MolangContext c) -> c.entity == null || !c.entity.isSwimming() && (!c.entity.isVisuallySwimming() || !c.entity.isInWater()) ? 0.0 : 1.0
        );
        MolangQueries.registerFor(EntityTypes.PLAYER, "is_jumping", (Node[] var0) -> (MolangContext c) -> {
            LivingEntity e = c.entity;
            if (e == null) {
                return 0.0;
            } else if (e instanceof LocalPlayer local) {
                return local.input.keyPresses.jump() ? 1.0 : 0.0;
            } else {
                return !e.onGround() && e.getY() - e.yo > 0.01 && !e.isInWater() ? 1.0 : 0.0;
            }
        });
        MolangQueries.registerFor(
            EntityTypes.PLAYER,
            "main_hand_item_use_duration",
            (Node[] var0) -> (MolangContext c) -> c.entity != null && c.entity.isUsingItem() && c.entity.getUsedItemHand() == InteractionHand.MAIN_HAND
                ? c.entity.getTicksUsingItem(c.partialTick) / 20.0
                : 0.0
        );
        MolangQueries.registerFor(
            EntityTypes.PLAYER,
            "sleep_rotation",
            (Node[] var0) -> (MolangContext c) -> c.entity != null && c.entity.getBedOrientation() != null
                ? (SLEEP_ROTATION ? sleepRotation(c.entity.getBedOrientation()) : c.entity.getBedOrientation().toYRot())
                : 0.0
        );
        MolangQueries.registerFor(
            EntityTypes.PLAYER,
            "walk_distance",
            (Node[] var0) -> (MolangContext c) -> c.actor instanceof AbstractClientPlayer p ? p.avatarState().getInterpolatedWalkDistance(c.partialTick) : 0.0
        );
        MolangQueries.registerFor(EntityTypes.PLAYER, "is_in_ui", (Node[] var0) -> (MolangContext c) -> UiRender.active() ? 1.0 : 0.0);
    }

    private static void engineVariables() {
        EngineVariables.registerForPartial(EntityTypes.PLAYER, "is_holding_right", (p, var1) -> p.getMainHandItem().isEmpty() ? 0.0 : 1.0);
        EngineVariables.registerForPartial(EntityTypes.PLAYER, "is_holding_left", (p, var1) -> p.getOffhandItem().isEmpty() ? 0.0 : 1.0);
        EngineVariables.registerForPartial(EntityTypes.PLAYER, "player_x_rotation", (p, pt) -> p.getXRot(pt));
        EngineVariables.registerForPartial(EntityTypes.PLAYER, "gliding_speed_value", (p, var1) -> glidingSpeed(p));
        EngineVariables.registerForPartial(EntityTypes.PLAYER, "is_holding_spyglass", (p, var1) -> p.isScoping() ? 1.0 : 0.0);
        EngineVariables.registerForPartial(EntityTypes.PLAYER, "is_tooting_goat_horn", (p, var1) -> using(p, ItemUseAnimation.TOOT_HORN) ? 1.0 : 0.0);
        EngineVariables.registerForPartial(EntityTypes.PLAYER, "is_using_brush", (p, var1) -> using(p, ItemUseAnimation.BRUSH) ? 1.0 : 0.0);

        for (String zero : new String[]{"is_first_person", "is_paperdoll", "map_face_icon", "bob_animation", "short_arm_offset_left", "short_arm_offset_right"}) {
            EngineVariables.registerForPartial(EntityTypes.PLAYER, zero, (var0, var1) -> 0.0);
        }
    }

    private static boolean using(Player p, ItemUseAnimation anim) {
        return p.isUsingItem() && p.getUseItem().getUseAnimation() == anim;
    }

    static float glidingSpeed(LivingEntity e) {
        float speed = 1.0F;
        if (e.isFallFlying()) {
            speed = (float)e.getDeltaMovement().lengthSqr() / 0.2F;
            speed = speed * speed * speed;
        }

        return Math.max(1.0F, speed);
    }

    private static double cooldown(MolangContext c, Node[] a, boolean total) {
        if (!(c.entity instanceof Player player)) {
            return 0.0;
        } else {
            ItemStack stack;
            if (a.length > 0 && a[0].value(c) instanceof String slotName) {
                EquipmentSlot slot = EquipmentQueries.slot(slotName);
                stack = slot == null ? ItemStack.EMPTY : player.getItemBySlot(slot);
            } else {
                stack = player.getMainHandItem();
            }

            if (stack.isEmpty()) {
                return 0.0;
            }

            ItemCooldowns cooldowns = player.getCooldowns();
            if (!cooldowns.isOnCooldown(stack)) {
                return 0.0;
            }

            int[] se = instance(cooldowns, cooldowns.getCooldownGroup(stack));
            if (se == null) {
                return total ? 1.0 : cooldowns.getCooldownPercent(stack, c.partialTick);
            }

            int now = tickCount(cooldowns);
            return total ? (se[1] - se[0]) / 20.0 : Math.max(0.0F, se[1] - (now + c.partialTick)) / 20.0;
        }
    }

    private static int @Nullable [] instance(ItemCooldowns cooldowns, Identifier group) {
        if (reflectionFailed) {
            return null;
        }

        try {
            if (cooldownsField == null) {
                cooldownsField = ItemCooldowns.class.getDeclaredField("cooldowns");
                cooldownsField.setAccessible(true);
                tickCountField = ItemCooldowns.class.getDeclaredField("tickCount");
                tickCountField.setAccessible(true);
                Class<?> inst = Class.forName("net.minecraft.world.item.ItemCooldowns$CooldownInstance");
                startField = inst.getDeclaredField("startTime");
                startField.setAccessible(true);
                endField = inst.getDeclaredField("endTime");
                endField.setAccessible(true);
            }

            Object i = ((Map)cooldownsField.get(cooldowns)).get(group);
            return i == null ? null : new int[]{startField.getInt(i), endField.getInt(i)};
        } catch (ReflectiveOperationException | RuntimeException e) {
            reflectionFailed = true;
            Ans.LOGGER.warn("[ans] item cooldown internals unavailable ({}); q.cooldown_time reads 1 s", e.toString());
            return null;
        }
    }

    private static int tickCount(ItemCooldowns cooldowns) {
        try {
            return tickCountField == null ? 0 : tickCountField.getInt(cooldowns);
        } catch (ReflectiveOperationException e) {
            return 0;
        }
    }
}
