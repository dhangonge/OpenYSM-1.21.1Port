package com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid;

import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapability;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapabilityProvider;
import com.elfmcys.yesstevemodel.client.gui.AnimationRouletteScreen;
import com.elfmcys.yesstevemodel.client.model.ModelAssembly;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class MaidAnimationRoulette {
    public static boolean canOpenRoulette() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return false;
        }
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (!(hitResult instanceof EntityHitResult)) {
            return false;
        }
        Entity entity = ((EntityHitResult) hitResult).getEntity();
        if (entity instanceof EntityMaid entityMaid) {
            if (!entityMaid.isYsmModel()) {
                return false;
            }
            return localPlayer.getUUID().equals(entityMaid.getOwnerUUID());
        }
        return false;
    }

    public static void openRouletteScreen() {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (!(hitResult instanceof EntityHitResult)) {
            return;
        }
        Entity entity = ((EntityHitResult) hitResult).getEntity();
        if (entity instanceof EntityMaid) {
            // 上游用 ifPresent 保护，移植版曾直接解引用：cap 为 null（TLM 兼容未注册）时 NPE 崩溃
            MaidCapability cap = entity.getCapability(MaidCapabilityProvider.MAID_CAP);
            if (cap == null) {
                return;
            }
            ModelAssembly modelAssembly = cap.getModelAssembly();
            if (modelAssembly != null && !modelAssembly.getModelData().getModelProperties().getExtraAnimation().isEmpty()) {
                if (Minecraft.getInstance().screen == null) {
                    if (com.elfmcys.yesstevemodel.config.GeneralConfig.effectiveModernRoulette()) {
                        Minecraft.getInstance().setScreen(new rip.ysm.gui.ModernAnimationRouletteScreen(cap.getModelId(), modelAssembly, cap));
                    } else {
                        Minecraft.getInstance().setScreen(new AnimationRouletteScreen(cap.getModelId(), modelAssembly, cap));
                    }
                } else if (Minecraft.getInstance().screen instanceof AnimationRouletteScreen || Minecraft.getInstance().screen instanceof rip.ysm.gui.ModernAnimationRouletteScreen) {
                    Minecraft.getInstance().setScreen(null);
                }
            }

        }
    }
}