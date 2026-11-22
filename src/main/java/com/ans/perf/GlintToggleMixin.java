package com.ans.perf;

import com.ans.client.render.LayerSubmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LayerSubmitter.class, remap = false)
public abstract class GlintToggleMixin {
    @Unique
    private static final boolean ANS_VANILLA_GLINT = !"false".equalsIgnoreCase(System.getProperty("ans.vanillaGlint", "true"));

    @Inject(method = "submitGlint", at = @At("HEAD"), cancellable = true)
    private static void ans$glintToggle(CallbackInfo var0) {
        if (!ANS_VANILLA_GLINT) {
            var0.cancel();
        }
    }
}
