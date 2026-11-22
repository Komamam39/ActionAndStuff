package com.ans.perf;

import com.ans.client.mob.player.PlayerRenderState;
import com.ans.client.mob.player.PlayerRenderer;
import com.ans.perfutil.GlideState;
import com.ans.perfutil.WaterNear;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerRenderer.class, remap = false)
public abstract class ElytraRollMixin {
    @Unique
    private static final DataTicket<Float> ANS_ROLL = DataTicket.create("ans_elytra_roll", Float.class);
    @Unique
    private static final boolean ANS_WATER = !"false".equals(System.getProperty("ans.swimSurface", "true"));
    @Unique
    private static final boolean ANS_ON = !"false".equals(System.getProperty("ans.elytraRoll", "true"));

    @Inject(method = "extract", at = @At("TAIL"), require = 0)
    private static void ans$extract(AbstractClientPlayer var0, PlayerRenderState var1, float var2, CallbackInfo var3) {
        float var4 = 0.0F;
        boolean var5 = GlideState.gliding(var0);
        if (var5 && !WaterNear.touching(var0)) {
            var1.isFallFlying = true;
            var1.fallFlyingTimeInTicks = GlideState.ticks(var0, var2);
        }

        if (ANS_ON && var5 && !WaterNear.touching(var0)) {
            Vec3 var6 = var0.getViewVector(var2);
            Vec3 var7 = var0.avatarState().deltaMovementOnPreviousTick().lerp(var0.getDeltaMovement(), var2);
            double var8 = var7.horizontalDistanceSqr();
            double var10 = var6.horizontalDistanceSqr();
            if (var8 > 1.0E-5 && var10 > 1.0E-5) {
                double var12 = (var7.x * var6.x + var7.z * var6.z) / Math.sqrt(var8 * var10);
                double var14 = var7.x * var6.z - var7.z * var6.x;
                var4 = (float)(Math.signum(var14) * Math.acos(Math.min(1.0, Math.max(-1.0, var12))));
                float var16 = Math.min(1.0F, GlideState.ticks(var0, var2) / 10.0F) * (float)Math.min(1.0, Math.sqrt(var8) / 0.3);
                var4 *= var16;
            }
        }

        ((GeoRenderState)var1).addGeckolibData(ANS_ROLL, var4);
        if (ANS_WATER && var1.isFallFlying && WaterNear.touching(var0)) {
            var1.isFallFlying = false;
        }
    }

    @Inject(method = "applyRotations", at = @At("RETURN"), require = 0)
    private void ans$roll(RenderPassInfo<?> var1, PoseStack var2, float var3, CallbackInfo var4) {
        Float var5 = var1.getGeckolibData(ANS_ROLL);
        if (var5 != null && var5 != 0.0F && var1.renderState() instanceof PlayerRenderState var6 && var6.isFallFlying) {
            var2.mulPose(new Matrix4f().rotationY(var5));
        }
    }
}
