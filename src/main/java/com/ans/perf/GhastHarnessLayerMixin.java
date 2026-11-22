package com.ans.perf;

import com.ans.client.mob.nether.HappyGhastHarnessLayer;
import com.ans.perfutil.GhastHarness;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HappyGhastHarnessLayer.class, remap = false)
public abstract class GhastHarnessLayerMixin {
    @Inject(method = "submitRenderTask", at = @At("HEAD"), cancellable = true)
    private void ans$skip(CallbackInfo var1) {
        if (GhastHarness.ON) {
            var1.cancel();
        }
    }
}
