package com.ans.client.bedrock;

import com.geckolib.animation.state.AnimationPoint;

public final class GeoCompat {
    private GeoCompat() {
    }

    public static AnimationPoint next(AnimationPoint var0, double var1) {
        return var0.createNext(var1, null);
    }
}
