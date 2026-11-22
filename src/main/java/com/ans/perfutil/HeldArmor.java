package com.ans.perfutil;

import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.molang.MolangScope;
import com.ans.client.molang.MolangVars;

public final class HeldArmor {
    private static final String VAR = System.getProperty("ans.heldArmorVar", "apeeop");
    private static int id = -1;

    private HeldArmor() {
    }

    public static boolean active(EntityRuntime var0) {
        if (var0 == null) {
            return false;
        }

        if (id < 0) {
            id = MolangVars.id(VAR);
        }

        MolangScope var1 = var0.variables();
        return var1.isDefined(id) && var1.getNum(id) != 0.0;
    }
}
