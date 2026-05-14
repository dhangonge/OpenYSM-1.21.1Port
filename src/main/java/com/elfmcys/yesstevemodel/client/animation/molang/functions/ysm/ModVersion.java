package com.elfmcys.yesstevemodel.client.animation.molang.functions.ysm;

import com.elfmcys.yesstevemodel.molang.runtime.ExecutionContext;
import com.elfmcys.yesstevemodel.molang.runtime.Function;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModVersion implements Function {
    @Override
    @Nullable
    public Object evaluate(@NotNull ExecutionContext<?> context, @NotNull Function.ArgumentCollection arguments) {
        String modid = arguments.getAsString(context, 0);
        if (modid == null) {
            return null;
        }
        return ModList.get().getModContainerById(modid).map(modContainer -> modContainer.getModInfo().getVersion().toString()).orElse(null);
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size == 1;
    }
}