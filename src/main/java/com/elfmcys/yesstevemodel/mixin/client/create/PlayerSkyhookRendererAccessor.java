package com.elfmcys.yesstevemodel.mixin.client.create;

import com.simibubi.create.foundation.render.PlayerSkyhookRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

@Mixin({PlayerSkyhookRenderer.class})
public interface PlayerSkyhookRendererAccessor {

    @Accessor(value = "hangingPlayers",remap = false)
    static Set<UUID> hangingPlayers() {
        try{
            Field hangingPlayers = PlayerSkyhookRenderer.class.getDeclaredField("hangingPlayers");
            hangingPlayers.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<UUID> set = (Set<UUID>) hangingPlayers.get(null);
            return set;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}