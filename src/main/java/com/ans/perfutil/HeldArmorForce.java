package com.ans.perfutil;

import com.ans.client.molang.MolangScope;
import com.ans.client.molang.MolangVars;
import java.util.Arrays;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HeldArmorForce {
    private static final Logger LOG = LoggerFactory.getLogger("ans_held");
    private static final boolean LOG_ON = "true".equalsIgnoreCase(System.getProperty("ans.heldLog", "false"));
    private static final boolean ON = !"false".equalsIgnoreCase(System.getProperty("ans.heldArmorForce", "true"));
    private static final String[] TIERS = new String[]{"leather", "chainmail", "copper", "iron", "golden", "diamond", "netherite"};
    private static final String[] TIER_VAL = new String[]{"leather", "haiscg", "khmlti", "xfmoki", "qouyjp", "diamond", "anchlp"};
    private static final String[] PIECES = new String[]{"helmet", "chestplate", "leggings", "boots"};
    private static final String[] PIECE_VAL = new String[]{"uyjuxl", "nwuegn", "vgggpu", "bpzmfy"};
    private static final String[] HARNESS = new String[]{
        "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };
    private static long lastLog;

    private HeldArmorForce() {
    }

    private static int id(String var0) {
        return MolangVars.id(var0);
    }

    public static void apply(MolangScope var0, ItemStack var1, boolean var2) {
        if (ON && !var1.isEmpty()) {
            String var3 = BuiltInRegistries.ITEM.getKey(var1.getItem()).getPath();
            boolean var4 = var0.getNum(id("apeeop")) != 0.0;
            boolean var5 = false;
            int var6 = -1;
            int var7 = -1;

            for (int var8 = 0; var8 < TIERS.length; var8++) {
                if (var3.startsWith(TIERS[var8] + "_")) {
                    var6 = var8;
                }
            }

            for (int var10 = 0; var10 < PIECES.length; var10++) {
                if (var3.endsWith("_" + PIECES[var10])) {
                    var7 = var10;
                }
            }

            if (var6 >= 0 && var7 >= 0) {
                if (!var4) {
                    var0.set(id("fnxeeb"), TIER_VAL[var6]);
                    var0.set(id("ncniti"), var6);
                    var0.set(id("unnioh"), PIECE_VAL[var7]);
                }

                var5 = true;
            } else if (var3.equals("elytra")) {
                if (!var4) {
                    var0.set(id("aizveh"), 1.0);
                }

                var5 = true;
            } else if (var3.equals("turtle_helmet")) {
                if (!var4) {
                    var0.set(id("safcey"), 1.0);
                }

                var5 = true;
            } else if (var3.equals("wolf_armor")) {
                if (!var4) {
                    var0.set(id("nzmpof"), 1.0);
                    pos(var0, -3.0, 0.0, -5.942, 3.0, 6.0, 7.0);
                }

                var5 = true;
            } else if (var3.endsWith("_harness")) {
                int var11 = Arrays.asList(HARNESS).indexOf(var3.substring(0, var3.length() - "_harness".length()));
                if (var11 >= 0) {
                    if (!var4) {
                        var0.set(id("iuymts"), var11);
                        var0.set(id("fhzlqv"), 1.0);
                        pos(var0, -5.0, 0.5, -5.0, 5.0, 10.5, 5.0);
                    }

                    var5 = true;
                }
            }

            if (var5) {
                long var12 = System.currentTimeMillis();
                if (LOG_ON && var12 - lastLog > 5000L) {
                    lastLog = var12;
                    LOG.info(
                        "[ans_held] {} en la mano ({}): el pack {} la armadura 3D (evlypi={}, is_paperdoll={})",
                        var3,
                        var2 ? "1a persona" : "3a persona",
                        var4 ? "ya activo" : "NO activo; se fuerza",
                        var0.getOrNull(id("evlypi")),
                        var0.getOrNull(id("is_paperdoll"))
                    );
                }

                if (!var4) {
                    var0.set(id("apeeop"), 1.0);
                    var0.set(id("oreville_mainhand_custom_pos"), 1.0);
                    var0.set(id("oreville_mainhand_visible"), 1.0);
                }
            }
        }
    }

    private static void pos(MolangScope var0, double var1, double var3, double var5, double var7, double var9, double var11) {
        var0.set(id("swzcuz"), var1);
        var0.set(id("kectga"), var3);
        var0.set(id("ixazbz"), var5);
        var0.set(id("nkvphb"), var7);
        var0.set(id("jufwfp"), var9);
        var0.set(id("hcvyam"), var11);
    }
}
