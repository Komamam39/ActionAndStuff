package com.ans.perf;

import com.ans.client.bedrock.EntityBundle;
import com.ans.client.molang.MolangContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.ans.client.bedrock.EntityRuntime", remap = false)
public abstract class FpParticlesOnMixin {
    @Shadow
    @Final
    private EntityBundle bundle;
    @Shadow
    @Final
    private MolangContext ctx;
    @Shadow
    private boolean ui;

    @Inject(method = "particlesOn", at = @At("RETURN"), cancellable = true, require = 0)
    private void ans$fp(CallbackInfoReturnable<Boolean> var1) {
        if (!var1.getReturnValueZ() && !"false".equals(System.getProperty("ans.fpParticles", "true"))) {
            if (!this.ui && this.ctx.attachable != null && this.ctx.attachable.firstPerson() && !this.bundle.particles.isEmpty()) {
                var1.setReturnValue(true);
            }
        }
    }
}
