package com.ans.perf;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityType.class, remap = false)
public abstract class WitherHitboxMixin {
    @Unique
    private static final float[] ANS_BOX = ans$box();

    @Unique
    private static float[] ans$box() {
        try {
            String[] var0 = System.getProperty("ans.witherHitbox", "1.5,3.0").split(",");
            return new float[]{Float.parseFloat(var0[0].trim()), Float.parseFloat(var0[1].trim())};
        } catch (RuntimeException var1) {
            return new float[]{1.5F, 3.0F};
        }
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true, require = 0)
    private void ans$witherBox(CallbackInfoReturnable<EntityDimensions> var1) {
        if ((Object)this == EntityTypes.WITHER) {
            var1.setReturnValue(EntityDimensions.scalable(ANS_BOX[0], ANS_BOX[1]));
        }
    }
}
