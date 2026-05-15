package com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid;

import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapability;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapabilityProvider;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.event.ClientDistChecker;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.event.MaidCapabilityEvent;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.event.MaidClientTickEvent;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.event.MaidScreenEvent;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntityRenderer;
import com.github.tartaricacid.touhoulittlemaid.item.ItemHakureiGohei;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
public class MaidEventHandler {

    private static MaidEntityRenderer maidRenderer;

    public static void init() {
        NeoForge.EVENT_BUS.register(new MaidScreenEvent());
        NeoForge.EVENT_BUS.register(new MaidCapabilityEvent());
        NeoForge.EVENT_BUS.register(new MaidClientTickEvent());
        NeoForge.EVENT_BUS.register(new ClientDistChecker());
    }

    public static void registerMaidRenderer() {
        EntityMaidRenderer.YSM_ENTITY_MAID_RENDERER = context -> {
            maidRenderer = new MaidEntityRenderer(context);
            return (IGeoEntityRenderer) maidRenderer;
        };
    }

    public static boolean isMaid(Entity entity) {
        return entity instanceof EntityMaid;
    }

    public static boolean isYsmModelMaid(Entity entity) {
        if (!(entity instanceof EntityMaid entityMaid)) {
            return false;
        }
        return entityMaid.getCapability(MaidCapabilityProvider.MAID_CAP) != null && entityMaid.isYsmModel();
    }

    public static boolean isChair(Entity entity) {
        return entity instanceof EntityChair;
    }

    public static boolean isSit(Entity entity) {
        return entity instanceof EntitySit;
    }

    public static String getChairModelId(Entity entity) {
        if (entity instanceof EntityChair) {
            return ((EntityChair) entity).getModelId();
        }
        return StringPool.EMPTY;
    }

    public static boolean isMaidFishing(LivingEntity livingEntity) {
        return (livingEntity instanceof EntityMaid) && ((EntityMaid) livingEntity).fishing != null;
    }

    public static void setExtraRenderFlag(LivingEntity livingEntity) {
        MaidCapability cap = livingEntity.getCapability(MaidCapabilityProvider.MAID_CAP);
        if (cap != null)
            cap.setExtraRenderFlag(true);

    }

    public static boolean isGohei(Item item) {
        return item instanceof ItemHakureiGohei;
    }

    public static MaidEntityRenderer getMaidRenderer() {
        return maidRenderer;
    }
}