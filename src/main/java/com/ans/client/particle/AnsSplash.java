package com.ans.client.particle;

import com.ans.client.molang.MolangContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.slf4j.LoggerFactory;

public final class AnsSplash {
    public static final boolean ON = !"false".equals(System.getProperty("ans.waterSplash", "true"));
    private static Method context;
    private static Field pending;
    private static Field frameId;
    private static int PROBE_N;

    private AnsSplash() {
    }

    public static void splash(Entity var0) {
        if (!(var0 instanceof Player) && PROBE_N++ < 100) {
            LoggerFactory.getLogger("ans_probe")
                .info(
                    "[ans_probe] SPLASH {} under={} inWater={} y={}",
                    var0.getType().toShortString(),
                    var0.isUnderWater(),
                    var0.isInWater(),
                    String.format("%.2f", var0.getY())
                );
        }

        if (!(var0 instanceof WaterAnimal) && !var0.isUnderWater()) {
            double var1 = var0.getDeltaMovement().length();
            emit(var0, (int)Math.max(6.0, Math.min(20.0, 6.0 + var1 * 14.0)), 0.0);
        }
    }

    public static void surface(Entity var0, int var1) {
        for (int var2 = 0; var2 < var1; var2++) {
            emit(var0, 1, 0.45);
        }
    }

    private static void emit(Entity var0, int var1, double var2) {
        if (ON && BedrockParticles.ENABLED) {
            Level var4 = var0.level();
            if (var4 != null && var4.isClientSide()) {
                ParticleDef var5 = ParticleDefs.get("minecraft:water_splash_particle_manual");
                if (var5 != null) {
                    try {
                        if (context == null) {
                            context = BedrockParticles.class.getDeclaredMethod("context");
                            context.setAccessible(true);
                            pending = BedrockParticles.class.getDeclaredField("PENDING");
                            pending.setAccessible(true);
                            frameId = BedrockParticles.class.getDeclaredField("frameId");
                            frameId.setAccessible(true);
                        }

                        BlockPos var6 = var0.blockPosition();

                        for (int var7 = 0; var7 < 2 && !var4.getFluidState(var6).is(FluidTags.WATER); var7++) {
                            var6 = var6.below();
                        }

                        for (int var27 = 0;
                            var27 < 4 && !var4.getFluidState(var6.above()).isEmpty() && var4.getFluidState(var6.above()).is(FluidTags.WATER);
                            var27++
                        ) {
                            var6 = var6.above();
                        }

                        if (var4.getFluidState(var6.above()).is(FluidTags.WATER)) {
                            return;
                        }

                        FluidState var28 = var4.getFluidState(var6);
                        if (var28.is(FluidTags.WATER) && var6.getY() + var28.getHeight(var4, var6) - (var0.getY() + var0.getBbHeight()) > 0.0) {
                            return;
                        }

                        double var8 = var28.is(FluidTags.WATER) ? var6.getY() + var28.getHeight(var4, var6) + 0.05 : var0.getY() + 0.05;
                        Emitter var10 = new Emitter(var5, (MolangContext)context.invoke(null), null, null, null, false);
                        double var11 = 0.0;
                        double var13 = 0.0;
                        if (var2 > 0.0) {
                            double var15 = Math.random() * Math.PI * 2.0;
                            double var17 = var2 * Math.sqrt(Math.random());
                            var11 = Math.cos(var15) * var17;
                            var13 = Math.sin(var15) * var17;
                        }

                        double var29 = var0.getX();
                        double var30 = var0.getZ();
                        Vec3 var19 = var0.getDeltaMovement();
                        if (var0.yo > var8 && var0.getY() <= var8 && var0.yo - var0.getY() > 1.0E-6) {
                            double var31 = (var0.yo - var8) / (var0.yo - var0.getY());
                            var29 = var0.xo + (var0.getX() - var0.xo) * var31;
                            var30 = var0.zo + (var0.getZ() - var0.zo) * var31;
                        } else if (var0.getY() > var8 && var19.y < -0.001) {
                            double var20 = Math.min(1.5, (var0.getY() - var8) / -var19.y);
                            var29 = var0.getX() + var19.x * var20;
                            var30 = var0.getZ() + var19.z * var20;
                        }

                        var10.pos.set(var29 + var11, var8, var30 + var13);
                        var10.rot.identity();
                        var10.placed = true;
                        var10.burst = var1;
                        var10.setup(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false), 0.0, frameId.getLong(null));
                        MolangContext var32 = var10.ctx.enter();

                        try {
                            if (var5.creation != null) {
                                var5.creation.exec(var10.ctx);
                            }
                        } finally {
                            MolangContext.restore(var32);
                        }

                        ((List)pending.get(null)).add(var10);
                    } catch (ReflectiveOperationException | RuntimeException var26) {
                    }
                }
            }
        }
    }
}
