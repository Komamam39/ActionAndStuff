package com.ans.perfutil;

import com.geckolib.constant.dataticket.DataTicket;
import java.util.Locale;
import java.util.Set;
import net.minecraft.world.entity.Entity;

public final class CushionSeat {
    public static final DataTicket<Float> TICKET = DataTicket.create("ans_cushion_seat", Float.class);
    public static final float OFFSET = parse("ans.cushionSeat", 0.125F);
    public static final float FURNITURE = parse("ans.cushionFurniture", 0.0F);
    private static final Set<String> NAMES = Set.of(
        "couch",
        "couch corner",
        "couch left",
        "couch middle",
        "couch right",
        "cowch",
        "cowch corner",
        "cowch left",
        "cowch middle",
        "cowch right",
        "sofa",
        "sofa corner",
        "sofa left",
        "sofa middle",
        "sofa right",
        "couch middle legs",
        "cowch middle legs",
        "sofa middle legs",
        "acacia chair",
        "bamboo chair",
        "birch chair",
        "chair",
        "cherry chair",
        "crimson chair",
        "dark chair",
        "dark oak chair",
        "jungle chair",
        "mangrove chair",
        "oak chair",
        "pale chair",
        "pale oak chair",
        "poplar chair",
        "spruce chair",
        "warped chair",
        "acacia stool",
        "bamboo stool",
        "birch stool",
        "cherry stool",
        "crimson stool",
        "dark oak stool",
        "dark stool",
        "jungle stool",
        "mangrove stool",
        "oak stool",
        "pale oak stool",
        "pale stool",
        "poplar stool",
        "spruce stool",
        "stool",
        "warped stool"
    );

    private CushionSeat() {
    }

    public static boolean furniture(Entity var0) {
        if (var0 != null && var0.hasCustomName()) {
            String var1 = var0.getCustomName().getString().replaceAll("§.", "").toLowerCase(Locale.ROOT).trim();
            return NAMES.contains(var1) || NAMES.contains(var1.replace("chair", " chair").replace("stool", " stool").trim());
        } else {
            return false;
        }
    }

    private static float parse(String var0, float var1) {
        try {
            return Float.parseFloat(System.getProperty(var0, Float.toString(var1)));
        } catch (NumberFormatException var3) {
            return var1;
        }
    }
}
