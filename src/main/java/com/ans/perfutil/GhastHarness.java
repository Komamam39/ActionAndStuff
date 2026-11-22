package com.ans.perfutil;

public final class GhastHarness {
    public static final boolean ON = !"false".equals(System.getProperty("ans.ghastHarness", "true"));

    private GhastHarness() {
    }
}
