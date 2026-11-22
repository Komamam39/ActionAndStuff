package com.ansviewmodel.render;

import com.ansviewmodel.config.ViewmodelConfig;
import com.ansviewmodel.config.ViewmodelSetting;

public final class ArmPoseDelta {
    public static final String BASE_POSE_ANIMATION = "animation.player.first_person_empty_hand";
    public static final String[] ARM_BONES = new String[]{"rightarm", "leftarm"};
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    private static final float[] ROTATION_SIGN = new float[]{-1.0F, -1.0F, 1.0F};
    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0);
    private static final ViewmodelSetting[] OFFSET = new ViewmodelSetting[]{ViewmodelSetting.OFFSET_X, ViewmodelSetting.OFFSET_Y, ViewmodelSetting.OFFSET_Z};
    private static final ViewmodelSetting[] SCALE = new ViewmodelSetting[]{ViewmodelSetting.SCALE_X, ViewmodelSetting.SCALE_Y, ViewmodelSetting.SCALE_Z};
    private static final ViewmodelSetting[] ROTATION = new ViewmodelSetting[]{
        ViewmodelSetting.ROTATION_X, ViewmodelSetting.ROTATION_Y, ViewmodelSetting.ROTATION_Z
    };
    private static final float[][] CONTRIBUTION = new float[2][9];

    private ArmPoseDelta() {
    }

    public static void beginFrame() {
        for (float[] var3 : CONTRIBUTION) {
            var3[0] = var3[1] = var3[2] = var3[3] = var3[4] = var3[5] = 0.0F;
            var3[6] = var3[7] = var3[8] = 1.0F;
        }
    }

    public static void apply(ViewmodelConfig var0, int var1, float[] var2, float var3) {
        float[] var4 = CONTRIBUTION[var1];

        for (int var5 = 0; var5 < 3; var5++) {
            float var6 = var0.get(var1, ROTATION[var5]);
            float var7 = var0.get(var1, OFFSET[var5]);
            float var8 = var0.get(var1, SCALE[var5]);
            float var9 = var3 * ROTATION_SIGN[var5] * var6 * (float) (Math.PI / 180.0);
            var2[var5] += var9;
            var4[var5] += var9;
            float var10 = var3 * var7;
            var2[3 + var5] = var2[3 + var5] + var10;
            var4[3 + var5] = var4[3 + var5] + var10;
            float var11 = 1.0F + var3 * var8;
            var2[6 + var5] = var2[6 + var5] * var11;
            var4[6 + var5] = var4[6 + var5] * var11;
        }
    }

    static float[] contribution(int var0) {
        return CONTRIBUTION[var0];
    }

    public static float[] lastContribution(int var0) {
        return (float[])CONTRIBUTION[var0].clone();
    }
}
