package com.ans.client.mob.player;

import java.util.HashSet;

public final class ExpressionEyeCheck {
    static final int CORNER_PIXELS = 256;

    private ExpressionEyeCheck() {
    }

    public static boolean hasEyeData(int[] var0) {
        if (var0 != null && var0.length > 256) {
            HashSet var1 = new HashSet();

            for (int var2 = 256; var2 < var0.length; var2++) {
                if (var0[var2] >>> 24 >= 128) {
                    var1.add(var0[var2] | 0xFF000000);
                }
            }

            return var1.size() >= 2;
        } else {
            return false;
        }
    }
}
