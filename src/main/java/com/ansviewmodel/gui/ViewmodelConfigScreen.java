package com.ansviewmodel.gui;

import com.ansviewmodel.config.ConfigManager;
import com.ansviewmodel.config.ViewmodelConfig;
import com.ansviewmodel.config.ViewmodelSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

public final class ViewmodelConfigScreen extends Screen {
    private static final int PANEL_COLOR = -1072689128;
    private static final int PANEL_BORDER = 1358954495;
    private static final int TEXT_WHITE = -1;
    private static final int TEXT_HINT = -4671296;
    private static final ViewmodelSetting.Section[] SECTIONS = ViewmodelSetting.Section.values();
    private final @Nullable Screen parent;
    private final ViewmodelConfig config = ConfigManager.get();
    private final List<ValueSlider> sliders = new ArrayList<>();
    private final List<ViewmodelConfigScreen.Header> headers = new ArrayList<>();
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int titleY;

    public ViewmodelConfigScreen(@Nullable Screen var1) {
        super(Component.translatable("ans_viewmodel.title"));
        this.parent = var1;
    }

    @Override
    protected void init() {
        this.sliders.clear();
        this.headers.clear();
        byte var1 = 8;
        byte var2 = 8;
        int var3 = Math.min(160, (this.width - 2 * var1 - 2 * var2) / 3);
        if (var3 >= 112) {
            this.layoutColumns(var3, var2);
        } else {
            this.layoutSingleColumn(var1);
        }
    }

    private void layoutColumns(int var1, int var2) {
        byte var3 = 20;
        int var4 = var3 + 2;
        int var5 = 3 * var1 + 2 * var2;
        int var6 = (this.width - var5) / 2;
        byte var7 = 6;
        this.titleY = var7 + 6;
        int var8 = var7 + 22;
        int var9 = var8 + 12;

        for (int var10 = 0; var10 < SECTIONS.length; var10++) {
            int var11 = var6 + var10 * (var1 + var2);
            this.headers.add(new ViewmodelConfigScreen.Header(sectionTitle(SECTIONS[var10]), var11 + var1 / 2, var8));
            int var12 = 0;

            for (ViewmodelSetting var16 : ViewmodelSetting.VALUES) {
                if (var16.section == SECTIONS[var10]) {
                    this.addSlider(new ValueSlider(var11, var9 + var12++ * var4, var1, var3, var16, this.config));
                }
            }
        }

        int var17 = var9 + 3 * var4 + 4;
        int var18 = Math.min(120, (var5 - 2 * var2) / 3);
        int var19 = (this.width - (3 * var18 + 2 * var2)) / 2;
        this.addFooter(var19, var17, var18, var3, var2);
        this.setPanel(var6 - 6, var7, var5 + 12, var17 + 2 * var3 + 2 + 6 - var7);
    }

    private void layoutSingleColumn(int var1) {
        int var2 = Math.min(220, this.width - 2 * var1);
        int var3 = (this.width - var2) / 2;
        byte var4 = 4;
        int var5 = 16 + SECTIONS.length * 12 + 6 + 8 + 22;
        int var6 = Math.clamp((this.height - var4 - var5) / 11 - 2, 12, 20);
        int var7 = var6 + 2;
        this.titleY = var4 + 4;
        int var8 = var4 + 16;

        for (ViewmodelSetting.Section var12 : SECTIONS) {
            this.headers.add(new ViewmodelConfigScreen.Header(sectionTitle(var12), var3 + var2 / 2, var8 + 2));
            var8 += 12;

            for (ViewmodelSetting var16 : ViewmodelSetting.VALUES) {
                if (var16.section == var12) {
                    this.addSlider(new ValueSlider(var3, var8, var2, var6, var16, this.config));
                    var8 += var7;
                }
            }
        }

        var8 += 4;
        byte var18 = 4;
        int var19 = (var2 - 2 * var18) / 3;
        this.addFooter(var3, var8, var19, var6, var18);
        this.setPanel(var3 - 4, var4, var2 + 8, var8 + 2 * var6 + 2 + 4 - var4);
    }

    private void addSlider(ValueSlider var1) {
        this.sliders.add(this.addRenderableWidget(var1));
    }

    private void addFooter(int var1, int var2, int var3, int var4, int var5) {
        this.addRenderableWidget(
            CycleButton.<ViewmodelConfig.Hand>builder(
                    var0 -> Component.translatable("ans_viewmodel.hand." + var0.name().toLowerCase(Locale.ROOT)), this.config.editHand()
                )
                .withValues(ViewmodelConfig.Hand.values())
                .create(var1, var2, 3 * var3 + 2 * var5, var4, Component.translatable("ans_viewmodel.hand"), (var1x, var2x) -> {
                    this.config.setEditHand(var2x);
                    this.sliders.forEach(ValueSlider::refresh);
                })
        );
        var2 += var4 + 2;
        this.addRenderableWidget(
            CycleButton.onOffBuilder(this.config.enabled())
                .create(var1, var2, var3, var4, Component.translatable("ans_viewmodel.enabled"), (var1x, var2x) -> this.config.setEnabled(var2x))
        );
        this.addRenderableWidget(Button.builder(Component.translatable("ans_viewmodel.reset"), var1x -> {
            this.config.resetValues();
            this.sliders.forEach(ValueSlider::refresh);
        }).bounds(var1 + var3 + var5, var2, var3, var4).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, var1x -> this.onClose()).bounds(var1 + 2 * (var3 + var5), var2, var3, var4).build());
    }

    private void setPanel(int var1, int var2, int var3, int var4) {
        this.panelX = var1;
        this.panelY = var2;
        this.panelWidth = var3;
        this.panelHeight = var4;
    }

    private static Component sectionTitle(ViewmodelSetting.Section var0) {
        return Component.translatable(var0.translationKey()).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor var1, int var2, int var3, float var4) {
        if (this.minecraft.level == null) {
            super.extractBackground(var1, var2, var3, var4);
        }

        var1.fill(this.panelX, this.panelY, this.panelX + this.panelWidth, this.panelY + this.panelHeight, -1072689128);
        var1.outline(this.panelX, this.panelY, this.panelWidth, this.panelHeight, 1358954495);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor var1, int var2, int var3, float var4) {
        super.extractRenderState(var1, var2, var3, var4);
        var1.centeredText(this.font, this.title, this.width / 2, this.titleY, -1);

        for (ViewmodelConfigScreen.Header var6 : this.headers) {
            var1.centeredText(this.font, var6.text(), var6.centerX(), var6.y(), -1);
        }

        MutableComponent var7 = Component.translatable(this.minecraft.level == null ? "ans_viewmodel.hint.no_world" : "ans_viewmodel.hint");
        var1.centeredText(this.font, var7, this.width / 2, this.panelY + this.panelHeight + 4, -4671296);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    @Override
    public void removed() {
        ConfigManager.save();
    }

    private record Header(Component text, int centerX, int y) {
    }
}
