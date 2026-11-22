package com.ans.perf;

import com.ans.client.molang.MolangQueries;
import com.ans.perfutil.WaterNear;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.ans.client.molang.BedrockQueries", remap = false)
public abstract class SwimCrawlMixin {
    @Unique
    private static final boolean ANS_BEDROCK = !"false".equalsIgnoreCase(System.getProperty("ans.bedrockInWater", "true"));

    @Inject(method = "movement", at = @At("TAIL"), require = 0)
    private static void ans$crawl(CallbackInfo var0) {
        if (!"false".equals(System.getProperty("ans.swimSurface", "true"))) {
            MolangQueries.flag("is_crawling", var0x -> var0x.getPose() == Pose.SWIMMING && !WaterNear.near(var0x));
            MolangQueries.actorFlag("is_in_water", var0x -> {
                boolean var1 = var0x instanceof LivingEntity var2 && (var2.isSwimming() || var2.isVisuallySwimming());
                return var1 && WaterNear.near(var0x) ? true : var0x.isInWater() && (!ANS_BEDROCK || var0x.getFluidHeight(FluidTags.WATER) >= 0.4);
            });
            MolangQueries.flag("is_gliding", var0x -> var0x.isFallFlying() && !WaterNear.touching(var0x));
        }
    }
}
