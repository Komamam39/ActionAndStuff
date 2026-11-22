package com.ans.perf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.ans.client.bedrock.EntityRuntime$AnimationPlayer", remap = false)
public abstract class LoopDelayMixin {
    @Unique
    private static final boolean ANS_HOLD = "true".equals(System.getProperty("ans.loopDelayHold"));
    @Shadow
    double loopWait;

    @Inject(method = "contribute", at = @At("HEAD"), cancellable = true, require = 0)
    private void ans$skipWhileWaiting(double var1, double var3, CallbackInfo var5) {
        if (!ANS_HOLD && this.loopWait > 0.0) {
            var5.cancel();
        }
    }
}
