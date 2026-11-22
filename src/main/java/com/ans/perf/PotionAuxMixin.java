package com.ans.perf;

import com.ans.perfutil.PotionAux;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.ans.client.AnsClient", remap = false)
public abstract class PotionAuxMixin {
    @Inject(method = "onInitializeClient", at = @At("TAIL"), require = 0)
    private void ans$potions(CallbackInfo var1) {
        PotionAux.install();
    }
}
