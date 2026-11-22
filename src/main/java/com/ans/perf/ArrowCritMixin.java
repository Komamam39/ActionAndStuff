package com.ans.perf;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AbstractArrow.class, remap = false)
public abstract class ArrowCritMixin {
    @Redirect(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
        require = 0
    )
    private void ans$noCrit(Level var1, ParticleOptions var2, double var3, double var5, double var7, double var9, double var11, double var13) {
        if (var2 != ParticleTypes.CRIT || "true".equals(System.getProperty("ans.arrowCrit"))) {
            var1.addParticle(var2, var3, var5, var7, var9, var11, var13);
        }
    }
}
