package com.ans.perf;

import com.ans.client.particle.RuntimeEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RuntimeEffects.class, remap = false)
public abstract class AttachableFreshMixin {
    @Shadow
    private long tickedAt;

    @Inject(method = "captureAttachable", at = @At("RETURN"), require = 0)
    private void ans$fresh(CallbackInfo var1) {
        this.tickedAt = System.nanoTime();
    }
}
