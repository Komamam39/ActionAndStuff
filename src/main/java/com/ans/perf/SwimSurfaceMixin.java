package com.ans.perf;

import com.ans.client.molang.BlockTagQueries;
import com.ans.client.molang.MolangContext;
import com.ans.client.molang.MolangQueries;
import com.ans.client.molang.Node;
import com.ans.perfutil.WaterNear;
import java.util.Locale;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockTagQueries.class, remap = false)
public abstract class SwimSurfaceMixin {
    @Shadow
    private static BlockState block(Node[] var0, MolangContext var1) {
        throw new AssertionError();
    }

    @Shadow
    private static boolean has(BlockState var0, String var1) {
        throw new AssertionError();
    }

    @Inject(method = "install", at = @At("TAIL"))
    private static void ans$swim(CallbackInfo var0) {
        if (!"false".equals(System.getProperty("ans.swimSurface", "true"))) {
            MolangQueries.register(
                "relative_block_has_any_tag",
                var0x -> var1 -> {
                    BlockState var2 = block(var0x, var1);
                    if (var2 == null) {
                        return 0.0;
                    }

                    String[] var3 = MolangQueries.strings(var0x, 3, var1);

                    for (String var7 : var3) {
                        if (has(var2, var7)) {
                            return 1.0;
                        }
                    }

                    if (var1.actor instanceof LivingEntity var17
                        && WaterNear.near(var17)
                        && (var17.isSwimming() || var17.isVisuallySwimming())
                        && var0x.length >= 3) {
                        double var18 = var0x[0].eval(var1);
                        double var8 = var0x[1].eval(var1);
                        double var10 = var0x[2].eval(var1);
                        if (Math.abs(var18) < 0.01 && Math.abs(var10) < 0.01 && var8 <= 0.0 && var8 >= -2.0) {
                            for (String var15 : var3) {
                                if (ans$water(var15)) {
                                    return 1.0;
                                }
                            }
                        }
                    }

                    return 0.0;
                }
            );
        }
    }

    @Unique
    private static boolean ans$water(String var0) {
        return "water".equals(var0.toLowerCase(Locale.ROOT).replace("minecraft:", ""));
    }
}
