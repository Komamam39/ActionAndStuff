package com.ans.perf;

import com.ans.perfutil.SwingTracker;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class SwingStartMixin {
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z", at = @At("RETURN"))
    private void ans$swingStart(InteractionHand var1, SwingAnimation var2, boolean var3, CallbackInfoReturnable<Boolean> var4) {
        LivingEntity var5 = (LivingEntity)(Object)this;
        if (var5.level().isClientSide()) {
            SwingTracker.started(var5, var4.getReturnValueZ());
        }
    }
}
