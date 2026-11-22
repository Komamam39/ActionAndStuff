package com.ansviewmodel.config;

import java.util.Arrays;

public final class ViewmodelConfig {
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    private final float[][] values = new float[2][ViewmodelSetting.VALUES.length];
    private boolean enabled = true;
    private ViewmodelConfig.Hand editHand = ViewmodelConfig.Hand.BOTH;

    public ViewmodelConfig.Hand editHand() {
        return this.editHand;
    }

    public void setEditHand(ViewmodelConfig.Hand var1) {
        this.editHand = var1;
    }

    public float get(int var1, ViewmodelSetting var2) {
        return this.values[var1][var2.ordinal()];
    }

    public void set(int var1, ViewmodelSetting var2, float var3) {
        this.values[var1][var2.ordinal()] = var2.sanitize(var3);
    }

    public float get(ViewmodelSetting var1) {
        return this.get(this.editHand == ViewmodelConfig.Hand.LEFT ? 1 : 0, var1);
    }

    public void set(ViewmodelSetting var1, float var2) {
        if (this.editHand != ViewmodelConfig.Hand.LEFT) {
            this.set(0, var1, var2);
        }

        if (this.editHand != ViewmodelConfig.Hand.RIGHT) {
            this.set(1, var1, var2);
        }
    }

    public boolean enabled() {
        return this.enabled;
    }

    public void setEnabled(boolean var1) {
        this.enabled = var1;
    }

    public void resetValues() {
        for (int var1 = 0; var1 < 2; var1++) {
            if (this.editHand == ViewmodelConfig.Hand.BOTH || this.editHand == (var1 == 0 ? ViewmodelConfig.Hand.RIGHT : ViewmodelConfig.Hand.LEFT)) {
                Arrays.fill(this.values[var1], 0.0F);
            }
        }
    }

    public void resetAll() {
        Arrays.fill(this.values[0], 0.0F);
        Arrays.fill(this.values[1], 0.0F);
    }

    public boolean isNeutral() {
        for (float[] var4 : this.values) {
            for (float var8 : var4) {
                if (var8 != 0.0F) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean active() {
        return this.enabled && !this.isNeutral();
    }

    public void copyFrom(ViewmodelConfig var1) {
        for (int var2 = 0; var2 < 2; var2++) {
            System.arraycopy(var1.values[var2], 0, this.values[var2], 0, this.values[var2].length);
        }

        this.enabled = var1.enabled;
    }

    @Override
    public String toString() {
        return "ViewmodelConfig{enabled=" + this.enabled + ", right=" + Arrays.toString(this.values[0]) + ", left=" + Arrays.toString(this.values[1]) + "}";
    }

    public enum Hand {
        BOTH,
        RIGHT,
        LEFT;

        public ViewmodelConfig.Hand next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }
}
