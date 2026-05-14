package com.elfmcys.yesstevemodel.client.gui.button;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigCheckBoxForge extends Checkbox {

    private final ModConfigSpec.BooleanValue forgeConfigSpec;

    public ConfigCheckBoxForge(int x, int y, String str, ModConfigSpec.BooleanValue booleanValue) {
        super(x, y, 400, 20, Component.translatable("gui.yes_steve_model.config." + str), booleanValue.get().booleanValue());
        this.forgeConfigSpec = booleanValue;
    }

    public void onPress() {
        super.onPress();
        this.forgeConfigSpec.set(Boolean.valueOf(!this.forgeConfigSpec.get().booleanValue()));
    }
}