package com.ans.client.mob.player;

import com.ans.Ans;
import com.ans.client.attachable.AttachableRenderer;
import com.ans.client.attachable.Attachables;
import com.ans.client.bedrock.AnimationLookup;
import com.ans.client.bedrock.EntityBundle;
import com.ans.client.bedrock.EntityBundles;
import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.mixin.ItemInHandRendererAccessor;
import com.ans.client.mob.AnimatedMob;
import com.ans.client.mob.AnimatedMobs;
import com.ans.client.molang.MolangContext;
import com.ans.client.molang.MolangVars;
import com.ans.client.render.LayerSubmitter;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class FirstPerson {
    public static boolean enabled = !"false".equalsIgnoreCase(System.getProperty("ans.firstPerson", "true"));
    private static final float EYE = 1.62F;
    private static final EquipmentSlot[] HANDS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    private static final int V_IS_FIRST_PERSON = MolangVars.id("is_first_person");
    private static final int V_BOB_ANIMATION = MolangVars.id("bob_animation");
    private static final int V_IS_PAPERDOLL = MolangVars.id("is_paperdoll");
    private static final int V_MAP_FACE_ICON = MolangVars.id("map_face_icon");
    private static @Nullable EntityRuntime runtime;
    private static @Nullable LocalPlayer runtimeOwner;
    private static final LivingEntityRenderState STATE = new PlayerRenderState();
    private static final CameraRenderState CAMERA = new CameraRenderState();
    private static boolean failed;
    private static float offHandArmHeight = 1.0F;
    private static float mainHandArmHeight = 1.0F;

    private FirstPerson() {
    }

    public static @Nullable EntityRuntime runtime() {
        return runtime;
    }

    public static boolean submit(
        FirstPersonHandsAndItemsRenderer var0,
        float var1,
        PoseStack var2,
        SubmitNodeCollector var3,
        net.minecraft.client.renderer.state.level.PlayerRenderState var4,
        FirstPersonHandsAndItemsRenderState var5
    ) {
        Minecraft var6 = Minecraft.getInstance();
        LocalPlayer var7 = var6.player;
        AvatarRenderState var8 = var4.avatarRenderState;
        if (enabled && !failed && var7 != null && var8 != null && PlayerRendering.replaces(var7)) {
            if (var6.getEntityRenderDispatcher().getRenderer(var7) instanceof PlayerRenderer var9) {
                AnimatedMob var14 = AnimatedMobs.get(EntityTypes.PLAYER);
                EntityBundle var11 = var14 == null ? null : EntityBundles.get(var14.id(), var14.bundleFor(var7));
                if (var11 == null) {
                    return false;
                }

                try {
                    draw(var9, var11, (ItemInHandRendererAccessor)var0, var1, var2, var3, var7, var8.lightCoords, var4, var5, var8);
                    return true;
                } catch (RuntimeException var13) {
                    failed = true;
                    Ans.LOGGER.error("[ans] first-person view failed; Java's hands are drawn from now on (logged once)", var13);
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static EntityRuntime runtime(EntityBundle var0, LocalPlayer var1) {
        EntityRuntime var2 = runtime;
        if (var2 == null || var2.bundle() != var0 || runtimeOwner != var1 || var2.generation() != AnimationLookup.generation()) {
            var2 = new EntityRuntime("player", var0).firstPerson(true, FirstPerson::engineVariables);
            runtime = var2;
            runtimeOwner = var1;
        }

        return var2;
    }

    private static void engineVariables(MolangContext var0) {
        var0.vars.set(V_IS_FIRST_PERSON, 1.0);
        var0.vars.set(V_IS_PAPERDOLL, 0.0);
        var0.vars.set(V_MAP_FACE_ICON, 0.0);
        var0.vars.set(V_BOB_ANIMATION, Minecraft.getInstance().options.bobView().get() ? 1.0 : 0.0);
        var0.contextVars.put("player_offhand_arm_height", offHandArmHeight);
        var0.contextVars.put("player_arm_height", mainHandArmHeight);
    }

    private static void draw(
        PlayerRenderer var0,
        EntityBundle var1,
        ItemInHandRendererAccessor var2,
        float var3,
        PoseStack var4,
        SubmitNodeCollector var5,
        LocalPlayer var6,
        int var7,
        net.minecraft.client.renderer.state.level.PlayerRenderState var8,
        FirstPersonHandsAndItemsRenderState var9,
        AvatarRenderState var10
    ) {
        mainHandArmHeight = Mth.lerp(var3, var9.oldMainHandHeight, var9.mainHandHeight);
        offHandArmHeight = Mth.lerp(var3, var9.oldOffHandHeight, var9.offHandHeight);
        PlayerRenderState var11 = (PlayerRenderState)STATE;
        GeoRenderState var12 = (GeoRenderState)var11;
        var12.getDataMap().clear();
        var11.lightCoords = var7;
        var11.isInvisible = var6.isInvisible();
        var11.ageInTicks = var6.tickCount + var3;
        EntityRuntime var13 = runtime(var1, var6);
        FrameResult var14 = var13.tick(var6, var3, var12);
        var12.addGeckolibData(FrameResult.TICKET, var14);
        PlayerRenderer.hideModelParts(var6, var11);
        var14 = var12.getGeckolibData(FrameResult.TICKET);
        ArrayList<Attachables.Frame> var15 = new ArrayList<>(2);
        boolean[] var16 = new boolean[2];
        if (Attachables.ENABLED) {
            for (int var17 = 0; var17 < HANDS.length; var17++) {
                ItemStack var18 = var6.getItemBySlot(HANDS[var17]);
                if (!var18.isEmpty()) {
                    Attachables.Frame var19 = Attachables.tick(var6, HANDS[var17], var18, var3, var12, true);
                    if (var19 != null) {
                        var15.add(var19);
                        var16[var17] = true;
                    }
                }
            }

            for (Attachables.Frame var23 : var15) {
                AttachableRenderer.resolveRelative(var23, var14);
            }
        }

        if (!var6.isScoping()) {
            GeoModel var22 = var0.getGeoModel();
            var4.pushPose();
            var4.translate(0.0F, -1.62F, 0.0F);
            var4.rotateDegrees(Axis.YP, 180.0F);
            if (var6.isPassenger()) {
                var4.rotateDegrees(
                    Axis.YP, Mth.wrapDegrees(Mth.rotLerp(var3, var6.yHeadRotO, var6.yHeadRot) - Mth.rotLerp(var3, var6.yBodyRotO, var6.yBodyRot))
                );
            }

            var4.scale(var14.scaleX(), var14.scaleY(), var14.scaleZ());
            RenderPassInfo var24 = RenderPassInfo.create(var0, STATE, var4, CAMERA, true);
            var24.captureModelRenderPose();
            if (!var11.isInvisible) {
                LayerSubmitter.submit(var22, var24, var5, var14, false);
            }

            if (!var15.isEmpty()) {
                AttachableRenderer.submit(var22, var24, var5, var14, var15);
            }

            var4.popPose();
            javaHands(var2, var3, var4, var5, var6, var7, var16, var8, var9, var10);
        }
    }

    private static void javaHands(
        ItemInHandRendererAccessor var0,
        float var1,
        PoseStack var2,
        SubmitNodeCollector var3,
        LocalPlayer var4,
        int var5,
        boolean[] var6,
        net.minecraft.client.renderer.state.level.PlayerRenderState var7,
        FirstPersonHandsAndItemsRenderState var8,
        AvatarRenderState var9
    ) {
        float var10 = var9.swingAnimation;
        InteractionHand var11 = MoreObjects.firstNonNull(var8.attackHand, InteractionHand.MAIN_HAND);
        float var12 = var9.xRot;
        ItemStack var13 = var8.mainHandItem;
        ItemStack var14 = var8.offHandItem;

        for (int var15 = 0; var15 < 2; var15++) {
            InteractionHand var16 = var15 == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack var17 = var15 == 0 ? var13 : var14;
            if (var17 != null && !var17.isEmpty() && !var8.isScoping) {
                float var18 = var11 == var16 ? var10 : 0.0F;
                float var19 = var15 == 0
                    ? Mth.lerp(var1, var8.oldMainHandHeight, var8.mainHandHeight)
                    : Mth.lerp(var1, var8.oldOffHandHeight, var8.offHandHeight);
                float var20 = var15 == 0 ? var8.mainHandSwapScale : var8.offHandSwapScale;
                float var21 = var20 * (1.0F - var19);
                if (var17.has(DataComponents.MAP_ID)) {
                    var2.pushPose();
                    if (var15 != 0 || var14 != null && !var14.isEmpty()) {
                        HumanoidArm var22 = var15 == 0 ? var9.mainArm : var9.mainArm.getOpposite();
                        var0.ans$renderOneHandedMap(var2, var3, var5, var21, var22, var18, var17, var7, var8);
                    } else {
                        var0.ans$renderTwoHandedMap(var2, var3, var5, var12, var21, var18, var7, var8);
                    }

                    var2.popPose();
                } else if (!var6[var15] && !isStaleHand(var4, var15, var17)) {
                    var0.ans$submitArmWithItem(var7, var8, var1, var12, var16, var18, var17, var21, var2, var3, var5);
                }
            }
        }
    }

    public static String describe(boolean var0) {
        EntityRuntime var1 = runtime;
        return var1 == null ? "(no first-person runtime yet)" : var1.describe(var0);
    }

    // During a hotbar switch the render state may still hold the previous item;
    // drawing it would flash the vanilla item out of the hand for a frame.
    private static boolean isStaleHand(LocalPlayer player, int hand, ItemStack item) {
        return !ItemStack.isSameItem(player.getItemBySlot(HANDS[hand]), item);
    }
}
