package com.ans.perf;

import com.ans.Ans;
import com.ans.client.particle.AnsSplash;
import com.ans.perfutil.WaterNear;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, remap = false)
public abstract class SurfaceSplashMixin {
    @Unique
    private static boolean ans$logged;
    @Unique
    private static final boolean ANS_ON = !"false".equals(System.getProperty("ans.surfaceSplash", "true"));

    @Inject(method = "baseTick", at = @At("TAIL"), require = 0)
    private void ans$surface(CallbackInfo var1) {
        Entity var2 = (Entity)(Object)this;
        if (ANS_ON && var2 instanceof Player && var2.level().isClientSide() && !var2.isSpectator()) {
            if (!var2.isUnderWater() && WaterNear.touching(var2) && var2.isInWater()) {
                Vec3 var3 = var2.getDeltaMovement();
                double var4 = var3.horizontalDistance();
                double var6 = Math.abs(var2.getY() - var2.yo);
                boolean var8 = var4 >= 0.02 || var6 >= 0.02;
                if (var2.tickCount % (var8 ? 2 : 4) == 0) {
                    int var9 = var8 ? 2 + (int)Math.min(3.0, (var4 + var6) * 12.0) : 1;
                    if (!ans$logged) {
                        ans$logged = true;
                        Ans.LOGGER.info("[ans] surface splash active ({} drops)", var9);
                    }

                    AnsSplash.surface(var2, var9);
                }
            }
        }
    }
}
