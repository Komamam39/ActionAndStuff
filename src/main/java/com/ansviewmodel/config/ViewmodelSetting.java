package com.ansviewmodel.config;

import java.util.Locale;

public enum ViewmodelSetting {
    OFFSET_X(ViewmodelSetting.Section.POSITION, ViewmodelSetting.Axis.X, "oreville_ans:offsetx", -50.0F, 50.0F, 1.0F),
    OFFSET_Y(ViewmodelSetting.Section.POSITION, ViewmodelSetting.Axis.Y, "oreville_ans:offsety", -50.0F, 50.0F, 1.0F),
    OFFSET_Z(ViewmodelSetting.Section.POSITION, ViewmodelSetting.Axis.Z, "oreville_ans:offsetz", -50.0F, 50.0F, 1.0F),
    SCALE_X(ViewmodelSetting.Section.SCALE, ViewmodelSetting.Axis.X, "oreville_ans:sclx", -1.0F, 1.0F, 0.01F),
    SCALE_Y(ViewmodelSetting.Section.SCALE, ViewmodelSetting.Axis.Y, "oreville_ans:scly", -1.0F, 1.0F, 0.01F),
    SCALE_Z(ViewmodelSetting.Section.SCALE, ViewmodelSetting.Axis.Z, "oreville_ans:sclz", -1.0F, 1.0F, 0.01F),
    ROTATION_X(ViewmodelSetting.Section.ROTATION, ViewmodelSetting.Axis.X, "oreville_ans:rotx", -50.0F, 50.0F, 1.0F),
    ROTATION_Y(ViewmodelSetting.Section.ROTATION, ViewmodelSetting.Axis.Y, "oreville_ans:roty", -50.0F, 50.0F, 1.0F),
    ROTATION_Z(ViewmodelSetting.Section.ROTATION, ViewmodelSetting.Axis.Z, "oreville_ans:rotz", -50.0F, 50.0F, 1.0F);

    public static final ViewmodelSetting[] VALUES = values();
    public final ViewmodelSetting.Section section;
    public final ViewmodelSetting.Axis axis;
    public final String bedrockId;
    public final float min;
    public final float max;
    public final float step;
    public final float defaultValue = 0.0F;

    ViewmodelSetting(ViewmodelSetting.Section section, ViewmodelSetting.Axis axis, String bedrockId, float min, float max, float step) {
        this.section = section;
        this.axis = axis;
        this.bedrockId = bedrockId;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public float sanitize(float value) {
        if (!Float.isFinite(value)) {
            return 0.0F;
        }

        float clamped = Math.clamp(value, this.min, this.max);
        float snapped = Math.round(clamped / this.step) * this.step;
        snapped = Math.round(snapped * 1000.0F) / 1000.0F;
        return snapped == 0.0F ? 0.0F : snapped;
    }

    public String translationKey() {
        return "ans_viewmodel.setting." + this.name().toLowerCase(Locale.ROOT);
    }

    public enum Axis {
        X,
        Y,
        Z;

        public final int index = this.ordinal();

        public String jsonKey() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public enum Section {
        POSITION("offset"),
        SCALE("scale"),
        ROTATION("rotation");

        public final String jsonKey;

        Section(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        public String translationKey() {
            return "ans_viewmodel.section." + this.name().toLowerCase(Locale.ROOT);
        }
    }
}
