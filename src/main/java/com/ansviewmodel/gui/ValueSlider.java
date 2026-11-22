package com.ansviewmodel.gui;

import com.ansviewmodel.config.ViewmodelConfig;
import com.ansviewmodel.config.ViewmodelSetting;
import java.util.Locale;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

final class ValueSlider extends AbstractSliderButton {
    private final ViewmodelSetting setting;
    private final ViewmodelConfig config;

    ValueSlider(int x, int y, int width, int height, ViewmodelSetting setting, ViewmodelConfig config) {
        super(x, y, width, height, Component.empty(), toSlider(setting, config.get(setting)));
        this.setting = setting;
        this.config = config;
        this.updateMessage();
        this.setTooltip(Tooltip.create(Component.translatable("ans_viewmodel.slider.tooltip", setting.bedrockId)));
    }

    private static double toSlider(ViewmodelSetting setting, float value) {
        return (double)(value - setting.min) / (setting.max - setting.min);
    }

    void refresh() {
        this.value = toSlider(this.setting, this.config.get(this.setting));
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.translatable(this.setting.translationKey(), format(this.setting, this.config.get(this.setting))));
    }

    @Override
    protected void applyValue() {
        float raw = (float)(this.setting.min + this.value * (this.setting.max - this.setting.min));
        this.config.set(this.setting, raw);
        this.value = toSlider(this.setting, this.config.get(this.setting));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.canChangeValue && (event.isLeft() || event.isRight())) {
            float direction = event.isLeft() ? -1.0F : 1.0F;
            this.config.set(this.setting, this.config.get(this.setting) + direction * this.setting.step);
            this.refresh();
            return true;
        } else {
            return super.keyPressed(event);
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        super.onClick(event, doubleClick);
        if (doubleClick) {
            this.config.set(this.setting, 0.0F);
            this.refresh();
        }
    }

    static String format(ViewmodelSetting setting, float value) {
        return switch (setting.section) {
            case POSITION -> String.format(Locale.ROOT, "%+d", Math.round(value));
            case ROTATION -> String.format(Locale.ROOT, "%+d°", Math.round(value));
            case SCALE -> String.format(Locale.ROOT, "%+.2f (×%.2f)", value, 1.0F + value);
        };
    }
}
