package com.ans.perf;

import com.ansviewmodel.gui.ViewmodelConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ViewmodelConfigScreen.class, remap = false)
public interface ViewmodelScreenAccessor {
    @Accessor("parent")
    Screen ans$parent();
}
