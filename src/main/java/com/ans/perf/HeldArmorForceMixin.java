package com.ans.perf;

import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.molang.MolangScope;
import com.ans.perfutil.HeldArmorForce;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRuntime.class, remap = false)
public abstract class HeldArmorForceMixin {
    @Shadow
    @Final
    private String mobId;
    @Shadow
    @Final
    private MolangScope vars;
    @Shadow
    private boolean ui;
    @Shadow
    private boolean firstPerson;

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/ans/client/bedrock/EntityRuntime;execScript(Ljava/util/List;Lcom/ans/client/molang/MolangContext;)V",
            ordinal = 1,
            shift = Shift.AFTER
        ),
        require = 0
    )
    private void ans$heldArmor(Entity var1, float var2, GeoRenderState var3, CallbackInfoReturnable<FrameResult> var4) {
        if (!this.ui && "player".equals(this.mobId) && var1 instanceof Player var5) {
            HeldArmorForce.apply(this.vars, var5.getMainHandItem(), this.firstPerson);
        }
    }
}
