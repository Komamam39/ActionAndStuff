package com.ans.perfutil;

import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.molang.MolangVars;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

public final class PotionAux {
    private static final String[] IDS = new String[]{
        "water",
        "mundane",
        "long_mundane",
        "thick",
        "awkward",
        "night_vision",
        "long_night_vision",
        "invisibility",
        "long_invisibility",
        "leaping",
        "long_leaping",
        "strong_leaping",
        "fire_resistance",
        "long_fire_resistance",
        "swiftness",
        "long_swiftness",
        "strong_swiftness",
        "slowness",
        "long_slowness",
        "water_breathing",
        "long_water_breathing",
        "healing",
        "strong_healing",
        "harming",
        "strong_harming",
        "poison",
        "long_poison",
        "strong_poison",
        "regeneration",
        "long_regeneration",
        "strong_regeneration",
        "strength",
        "long_strength",
        "strong_strength",
        "weakness",
        "long_weakness",
        "wither",
        "turtle_master",
        "long_turtle_master",
        "strong_turtle_master",
        "slow_falling",
        "long_slow_falling",
        "strong_slowness",
        "wind_charged",
        "weaving",
        "oozing",
        "infested"
    };
    private static final Map<String, Integer> MAP = new HashMap<>();
    private static boolean installed;

    private PotionAux() {
    }

    public static synchronized void install() {
        if (!installed && !"false".equals(System.getProperty("ans.potionAux", "true"))) {
            installed = true;
            int var0 = MolangVars.id("ggcegi");
            EntityRuntime.addPreAnimationHook((var1, var2, var3) -> {
                if (var1 instanceof LivingEntity var4) {
                    ItemStack var5 = var4.getMainHandItem();
                    if (var5.is(Items.POTION) || var5.is(Items.SPLASH_POTION) || var5.is(Items.LINGERING_POTION)) {
                        PotionContents var6 = var5.get(DataComponents.POTION_CONTENTS);
                        int var7 = 0;
                        if (var6 != null && var6.potion().isPresent()) {
                            String var8 = var6.potion().get().unwrapKey().map(var0xx -> var0xx.identifier().getPath()).orElse("water");
                            var7 = MAP.getOrDefault(var8, 0);
                        }

                        var2.variables().set(var0, var7);
                    }
                }
            });
        }
    }

    static {
        for (int var0 = 0; var0 < IDS.length; var0++) {
            MAP.put(IDS[var0], var0);
        }
    }
}
