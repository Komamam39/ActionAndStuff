package com.ans.perfutil;

import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.GeoQuad;
import com.geckolib.cache.model.GeoVertex;
import com.geckolib.cache.model.cuboid.CuboidGeoBone;
import com.geckolib.cache.model.cuboid.GeoCube;
import java.util.Map;
import java.util.WeakHashMap;

public final class ItemCenter {
    private static final Map<BakedGeoModel, float[]> CACHE = new WeakHashMap<>();
    private static final float[] NONE = new float[0];

    private ItemCenter() {
    }

    public static synchronized float[] of(BakedGeoModel var0, GeoBone var1) {
        float[] var2 = CACHE.get(var0);
        if (var2 == null) {
            float[] var3 = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
            walk(var1, var3, 0);
            var2 = var3[0] > var3[3] ? NONE : new float[]{(var3[0] + var3[3]) * 0.5F, (var3[1] + var3[4]) * 0.5F, (var3[2] + var3[5]) * 0.5F};
            CACHE.put(var0, var2);
        }

        return var2.length == 0 ? null : var2;
    }

    private static void walk(GeoBone var0, float[] var1, int var2) {
        if (var0 != null && var2 <= 32) {
            if (var0 instanceof CuboidGeoBone var3 && var3.cubes != null) {
                for (GeoCube var7 : var3.cubes) {
                    for (GeoQuad var11 : var7.quads()) {
                        if (var11 != null) {
                            for (GeoVertex var15 : var11.vertices()) {
                                var1[0] = Math.min(var1[0], var15.posX());
                                var1[1] = Math.min(var1[1], var15.posY());
                                var1[2] = Math.min(var1[2], var15.posZ());
                                var1[3] = Math.max(var1[3], var15.posX());
                                var1[4] = Math.max(var1[4], var15.posY());
                                var1[5] = Math.max(var1[5], var15.posZ());
                            }
                        }
                    }
                }
            }

            for (GeoBone var19 : var0.children()) {
                walk(var19, var1, var2 + 1);
            }
        }
    }
}
