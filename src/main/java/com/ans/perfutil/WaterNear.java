package com.ans.perfutil;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;

public final class WaterNear {
    private WaterNear() {
    }

    public static boolean touching(Entity var0) {
        if (var0.isInWater()) {
            return true;
        }

        BlockPos var1 = BlockPos.containing(var0.getX(), var0.getY() - 0.25, var0.getZ());
        return var0.level().getFluidState(var0.blockPosition()).is(FluidTags.WATER) || var0.level().getFluidState(var1).is(FluidTags.WATER);
    }

    public static boolean near(Entity var0) {
        if (var0.isInWater()) {
            return true;
        }

        BlockPos var1 = var0.blockPosition();
        return var0.level().getFluidState(var1).is(FluidTags.WATER) || var0.level().getFluidState(var1.below()).is(FluidTags.WATER);
    }
}
