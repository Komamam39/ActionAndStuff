package com.ans.perf;

import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.bedrock.FrameResult;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRuntime.class, remap = false)
public abstract class AnimLodMixin {
    @Unique
    private static final boolean ANS_LOD = !"false".equalsIgnoreCase(System.getProperty("ans.lod", "true"));
    @Unique
    private static final double D1 = 576.0;
    @Unique
    private static final double D2 = 2304.0;
    @Unique
    private static final double D3 = 6400.0;
    @Shadow
    private boolean firstPerson;
    @Shadow
    private boolean ui;
    @Unique
    private FrameResult ansLod$last;
    @Unique
    private Entity ansLod$entity;
    @Unique
    private int ansLod$skipped;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void ansLod$maybeSkip(Entity var1, float var2, GeoRenderState var3, CallbackInfoReturnable<FrameResult> var4) {
        if (ANS_LOD && !this.firstPerson && !this.ui && !(var1 instanceof Player) && this.ansLod$last != null && this.ansLod$entity == var1) {
            Entity var5 = Minecraft.getInstance().getCameraEntity();
            if (var5 != null) {
                double var6 = var1.distanceToSqr(var5);
                int var8 = var6 < 576.0 ? 1 : (var6 < 2304.0 ? 2 : (var6 < 6400.0 ? 3 : 4));
                if (++this.ansLod$skipped < var8) {
                    var4.setReturnValue(this.ansLod$last);
                } else {
                    this.ansLod$skipped = 0;
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void ansLod$remember(Entity var1, float var2, GeoRenderState var3, CallbackInfoReturnable<FrameResult> var4) {
        if (this.ansLod$last != var4.getReturnValue()) {
            this.ansLod$last = (FrameResult)var4.getReturnValue();
            this.ansLod$entity = var1;
            this.ansLod$skipped = 0;
        }
    }
}
