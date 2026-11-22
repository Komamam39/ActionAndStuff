package com.ans.perf;

import com.ans.client.molang.BlockTagQueries;
import java.util.Locale;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockTagQueries.class, remap = false)
public abstract class BlockTagLadderMixin {
    @Inject(method = "has", at = @At("HEAD"), cancellable = true)
    private static void ans$ladder(BlockState var0, String var1, CallbackInfoReturnable<Boolean> var2) {
        if (var0.is(Blocks.LADDER) && "wood".equals(var1.toLowerCase(Locale.ROOT).replace("minecraft:", ""))) {
            var2.setReturnValue(true);
        }
    }
}
