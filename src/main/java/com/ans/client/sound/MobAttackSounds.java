package com.ans.client.sound;

import com.ans.client.mob.AnimatedMob;
import com.ans.client.mob.AnimatedMobs;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class MobAttackSounds {
    private static final Set<Integer> SWINGING = new HashSet<>();

    private MobAttackSounds() {
    }

    static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(MobAttackSounds::tick);
    }

    private static void tick(Minecraft var0) {
        if (var0.level != null && !var0.isPaused()) {
            HashSet var1 = new HashSet();

            for (Entity var3 : var0.level.entitiesForRendering()) {
                boolean var4 = var3 instanceof LivingEntity var5 && var5.isSwinging();
                if (var4) {
                    var1.add(var3.getId());
                }

                if (var3 instanceof LivingEntity var8 && !(var3 instanceof Player) && var4 && !SWINGING.contains(var3.getId())) {
                    AnimatedMob var6 = AnimatedMobs.get(var3.getType());
                    if (var6 != null && PackSounds.hasOverride(var6.id(), "attack")) {
                        PackSounds.Override var7 = PackSounds.entityOverride(var6.id(), var3, "attack");
                        if (var7 != null && !var7.mute() && var7.id() != null) {
                            PackSounds.playOverride(
                                var7, var3.getX(), var3.getY(), var3.getZ(), var3.getSoundSource(), "attack swing of " + var6.id() + " #" + var3.getId()
                            );
                        }
                    }
                }
            }

            SWINGING.clear();
            SWINGING.addAll(var1);
        }
    }
}
