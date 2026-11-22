package com.ans.perfutil;

import com.ans.client.settings.AnsSettings;
import com.ansviewmodel.config.ConfigManager;
import com.ansviewmodel.config.ViewmodelConfig;
import com.ansviewmodel.config.ViewmodelSetting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public final class AnsHubScreen extends Screen {
    public static final boolean ENABLED = !"false".equalsIgnoreCase(System.getProperty("ans.newSettingsScreen", "true"));
    private static final Identifier ICONS = Identifier.fromNamespaceAndPath("ans_ui", "textures/gui/icons.png");
    private static final List<String> ICON_NAMES = List.of(
        "sliders",
        "eye",
        "frame",
        "play",
        "speaker",
        "hand",
        "sparkle",
        "leaf",
        "person",
        "smile",
        "shirt",
        "shield",
        "pickaxe",
        "arrow",
        "drop",
        "chick",
        "fish",
        "totem",
        "palette",
        "star",
        "note",
        "run",
        "reset",
        "back"
    );
    private static final int C_FRAME = -14803425;
    private static final int C_TITLEBAR = -1184275;
    private static final int C_TITLE_TEXT = -14803425;
    private static final int C_LEFT = -12895427;
    private static final int C_RIGHT = -12040117;
    private static final int C_DIVIDER = -13487564;
    private static final int C_CAT_SEL = -14277080;
    private static final int C_CAT_HOVER = -13487563;
    private static final int C_GREEN = -12810969;
    private static final int C_GREEN_HI = -11887565;
    private static final int C_GREEN_SH = -14856941;
    private static final int C_LIGHT = -3092012;
    private static final int C_LIGHT_HI = -1644567;
    private static final int C_LIGHT_SH = -10987430;
    private static final int C_PURPLE = -9552679;
    private static final int C_PURPLE_HI = -8433174;
    private static final int C_PURPLE_SH = -11918426;
    private static final int C_WHITE = -1;
    private static final int C_GREY_TEXT = -3947578;
    private static final int C_DARK_TEXT = -14803425;
    private final @Nullable Screen parent;
    private AnsHubScreen.Cat cat;
    private double scroll;
    private double scrollTarget;
    private double contentHeight;
    private int viewHeight;
    private final Set<String> expanded = new HashSet<>();
    private final List<AnsHubScreen.Hot> hots = new ArrayList<>();
    private AnsHubScreen.@Nullable Drag dragging;
    private boolean draggingViewmodel;
    private float alpha = 1.0F;
    private int mouseX;
    private int mouseY;
    private int clipY0;
    private int clipY1;

    public AnsHubScreen(@Nullable Screen var1, @Nullable String var2) {
        super(Component.translatable("ans.settings.title"));
        this.parent = var1;
        AnsHubScreen.Cat var3 = AnsHubScreen.Cat.GENERAL;
        if (var2 != null) {
            for (AnsHubScreen.Cat var7 : AnsHubScreen.Cat.values()) {
                if (var7.key().equals(var2)) {
                    var3 = var7;
                }
            }
        }

        this.cat = var3;
    }

    private static String catOf(AnsSettings.Setting var0) {
        String var1 = var0.readable();
        if (var1.startsWith("3d_arrows")) {
            return "visuals";
        }

        return switch (var1) {
            case "armor_visibility", "tools_3d", "axolotl_bucket_color", "classic_baby_chicken", "rain_particles", "totem_translucency" -> "visuals";
            case "enchant_look", "enchant_color" -> "outline";
            case "special_movement_animations" -> "animations";
            case "hotbar_sounds" -> "sounds";
            default -> "general";
        };
    }

    private static String iconOf(AnsSettings.Setting var0) {
        String var1 = var0.readable();
        if (var1.startsWith("3d_arrows")) {
            return "arrow";
        }

        return switch (var1) {
            case "experience" -> "sparkle";
            case "biome_variants" -> "leaf";
            case "morph" -> "person";
            case "expressions" -> "smile";
            case "costumes" -> "shirt";
            case "armor_visibility" -> "shield";
            case "tools_3d" -> "pickaxe";
            case "axolotl_bucket_color" -> "fish";
            case "classic_baby_chicken" -> "chick";
            case "rain_particles" -> "drop";
            case "totem_translucency" -> "totem";
            case "enchant_look" -> "star";
            case "enchant_color" -> "palette";
            case "special_movement_animations" -> "run";
            case "hotbar_sounds" -> "note";
            default -> "sparkle";
        };
    }

    private static MutableComponent tr(String var0) {
        return Component.translatable("ans_ui." + var0);
    }

    private int a(int var1) {
        if (this.alpha >= 0.999F) {
            return var1;
        }

        int var2 = Math.round((var1 >>> 24 & 0xFF) * this.alpha);
        return var2 << 24 | var1 & 16777215;
    }

    private void fill(GuiGraphicsExtractor var1, int var2, int var3, int var4, int var5, int var6) {
        var1.fill(var2, var3, var4, var5, this.a(var6));
    }

    private void icon(GuiGraphicsExtractor var1, String var2, int var3, int var4, int var5) {
        int var6 = ICON_NAMES.indexOf(var2);
        if (var6 >= 0) {
            var1.blit(RenderPipelines.GUI_TEXTURED, ICONS, var3, var4, var6 * 16.0F, 0.0F, 16, 16, 16 * ICON_NAMES.size(), 16, this.a(var5));
        }
    }

    private void text(GuiGraphicsExtractor var1, Component var2, int var3, int var4, int var5) {
        var1.text(this.font, var2, var3, var4, this.a(var5), false);
    }

    private void textScaled(GuiGraphicsExtractor var1, Component var2, int var3, int var4, int var5, float var6) {
        var1.pose().pushMatrix();
        var1.pose().translate(var3, var4);
        var1.pose().scale(var6, var6);
        var1.text(this.font, var2, 0, 0, this.a(var5), false);
        var1.pose().popMatrix();
    }

    private void centered(GuiGraphicsExtractor var1, Component var2, int var3, int var4, int var5) {
        var1.text(this.font, var2, var3 - this.font.width(var2) / 2, var4, this.a(var5), false);
    }

    private String fit(String var1, int var2) {
        if (this.font.width(var1) <= var2) {
            return var1;
        }

        String var3 = this.font.plainSubstrByWidth(var1, Math.max(0, var2 - this.font.width("...")));
        return var3 + "...";
    }

    private void button(GuiGraphicsExtractor var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
        this.fill(var1, var2, var3, var4, var5, -14803425);
        this.fill(var1, var2 + 1, var3 + 1, var4 - 1, var5 - 1, var8);
        this.fill(var1, var2 + 1, var3 + 1, var4 - 1, var5 - 3, var9 ? var7 : var6);
    }

    private boolean hover(int var1, int var2, int var3, int var4) {
        return this.dragging == null
            && this.mouseX >= var1
            && this.mouseX < var3
            && this.mouseY >= var2
            && this.mouseY < var4
            && (var4 <= this.clipY0 || var2 >= this.clipY1 || this.mouseY >= this.clipY0 && this.mouseY < this.clipY1);
    }

    private void hot(int var1, int var2, int var3, int var4, Runnable var5) {
        this.hots.add(new AnsHubScreen.Hot(var1, var2, var3, var4, var5, null));
    }

    private static void click() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor var1, int var2, int var3, float var4) {
        if (this.minecraft.level == null) {
            super.extractBackground(var1, var2, var3, var4);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor var1, int var2, int var3, float var4) {
        super.extractRenderState(var1, var2, var3, var4);
        this.mouseX = var2;
        this.mouseY = var3;
        this.hots.clear();
        float var5 = this.draggingViewmodel ? 0.18F : 1.0F;
        this.alpha = this.alpha + (var5 - this.alpha) * 0.35F;
        if (Math.abs(var5 - this.alpha) < 0.01F) {
            this.alpha = var5;
        }

        this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.5;
        if (Math.abs(this.scrollTarget - this.scroll) < 0.5) {
            this.scroll = this.scrollTarget;
        }

        int var6 = this.width;
        int var7 = this.height;
        boolean var8 = var6 >= 520 && var7 >= 300;
        byte var9 = 6;
        byte var10 = var9;
        byte var11 = var9;
        int var12 = var6 - var9;
        int var13 = var7 - var9;
        this.fill(var1, var10 - 2, var11 - 2, var12 + 2, var13 + 2, -14803425);
        int var14 = var8 ? 28 : 18;
        this.fill(var1, var10, var11, var12, var11 + var14, -1184275);
        MutableComponent var15 = Component.translatable("ans.settings.title").withStyle(ChatFormatting.BOLD);
        float var16 = var8 ? 2.0F : 1.0F;
        int var17 = Math.round(this.font.width(var15) * var16);
        this.textScaled(var1, var15, (var10 + var12) / 2 - var17 / 2, var11 + (var14 - Math.round(8.0F * var16)) / 2, -14803425, var16);
        int var18 = var11 + var14;
        this.fill(var1, var10, var18, var12, var18 + 2, -14803425);
        var18 += 2;
        int var19 = Mth.clamp((int)((var12 - var10) * 0.3), 112, 200);
        this.fill(var1, var10, var18, var10 + var19, var13, -12895427);
        this.fill(var1, var10 + var19, var18, var10 + var19 + 2, var13, -14803425);
        this.drawLeft(var1, var10, var18, var10 + var19, var13, var8, var2, var3);
        int var20 = var10 + var19 + 2;
        this.fill(var1, var20, var18, var12, var13, -12040117);
        this.drawRight(var1, var20, var18, var12, var13, var8);
    }

    private void drawLeft(GuiGraphicsExtractor var1, int var2, int var3, int var4, int var5, boolean var6, int var7, int var8) {
        byte var9 = 8;
        int var10 = var6 ? 24 : 20;
        int var11 = var6 ? 24 : 20;
        int var12 = AnsHubScreen.Cat.values().length * var10;
        int var13 = var5 - var3 - var9 * 3 - var11 - 8 - var12;
        int var14 = var4 - var2 - var9 * 2;
        int var15 = Math.min(Math.round(var14 * 0.56F), var13);
        int var16 = var3 + var9;
        if (var15 >= 40) {
            int var17 = var2 + var9;
            int var18 = var16;
            int var19 = var17 + var14;
            int var20 = var16 + var15;
            this.fill(var1, var17, var18, var19, var20, -14803425);
            var1.fillGradient(var17 + 2, var18 + 2, var19 - 2, var20 - 2, this.a(-8869408), this.a(-3152137));
            if (this.minecraft.player != null && this.alpha > 0.5F) {
                int var36 = Math.round(var15 * 0.36F);
                InventoryScreen.extractEntityInInventoryFollowsMouse(
                    var1, var17 + 2, var18 + 2, var19 - 2, var20 - 2, var36, 0.0625F, var7, var8, this.minecraft.player
                );
            } else {
                MutableComponent var21 = Component.literal("Motion & Bits").withStyle(ChatFormatting.BOLD);
                this.centered(var1, var21, (var17 + var19) / 2, (var18 + var20) / 2 - 4, -1);
            }

            var16 = var20 + 8;
        }

        int var32 = var2 + var9;
        int var33 = var4 - var9;
        int var34 = var16 + var11;
        boolean var35 = this.hover(var32, var16, var33, var34);
        this.button(var1, var32, var16, var33, var34, -9552679, -8433174, -11918426, var35);
        MutableComponent var37 = tr("done").withStyle(ChatFormatting.BOLD);
        if (var6) {
            int var22 = this.font.width(var37) * 2;
            this.textScaled(var1, var37, (var32 + var33) / 2 - var22 / 2, var16 + (var11 - 3 - 16) / 2 + 1, -1, 2.0F);
        } else {
            this.centered(var1, var37, (var32 + var33) / 2, var16 + (var11 - 3 - 8) / 2 + 1, -1);
        }

        this.hot(var32, var16, var33, var34, () -> {
            click();
            this.onClose();
        });
        var16 = var34 + 8;

        for (AnsHubScreen.Cat var25 : AnsHubScreen.Cat.values()) {
            int var26 = var16;
            int var27 = var16 + var10;
            boolean var28 = var25 == this.cat;
            boolean var29 = this.hover(var2, var26, var4, var27);
            if (var28) {
                this.fill(var1, var2, var26, var4 - 4, var27, -14277080);
                this.fill(var1, var2, var26, var2 + 3, var27, -12810969);
            } else if (var29) {
                this.fill(var1, var2, var26, var4 - 4, var27, -13487563);
            }

            this.icon(var1, var25.icon, var2 + var9 + 2, var26 + (var10 - 16) / 2, -1);
            String var30 = this.fit(tr("cat." + var25.key()).getString(), var4 - var2 - var9 - 30);
            this.text(var1, Component.literal(var30), var2 + var9 + 24, var26 + (var10 - 8) / 2, -1);
            this.hot(var2, var26, var4, var27, () -> {
                if (this.cat != var25) {
                    click();
                    this.cat = var25;
                    this.scroll = this.scrollTarget = 0.0;
                }
            });
            var16 = var27;
        }
    }

    private void drawRight(GuiGraphicsExtractor var1, int var2, int var3, int var4, int var5, boolean var6) {
        byte var7 = 10;
        MutableComponent var8 = Component.literal(tr("cat." + this.cat.key()).getString().toUpperCase(Locale.ROOT)).withStyle(ChatFormatting.BOLD);
        float var9 = var6 ? 2.0F : 1.0F;
        int var10 = var3 + 8;
        this.textScaled(var1, var8, var2 + var7, var10, -1, var9);
        var10 += Math.round(9.0F * var9) + 3;

        for (FormattedCharSequence var12 : this.font.split(tr("cat." + this.cat.key() + ".desc"), var4 - var2 - var7 * 2 - 70)) {
            var1.text(this.font, var12, var2 + var7, var10, this.a(-3947578), false);
            var10 += 10;
        }

        MutableComponent var34 = tr("reset");
        int var35 = Math.max(54, this.font.width(var34) + 26);
        int var13 = var4 - var7;
        int var14 = var13 - var35;
        int var15 = var3 + 8;
        int var16 = var15 + 18;
        boolean var17 = this.hover(var14, var15, var13, var16);
        this.button(var1, var14, var15, var13, var16, -3092012, -1644567, -10987430, var17);
        this.icon(var1, "reset", var14 + 4, var15 + 1, -14803425);
        this.text(var1, var34, var14 + 20, var15 + 5, -14803425);
        this.hot(var14, var15, var13, var16, () -> {
            click();
            this.resetCategory();
        });
        int var18 = Math.max(var10 + 4, var16 + 6);
        this.fill(var1, var2, var18, var4, var18 + 2, -13487564);
        int var19 = var18 + 2;
        int var20 = var5;
        this.clipY0 = var19;
        this.clipY1 = var20;
        this.viewHeight = var20 - var19;
        byte var21 = 6;
        int var22 = var2;
        int var23 = var4 - var21 - 2;
        var1.enableScissor(var2, var19, var4, var20);
        int var24 = this.hots.size();
        int var25 = var19 - (int)Math.round(this.scroll);
        int var26 = var25;
        if (this.cat == AnsHubScreen.Cat.VIEWMODEL) {
            var25 = this.drawViewmodel(var1, var22, var25, var23, var6);
        } else {
            ArrayList<AnsSettings.Setting> var27 = new ArrayList<>();

            for (AnsSettings.Setting var29 : AnsSettings.all()) {
                if (catOf(var29).equals(this.cat.key())) {
                    var27.add(var29);
                }
            }

            if (var27.isEmpty()) {
                this.text(var1, Component.translatable("ans.settings.missing"), var22 + var7, var25 + 10, -3947578);
                var25 += 30;
            }

            for (AnsSettings.Setting var40 : var27) {
                var25 = this.drawSetting(var1, var40, var22, var25, var23, var6);
            }
        }

        var1.disableScissor();

        for (int var36 = this.hots.size() - 1; var36 >= var24; var36--) {
            AnsHubScreen.Hot var39 = this.hots.get(var36);
            if (var39.y1 <= var19 || var39.y0 >= var20) {
                this.hots.remove(var36);
            } else if (var39.y0 < var19 || var39.y1 > var20) {
                this.hots.set(var36, new AnsHubScreen.Hot(var39.x0, Math.max(var39.y0, var19), var39.x1, Math.min(var39.y1, var20), var39.click, var39.drag));
            }
        }

        this.contentHeight = var25 - var26 + 8;
        double var37 = Math.max(0.0, this.contentHeight - this.viewHeight);
        if (this.scrollTarget > var37) {
            this.scrollTarget = var37;
        }

        if (this.scroll > var37) {
            this.scroll = var37;
        }

        if (var37 > 0.0) {
            int var41 = var4 - var21 - 2;
            int var30 = var4 - 2;
            this.fill(var1, var41, var19 + 2, var30, var20 - 2, -13750736);
            int var31 = Math.max(16, (int)((var20 - var19 - 4) * (this.viewHeight / this.contentHeight)));
            int var32 = var19 + 2 + (int)((var20 - var19 - 4 - var31) * (this.scroll / var37));
            this.fill(var1, var41, var32, var30, var32 + var31, -7631986);
        }
    }

    private int cardHeader(GuiGraphicsExtractor var1, String var2, String var3, String var4, int var5, int var6, int var7, int var8) {
        byte var9 = 10;
        this.icon(var1, var2, var5 + var9, var6 + 10, -1);
        int var10 = var5 + var9 + 26;
        int var11 = var7 - var10 - var9 - var8;
        this.text(var1, Component.literal(this.fit(var3, var11)), var10, var6 + 10, -1);
        int var12 = var6 + 22;
        if (!var4.isEmpty()) {
            for (FormattedCharSequence var14 : this.font.split(Component.literal(var4), var7 - var10 - var9 - var8)) {
                var1.text(this.font, var14, var10, var12, this.a(-3947578), false);
                var12 += 10;
            }
        }

        return var12 + 4;
    }

    private int divider(GuiGraphicsExtractor var1, int var2, int var3, int var4) {
        this.fill(var1, var2 + 6, var3, var4 - 6, var3 + 1, -13487564);
        return var3 + 1;
    }

    private int drawSetting(GuiGraphicsExtractor var1, AnsSettings.Setting var2, int var3, int var4, int var5, boolean var6) {
        String[] var7 = var2.label();
        byte var8 = 10;
        if (var2.toggle()) {
            boolean var39 = Boolean.parseBoolean(var2.value());
            int var42 = this.cardHeader(var1, iconOf(var2), var7[0], var7[1], var3, var4, var5, 48);
            byte var44 = 34;
            byte var46 = 16;
            int var48 = var5 - var8;
            int var50 = var48 - var44;
            int var52 = var4 + 9;
            int var54 = var52 + var46;
            this.toggleSwitch(var1, var50, var52, var48, var54, var39);
            this.hot(var3, var4, var5, Math.max(var42, var54 + 4), () -> {
                click();
                AnsSettings.set(var2, Boolean.toString(!var39), true);
            });
            var4 = Math.max(var42, var54 + 6) + 4;
            return this.divider(var1, var3, var4, var5);
        }

        if (MultiSelect.isMulti(var2.id())) {
            int var38 = this.cardHeader(var1, iconOf(var2), var7[0], var7[1], var3, var4, var5, 0);
            Set var41 = MultiSelect.get(var2.id());
            int var43 = var3 + var8 + 26;

            for (String var47 : var2.options()) {
                boolean var49 = var41.contains(var47);
                int var51 = var38;
                int var53 = var38 + 16;
                boolean var56 = this.hover(var43 - 4, var51, var5 - var8, var53);
                if (var56) {
                    this.fill(var1, var43 - 4, var51, var5 - var8, var53, -13487563);
                }

                this.checkbox(var1, var43, var51 + 2, var49);
                this.text(var1, Component.literal(var2.optionLabel(var47)), var43 + 18, var51 + 4, -1);
                this.hot(var43 - 4, var51, var5 - var8, var53, () -> {
                    click();
                    LinkedHashSet var2x = new LinkedHashSet<>(MultiSelect.get(var2.id()));
                    if (!var2x.remove(var47)) {
                        var2x.add(var47);
                    }

                    AnsSettings.set(var2, MultiSelect.encode(var2x), true);
                });
                var38 = var53 + 1;
            }

            var4 = var38 + 8;
            return this.divider(var1, var3, var4, var5);
        } else {
            String var9 = var2.options().contains(var2.value()) ? var2.value() : var2.defaultValue();
            int var10 = this.cardHeader(var1, iconOf(var2), var7[0], var7[1], var3, var4, var5, 0);
            int var11 = var3 + var8;
            int var12 = var5 - var8;
            int var13 = var6 ? 22 : 20;
            List<String> var14 = var2.options();
            int var15 = var14.size();
            byte var16 = 4;
            boolean var17 = false;
            if (var15 <= 3) {
                int var18 = (var12 - var11 - var16 * (var15 - 1)) / var15;
                var17 = true;

                for (String var20 : var14) {
                    if (this.font.width(var2.optionLabel(var20)) + 12 > var18) {
                        var17 = false;
                    }
                }

                if (var17) {
                    for (int var58 = 0; var58 < var15; var58++) {
                        String var61 = (String)var14.get(var58);
                        int var21 = var11 + var58 * (var18 + var16);
                        int var22 = var58 == var15 - 1 ? var12 : var21 + var18;
                        this.choice(var1, var2, var61, var61.equals(var9), var21, var10, var22, var10 + var13);
                    }

                    var10 += var13 + 4;
                } else {
                    for (String var62 : var14) {
                        this.choice(var1, var2, var62, var62.equals(var9), var11, var10, var12, var10 + var13);
                        var10 += var13 + 3;
                    }

                    var10++;
                }
            } else {
                boolean var57 = this.expanded.contains(var2.id());
                int var60 = var13;
                int var63 = Math.max(0, var14.indexOf(var9));
                boolean var64 = this.hover(var11, var10, var11 + var60, var10 + var13);
                this.button(var1, var11, var10, var11 + var60, var10 + var13, -3092012, -1644567, -10987430, var64);
                this.centered(var1, Component.literal("<"), var11 + var60 / 2, var10 + (var13 - 11) / 2 + 1, -14803425);
                this.hot(var11, var10, var11 + var60, var10 + var13, () -> {
                    click();
                    AnsSettings.set(var2, (String)var14.get((var63 - 1 + var15) % var15), true);
                });
                boolean var65 = this.hover(var12 - var60, var10, var12, var10 + var13);
                this.button(var1, var12 - var60, var10, var12, var10 + var13, -3092012, -1644567, -10987430, var65);
                this.centered(var1, Component.literal(">"), var12 - var60 / 2, var10 + (var13 - 11) / 2 + 1, -14803425);
                this.hot(var12 - var60, var10, var12, var10 + var13, () -> {
                    click();
                    AnsSettings.set(var2, (String)var14.get((var63 + 1) % var15), true);
                });
                int var23 = var11 + var60 + var16;
                int var24 = var12 - var60 - var16;
                boolean var25 = this.hover(var23, var10, var24, var10 + var13);
                this.button(var1, var23, var10, var24, var10 + var13, -12810969, -11887565, -14856941, var25);
                String var26 = this.fit(var2.optionLabel(var9), var24 - var23 - 22);
                this.centered(var1, Component.literal(var26), (var23 + var24) / 2, var10 + (var13 - 11) / 2 + 1, -1);
                this.text(var1, Component.literal(var57 ? "▲" : "▼"), var24 - 12, var10 + (var13 - 11) / 2 + 1, -1);
                this.hot(var23, var10, var24, var10 + var13, () -> {
                    click();
                    if (!this.expanded.remove(var2.id())) {
                        this.expanded.add(var2.id());
                    }
                });
                var10 += var13 + 4;
                if (var57) {
                    int var27 = var12 - var11 >= 300 ? 2 : 1;
                    int var28 = (var12 - var11 - var16 * (var27 - 1)) / var27;

                    for (int var29 = 0; var29 < var15; var29++) {
                        String var30 = (String)var14.get(var29);
                        int var31 = var29 % var27;
                        int var32 = var29 / var27;
                        int var33 = var11 + var31 * (var28 + var16);
                        int var34 = var10 + var32 * (var13 + 3);
                        this.choice(var1, var2, var30, var30.equals(var9), var33, var34, var31 == var27 - 1 ? var12 : var33 + var28, var34 + var13);
                    }

                    var10 += (var15 + var27 - 1) / var27 * (var13 + 3) + 1;
                }
            }

            var4 = var10 + 6;
            return this.divider(var1, var3, var4, var5);
        }
    }

    private void choice(GuiGraphicsExtractor var1, AnsSettings.Setting var2, String var3, boolean var4, int var5, int var6, int var7, int var8) {
        boolean var9 = this.hover(var5, var6, var7, var8);
        if (var4) {
            this.button(var1, var5, var6, var7, var8, -12810969, -11887565, -14856941, var9);
        } else {
            this.button(var1, var5, var6, var7, var8, -3092012, -1644567, -10987430, var9);
        }

        String var10 = this.fit(var2.optionLabel(var3), var7 - var5 - 8);
        int var11 = var6 + (var8 - var6 - 11) / 2 + 1;
        this.centered(var1, Component.literal(var10), (var5 + var7) / 2, var11, var4 ? -1 : -14803425);
        if (var4) {
            int var12 = (var5 + var7) / 2;
            this.fill(var1, var12 - 12, var8 - 5, var12 + 12, var8 - 4, -1);
        }

        this.hot(var5, var6, var7, var8, () -> {
            if (!var4) {
                click();
                AnsSettings.set(var2, var3, true);
            }
        });
    }

    private void toggleSwitch(GuiGraphicsExtractor var1, int var2, int var3, int var4, int var5, boolean var6) {
        this.fill(var1, var2, var3, var4, var5, -14803425);
        this.fill(var1, var2 + 1, var3 + 1, var4 - 1, var5 - 1, var6 ? -12810969 : -7631986);
        int var7 = var5 - var3 - 2;
        int var8 = var6 ? var4 - 1 - var7 : var2 + 1;
        this.fill(var1, var8, var3 + 1, var8 + var7, var5 - 1, -14803425);
        this.fill(var1, var8 + 1, var3 + 2, var8 + var7 - 1, var5 - 2, -3092012);
        if (var6) {
            this.fill(var1, var2 + 5, var3 + 5, var2 + 6, var5 - 5, -1);
        } else {
            this.fill(var1, var4 - 9, var3 + 5, var4 - 5, var3 + 6, -14803425);
        }
    }

    private void checkbox(GuiGraphicsExtractor var1, int var2, int var3, boolean var4) {
        this.fill(var1, var2, var3, var2 + 12, var3 + 12, -14803425);
        this.fill(var1, var2 + 1, var3 + 1, var2 + 11, var3 + 11, var4 ? -12810969 : -3092012);
        if (var4) {
            byte var5 = -1;
            this.fill(var1, var2 + 3, var3 + 6, var2 + 5, var3 + 8, var5);
            this.fill(var1, var2 + 5, var3 + 7, var2 + 6, var3 + 9, var5);
            this.fill(var1, var2 + 6, var3 + 5, var2 + 7, var3 + 8, var5);
            this.fill(var1, var2 + 7, var3 + 4, var2 + 8, var3 + 6, var5);
            this.fill(var1, var2 + 8, var3 + 3, var2 + 9, var3 + 5, var5);
        }
    }

    private int drawViewmodel(GuiGraphicsExtractor var1, int var2, int var3, int var4, boolean var5) {
        ViewmodelConfig var6 = ConfigManager.get();
        byte var7 = 10;
        boolean var8 = var6.enabled();
        int var9 = this.cardHeader(var1, "hand", tr("vm.enabled").getString(), tr("vm.enabled.desc").getString(), var2, var3, var4, 48);
        int var10 = var4 - var7;
        int var11 = var10 - 34;
        int var12 = var3 + 9;
        int var13 = var12 + 16;
        this.toggleSwitch(var1, var11, var12, var10, var13, var8);
        this.hot(var2, var3, var4, Math.max(var9, var13 + 4), () -> {
            click();
            var6.setEnabled(!var8);
            ConfigManager.save();
        });
        var3 = this.divider(var1, var2, Math.max(var9, var13 + 6) + 4, var4);
        int var8i = this.cardHeader(var1, "hand", tr("vm.hand").getString(), tr("vm.hand.desc").getString(), var2, var3, var4, 0);
        ViewmodelConfig.Hand[] var26 = ViewmodelConfig.Hand.values();
        var10 = var2 + var7;
        var11 = var4 - var7;
        var12 = var5 ? 22 : 20;
        byte var38 = 4;
        int var14 = (var11 - var10 - var38 * (var26.length - 1)) / var26.length;

        for (int var15 = 0; var15 < var26.length; var15++) {
            ViewmodelConfig.Hand var16 = var26[var15];
            boolean var17 = var6.editHand() == var16;
            int var18 = var10 + var15 * (var14 + var38);
            int var19 = var15 == var26.length - 1 ? var11 : var18 + var14;
            boolean var20 = this.hover(var18, var8i, var19, var8i + var12);
            if (var17) {
                this.button(var1, var18, var8i, var19, var8i + var12, -12810969, -11887565, -14856941, var20);
            } else {
                this.button(var1, var18, var8i, var19, var8i + var12, -3092012, -1644567, -10987430, var20);
            }

            this.centered(
                var1, tr("vm.hand." + var16.name().toLowerCase(Locale.ROOT)), (var18 + var19) / 2, var8i + (var12 - 11) / 2 + 1, var17 ? -1 : -14803425
            );
            if (var17) {
                this.fill(var1, (var18 + var19) / 2 - 12, var8i + var12 - 5, (var18 + var19) / 2 + 12, var8i + var12 - 4, -1);
            }

            this.hot(var18, var8i, var19, var8i + var12, () -> {
                click();
                var6.setEditHand(var16);
            });
        }

        var3 = this.divider(var1, var2, var8i + var12 + 10, var4);

        for (ViewmodelSetting.Section var33 : ViewmodelSetting.Section.values()) {
            String var36 = var33.name().toLowerCase(Locale.ROOT);

            String var39 = switch (var33) {
                case POSITION -> "sliders";
                case ROTATION -> "reset";
                case SCALE -> "frame";
            };
            var14 = this.cardHeader(var1, var39, tr("vm." + var36).getString(), tr("vm." + var36 + ".desc").getString(), var2, var3, var4, 0);

            for (ViewmodelSetting var44 : ViewmodelSetting.VALUES) {
                if (var44.section == var33) {
                    var14 = this.slider(
                        var1,
                        var44.axis.name(),
                        var2 + var7,
                        var14,
                        var4 - var7,
                        var44.min,
                        var44.max,
                        var44.step,
                        () -> var6.get(var44),
                        var2x -> var6.set(var44, (float)var2x),
                        () -> var6.set(var44, 0.0F)
                    );
                }
            }

            var3 = this.divider(var1, var2, var14 + 6, var4);
        }

        var8i = var2 + var7;
        MutableComponent var28 = tr(this.minecraft.level == null ? "vm.hint.no_world" : "vm.hint");
        var10 = var3 + 8;

        for (FormattedCharSequence var37 : this.font.split(var28, var4 - var2 - var7 * 2)) {
            var1.text(this.font, var37, var8i, var10, this.a(-3947578), false);
            var10 += 10;
        }

        return var10 + 6;
    }

    private int slider(
        GuiGraphicsExtractor var1,
        String var2,
        int var3,
        int var4,
        int var5,
        double var6,
        double var8,
        double var10,
        DoubleSupplier var12,
        DoubleConsumer var13,
        Runnable var14
    ) {
        byte var15 = 18;
        byte var16 = 16;
        byte var17 = 40;
        int var18 = var3 + 26 + var16;
        int var19 = var5 - var17 - 6;
        double var20 = var12.getAsDouble();
        this.text(var1, Component.literal(var2).withStyle(ChatFormatting.BOLD), var3 + 26, var4 + 5, -1);
        int var22 = var4 + var15 / 2;
        this.fill(var1, var18, var22 - 2, var19, var22 + 2, -14803425);
        int var23 = var18 + (int)Math.round((0.0 - var6) / (var8 - var6) * (var19 - var18));
        int var24 = var18 + (int)Math.round((var20 - var6) / (var8 - var6) * (var19 - var18));
        this.fill(var1, Math.min(var23, var24), var22 - 1, Math.max(var23, var24), var22 + 1, -12810969);
        this.fill(var1, var23, var22 - 4, var23 + 1, var22 + 4, -7631986);
        boolean var25 = this.hover(var18 - 4, var4, var19 + 4, var4 + var15)
            || this.dragging != null && this.dragging.trackX0() == var18 && this.dragging.trackX1() == var19;
        int var26 = var24 - 3;
        int var27 = var24 + 4;
        this.fill(var1, var26, var22 - 7, var27, var22 + 7, -14803425);
        this.fill(var1, var26 + 1, var22 - 6, var27 - 1, var22 + 6, var25 ? -1644567 : -3092012);
        String var28 = var10 >= 1.0 ? Integer.toString((int)Math.round(var20)) : String.format(Locale.ROOT, "%.2f", var20);
        this.text(var1, Component.literal(var28), var5 - var17 + (var17 - this.font.width(var28)), var4 + 5, -1);
        AnsHubScreen.Drag var29 = new AnsHubScreen.Drag(var18, var19, var6, var8, var13, var14);
        this.hots.add(new AnsHubScreen.Hot(var18 - 4, var4, var19 + 4, var4 + var15, null, var29));
        return var4 + var15 + 2;
    }

    private void resetCategory() {
        if (this.cat == AnsHubScreen.Cat.VIEWMODEL) {
            ConfigManager.get().resetValues();
            ConfigManager.save();
        } else {
            for (AnsSettings.Setting var2 : AnsSettings.all()) {
                if (catOf(var2).equals(this.cat.key())) {
                    AnsSettings.set(var2, var2.defaultValue(), false);
                }
            }

            AnsSettings.save();
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
        double var3 = var1.x();
        double var5 = var1.y();

        for (int var7 = this.hots.size() - 1; var7 >= 0; var7--) {
            AnsHubScreen.Hot var8 = this.hots.get(var7);
            if (var8.contains(var3, var5)) {
                if (var8.drag != null) {
                    if (var2) {
                        var8.drag.reset().run();
                        ConfigManager.save();
                        return true;
                    }

                    this.dragging = var8.drag;
                    this.draggingViewmodel = this.cat == AnsHubScreen.Cat.VIEWMODEL && this.minecraft.level != null;
                    this.applyDrag(var3);
                    return true;
                }

                if (var8.click != null) {
                    var8.click.run();
                    return true;
                }
            }
        }

        return super.mouseClicked(var1, var2);
    }

    private void applyDrag(double var1) {
        AnsHubScreen.Drag var3 = this.dragging;
        if (var3 != null) {
            double var4 = Mth.clamp((var1 - var3.trackX0()) / Math.max(1, var3.trackX1() - var3.trackX0()), 0.0, 1.0);
            var3.set().accept(var3.min() + var4 * (var3.max() - var3.min()));
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent var1, double var2, double var4) {
        if (this.dragging != null) {
            this.applyDrag(var1.x());
            return true;
        } else {
            return super.mouseDragged(var1, var2, var4);
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent var1) {
        if (this.dragging != null) {
            this.dragging = null;
            this.draggingViewmodel = false;
            ConfigManager.save();
            return true;
        } else {
            return super.mouseReleased(var1);
        }
    }

    @Override
    public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
        double var9 = Math.max(0.0, this.contentHeight - this.viewHeight);
        this.scrollTarget = Mth.clamp(this.scrollTarget - var7 * 28.0, 0.0, var9);
        return true;
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
        AnsSettings.save();
        ConfigManager.save();
        super.removed();
    }

    enum Cat {
        GENERAL("sliders"),
        VISUALS("eye"),
        OUTLINE("frame"),
        ANIMATIONS("play"),
        SOUNDS("speaker"),
        VIEWMODEL("hand");

        final String icon;

        Cat(String nullxx) {
            this.icon = nullxx;
        }

        String key() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    private record Drag(int trackX0, int trackX1, double min, double max, DoubleConsumer set, Runnable reset) {
    }

    private record Hot(int x0, int y0, int x1, int y1, @Nullable Runnable click, AnsHubScreen.@Nullable Drag drag) {
        boolean contains(double var1, double var3) {
            return var1 >= this.x0 && var1 < this.x1 && var3 >= this.y0 && var3 < this.y1;
        }
    }
}
