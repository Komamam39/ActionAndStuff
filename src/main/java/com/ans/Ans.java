package com.ans;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Ans {
    public static final String MOD_ID = "ans";
    public static final Logger LOGGER = LoggerFactory.getLogger("Motion & Bits");

    private Ans() {
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("ans", path);
    }
}
