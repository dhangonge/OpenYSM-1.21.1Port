package com.elfmcys.yesstevemodel.client.renderer;

import com.elfmcys.yesstevemodel.capability.PlayerCapability;
import com.elfmcys.yesstevemodel.capability.PlayerCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.VehicleCapabilityProvider;
import com.elfmcys.yesstevemodel.client.animation.molang.MolangWatchRegistry;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.ProjectileCapabilityProvider;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat;
import com.elfmcys.yesstevemodel.geckolib3.core.controller.IAnimationController;
import com.elfmcys.yesstevemodel.client.entity.GeoEntity;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.security.DrbgParameters;
import java.util.Objects;
import java.util.Optional;

public class AnimationDebugOverlay {

    private static final MolangWatchRegistry MOLANG_WATCH = new MolangWatchRegistry();
    private static final ReferenceArrayList<String> DEBUG_LINES = new ReferenceArrayList<>();
    private static WeakReference<GeoEntity<?>> activeModel = null;

    public static LayeredDraw.Layer createOverlay() {
        return (guiGraphics, partialTick) -> {
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            renderOverlay(guiGraphics, screenWidth, screenHeight);
        };
    }

    public static MolangWatchRegistry getMolangWatch() {
        return MOLANG_WATCH;
    }

    public static boolean isDebugActive() {
        return getActiveModel() != null;
    }

    public static boolean tryUpdateFromHitResult() {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof EntityHitResult) {
            return tryUpdateFromEntity(((EntityHitResult) hitResult).getEntity());
        }
        return tryUpdateFromLocalPlayer();
    }

    public static boolean tryUpdateFromLocalPlayer() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            PlayerCapability cap =localPlayer.getCapability(PlayerCapabilityProvider.PLAYER_CAP);
            if(cap!=null)
                setActiveModel(cap);
            return true;
        }
        clearActiveModel();
        return false;
    }

    public static boolean tryUpdateFromEntity(Entity entity) {
        Optional<?> capability;
        if (entity instanceof Player) {
            capability = Optional.ofNullable(entity.getCapability(PlayerCapabilityProvider.PLAYER_CAP));
        } else if (TouhouLittleMaidCompat.isMaidEntity(entity)) {
            capability = Optional.ofNullable(entity.getCapability(MaidCapabilityProvider.MAID_CAP));
        } else if (entity instanceof Projectile) {
            capability = Optional.ofNullable(entity.getCapability(ProjectileCapabilityProvider.PROJECTILE_CAP));
        } else {
            capability = Optional.ofNullable(entity.getCapability(VehicleCapabilityProvider.VEHICLE_CAP));
        }
        return capability.map(cap -> {
            setActiveModel((GeoEntity<?>) cap);
            return true;
        }).orElseGet(() -> {
            clearActiveModel();
            return false;
        });
    }

    public static void setActiveModel(GeoEntity<?> geoEntity) {
        clearActiveModel();
        activeModel = new WeakReference<>(geoEntity);
        geoEntity.setBoneLookup(MOLANG_WATCH);
        Entity entity = geoEntity.getEntity();
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            MutableComponent mutableComponentAppend = Component.translatable("message.yes_steve_model.model.debug_animation.true").append(" -> ");
            Component customName = entity.getCustomName();
            Objects.requireNonNull(entity);
            localPlayer.sendSystemMessage(mutableComponentAppend.append(Objects.requireNonNullElseGet(customName, entity::getDisplayName)));
        }
    }

    public static void clearActiveModel() {
        if (activeModel != null) {
            GeoEntity<?> geoEntity = activeModel.get();
            if (geoEntity != null) {
                geoEntity.setBoneLookup(null);
            }
            activeModel = null;
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null) {
                localPlayer.sendSystemMessage(Component.translatable("message.yes_steve_model.model.debug_animation.false"));
            }
        }
    }

    public static void addDebugLine(String str) {
        DEBUG_LINES.add(0, str);
    }

    public static void clearDebugLines() {
        DEBUG_LINES.clear();
    }

    @Nullable
    public static GeoEntity<?> getActiveModel() {
        if (activeModel != null) {
            GeoEntity<?> geoEntity = activeModel.get();
            if (geoEntity != null && geoEntity.isDebugMode()) {
                return geoEntity;
            }
            clearActiveModel();
            return null;
        }
        return null;
    }

    public static void renderOverlay(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        GeoEntity<?> geoEntity = getActiveModel();
        if (geoEntity == null) {
            return;
        }
        int[] currentY = {5};
        MOLANG_WATCH.forEachEntry((molangKey, molangValue) -> {
            renderDebugOverlay(guiGraphics, currentY, molangKey, molangValue, screenWidth, screenHeight);
        });
        DEBUG_LINES.forEach(str3 -> {
            IAnimationController controller = geoEntity.getAnimationData().getAnimationControllerByName(str3);
            renderDebugOverlay(guiGraphics, currentY, str3, controller != null ? controller.getCurrentAnimation() : "(N/A)", screenWidth, screenHeight);
        });
    }

    public static void renderDebugOverlay(GuiGraphics guiGraphics, int[] currentY, String key, String value, int screenWidth, int screenHeight) {
        Font font = Minecraft.getInstance().font;
        if ((currentY[0] - 5) % 20 == 0) {
            guiGraphics.fill(2, currentY[0] - 1, screenWidth, currentY[0] + 9, -1068478384);
        } else {
            guiGraphics.fill(2, currentY[0] - 1, screenWidth, currentY[0] + 9, -1068474288);
        }
        guiGraphics.drawString(font, key, 5, currentY[0], 16777215);
        guiGraphics.drawString(font, value, screenWidth / 2, currentY[0], 16777215);
        currentY[0] = currentY[0] + 10;
    }
}