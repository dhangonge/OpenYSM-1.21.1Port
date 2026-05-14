package com.elfmcys.yesstevemodel.capability;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public final class ProjectileCapabilityProvider implements ICapabilityProvider<Entity, Void, ProjectileCapability> {

    public static final EntityCapability<ProjectileCapability, Void> PROJECTILE_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "projectile_animatable"), ProjectileCapability.class);

    public static final ProjectileCapabilityProvider INSTANCE = new ProjectileCapabilityProvider();

    private final ConcurrentHashMap<UUID, ProjectileCapability> cache = new ConcurrentHashMap<>();

    private ProjectileCapabilityProvider() {}

    @Override
    @Nullable
    public ProjectileCapability getCapability(Entity entity, Void context) {
        if (entity instanceof Projectile projectile) {
            return cache.computeIfAbsent(entity.getUUID(), uuid -> new ProjectileCapability(projectile));
        }
        return null;
    }

    public void invalidate(UUID uuid) {
        cache.remove(uuid);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = YesSteveModel.MOD_ID)
    private static class CleanupHandler {
        @SubscribeEvent
        public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
            INSTANCE.invalidate(event.getEntity().getUUID());
        }
    }
}
