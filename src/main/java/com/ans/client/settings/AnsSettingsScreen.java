package com.ans.client.settings;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class AnsSettingsScreen extends OptionsSubScreen {
    public AnsSettingsScreen(@Nullable Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("ans.settings.title"));
    }

    @Override
    protected void addOptions() {
        List<AnsSettings.Setting> all = AnsSettings.all();
        if (all.isEmpty()) {
            this.list.addHeader(Component.translatable("ans.settings.missing"));
        } else {
            List<AbstractWidget> toggles = new ArrayList<>();

            for (AnsSettings.Setting s : all) {
                String[] label = s.label();
                Tooltip tooltip = label[1].isEmpty() ? null : Tooltip.create(Component.literal(label[1]));
                if (s.toggle()) {
                    CycleButton<Boolean> b = CycleButton.onOffBuilder(Boolean.parseBoolean(s.value()))
                        .withTooltip(var1x -> tooltip)
                        .create(0, 0, 150, 20, Component.literal(label[0]), (var1x, v) -> AnsSettings.set(s, Boolean.toString(v), true));
                    toggles.add(b);
                } else {
                    String current = s.options().contains(s.value()) ? s.value() : s.defaultValue();
                    CycleButton<String> b = CycleButton.<String>builder(o -> Component.literal(s.optionLabel(o)), current)
                        .withValues(s.options())
                        .withTooltip(var1x -> tooltip)
                        .create(0, 0, 310, 20, Component.literal(label[0]), (var1x, v) -> AnsSettings.set(s, v, true));
                    this.list.addBig(b);
                }
            }

            this.list.addSmall(toggles);
        }
    }

    @Override
    protected void addFooter() {
        LinearLayout footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(Component.translatable("ans.settings.reset"), var1x -> {
            AnsSettings.resetAll();
            this.rebuildWidgets();
        }).width(150).build());
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, var1x -> this.onClose()).width(150).build());
    }

    void scrollTo(double amount) {
        if (this.list != null) {
            this.list.setScrollAmount(amount);
        }
    }

    @Override
    public void removed() {
        AnsSettings.save();
        super.removed();
    }
}
