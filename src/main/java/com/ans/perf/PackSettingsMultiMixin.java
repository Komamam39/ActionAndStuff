package com.ans.perf;

import com.ans.client.molang.PackSettings;
import com.ans.perfutil.MultiSelect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PackSettings.class, remap = false)
public abstract class PackSettingsMultiMixin {
    @Inject(method = "selected", at = @At("HEAD"), cancellable = true)
    private static void ans$multi(String var0, String var1, CallbackInfoReturnable<Boolean> var2) {
        if (MultiSelect.isMulti(var0)) {
            var2.setReturnValue(MultiSelect.contains(var0, var1));
        }
    }

    @Inject(method = "invalidate", at = @At("HEAD"))
    private static void ans$invalidate(CallbackInfo var0) {
        MultiSelect.invalidate();
    }
}
