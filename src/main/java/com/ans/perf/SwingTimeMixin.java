package com.ans.perf;

import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.molang.MolangScope;
import com.ans.client.molang.MolangVars;
import com.ans.perfutil.SwingTracker;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRuntime.class, remap = false)
public abstract class SwingTimeMixin {
    @Unique
    private static int ans$slot = -1;
    @Shadow
    @Final
    private MolangScope vars;

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/ans/client/molang/EngineVariables;apply(Lnet/minecraft/world/entity/Entity;FLcom/ans/client/molang/MolangScope;)V",
            shift = Shift.AFTER
        )
    )
    private void ans$swingTime(Entity var1, float var2, GeoRenderState var3, CallbackInfoReturnable<FrameResult> var4) {
        if (var1 instanceof LivingEntity var5) {
            EntityType var6 = var5.getType();
            if (var6 != EntityTypes.HOGLIN && var6 != EntityTypes.ZOGLIN && var6 != EntityTypes.CREAKING && var6 != EntityTypes.WARDEN) {
                if (ans$slot < 0) {
                    ans$slot = MolangVars.id("attack_time");
                }

                double var7 = SwingTracker.attackTime(var5, var2);
                if (var7 >= 0.0) {
                    this.vars.set(ans$slot, var7);
                } else if (this.vars.getNum(ans$slot) >= 0.9999) {
                    this.vars.set(ans$slot, 0.0);
                }
            }
        }
    }
}
