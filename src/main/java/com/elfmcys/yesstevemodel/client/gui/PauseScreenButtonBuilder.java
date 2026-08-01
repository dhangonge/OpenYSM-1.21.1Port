package com.elfmcys.yesstevemodel.client.gui;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.PlayerCapability;
import com.elfmcys.yesstevemodel.capability.PlayerCapabilityProvider;
import com.elfmcys.yesstevemodel.client.model.ModelAssembly;
import com.elfmcys.yesstevemodel.config.GeneralConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PauseScreenButtonBuilder {
    public static boolean isServerConnected() {
        return YesSteveModel.isOnAndroid();
    }

    @Nullable
    public static List<Button> createButtons(PauseScreen pauseScreen) {
        Minecraft minecraft = Minecraft.getInstance();
            Button buttonBuild = Button.builder(Component.translatable("gui.yes_steve_model.skin"), button -> {
                if (GeneralConfig.DISCLAIMER_SHOW.get()) {
                    minecraft.setScreen(new DisclaimerScreen());
                } else {
                    minecraft.setScreen(new PlayerModelScreen());
                }
            }).bounds((pauseScreen.width / 2) - 69, pauseScreen.height - 35, 138, 30).build();
            buttonBuild.setTooltip(Tooltip.create(Component.translatable("key.yes_steve_model.player_model.desc")));
            Button buttonBuild2 = Button.builder(Component.literal("🔧"), button2 -> {
                minecraft.setScreen(new ExtraPlayerRenderScreen());
            }).bounds((pauseScreen.width / 2) - 120, pauseScreen.height - 35, 50, 30).build();
            buttonBuild2.setTooltip(Tooltip.create(Component.translatable("key.yes_steve_model.open_extra_player_render.desc")));
            Button buttonBuild3 = Button.builder(Component.literal("😄"), button3 -> {
                if (minecraft.player != null) {
                    PlayerCapability cap = minecraft.player.getCapability(PlayerCapabilityProvider.PLAYER_CAP);
                    if (cap != null) {
                        String str = cap.getModelId();
                        ModelAssembly modelAssembly = cap.getModelAssembly();
                        if (modelAssembly != null && !modelAssembly.getModelData().getModelProperties().getExtraAnimation().isEmpty()) {
                            if (GeneralConfig.effectiveModernRoulette()) {
                                minecraft.setScreen(new rip.ysm.gui.ModernAnimationRouletteScreen(str, modelAssembly, cap));
                            } else {
                                minecraft.setScreen(new AnimationRouletteScreen(str, modelAssembly, cap));
                            }
                        }
                    }
                }
            }).bounds((pauseScreen.width / 2) + 69, pauseScreen.height - 35, 50, 30).build();
            buttonBuild3.setTooltip(Tooltip.create(Component.translatable("key.yes_steve_model.animation_roulette.desc")));
            Button buttonBuild4 = Button.builder(Component.translatable("gui.yes_steve_model.config"), button4 -> {
                minecraft.setScreen(new ExtraPlayerConfigScreen(null));
            }).bounds((pauseScreen.width / 2) + 119, pauseScreen.height - 35, 60, 30).build();
            buttonBuild4.setTooltip(Tooltip.create(Component.translatable("gui.yes_steve_model.config.desc")));
            return List.of(buttonBuild, buttonBuild2, buttonBuild3, buttonBuild4);
    }
}