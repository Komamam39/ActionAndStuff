package com.ans.perf;

import com.ans.client.particle.ParticleDef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.ans.client.particle.Emitter", remap = false)
public abstract class ProbeEmitterMixin {
    @Unique
    private static final Logger ANS_PROBE = LoggerFactory.getLogger("ans_probe");
    @Unique
    private static final boolean ANS_ON = !"false".equalsIgnoreCase(System.getProperty("ans.probe", "true"));
    @Unique
    private static int ans$count;
    @Unique
    private static int ans$fish;
    @Shadow
    @Final
    ParticleDef def;
    @Shadow
    @Final
    @Nullable Entity actor;
    @Shadow
    @Final
    @Nullable String locator;

    @Inject(method = "<init>", at = @At("RETURN"), require = 0)
    private void ans$probeNew(CallbackInfo var1) {
        if (ANS_ON && this.def != null) {
            String var2 = this.def.id;
            Entity var3 = this.actor;
            String var4 = var3 == null ? "null" : var3.getType().toShortString();
            boolean var5 = var2.contains("aynkct")
                || var2.contains("dymxlh")
                || var2.contains("paljdo")
                || var2.contains("lhcxiq")
                || var2.contains("bguunf")
                || var2.contains("mtlnnp")
                || var2.contains("zjohdw")
                || var2.contains("xkagev")
                || var2.contains("waekpm")
                || var2.contains("gvzgfe")
                || var2.contains("wwvczy")
                || var2.contains("vfxqhr")
                || "arrow".equals(var4);
            boolean var6 = var3 instanceof WaterAnimal;
            if (var5 || var6) {
                if (var6) {
                    if (ans$fish++ > 300) {
                        return;
                    }
                } else if (ans$count++ > 600) {
                    return;
                }

                ANS_PROBE.info(
                    "[ans_probe] NEW {} actor={}#{} tick={} loc={} pos={} vel={}",
                    var2,
                    var4,
                    var3 == null ? -1 : var3.getId(),
                    var3 == null ? -1 : var3.tickCount,
                    this.locator,
                    var3 == null ? "?" : String.format("%.2f,%.2f,%.2f", var3.getX(), var3.getY(), var3.getZ()),
                    var3 == null ? "?" : String.format("%.2f,%.2f,%.2f", var3.getDeltaMovement().x, var3.getDeltaMovement().y, var3.getDeltaMovement().z)
                );
            }
        }
    }

    @Inject(method = "expire", at = @At("HEAD"), require = 0)
    private void ans$probeExpire(CallbackInfo var1) {
        if (ANS_ON && this.def != null) {
            String var2 = this.def.id;
            if (var2.contains("aynkct")) {
                Entity var3 = this.actor;
                if (ans$count++ <= 600) {
                    ANS_PROBE.info(
                        "[ans_probe] EXPIRE {} actor={}#{} tick={}",
                        var2,
                        var3 == null ? "null" : var3.getType().toShortString(),
                        var3 == null ? -1 : var3.getId(),
                        var3 == null ? -1 : var3.tickCount
                    );
                }
            }
        }
    }
}
