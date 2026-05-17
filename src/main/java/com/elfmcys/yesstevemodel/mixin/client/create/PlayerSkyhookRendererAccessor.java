package com.elfmcys.yesstevemodel.mixin.client.create;

import com.simibubi.create.foundation.render.PlayerSkyhookRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin({PlayerSkyhookRenderer.class})
public abstract class PlayerSkyhookRendererAccessor {

    @Shadow(remap = false)
    @Final
    private static Set<UUID> hangingPlayers;

    @Unique
    private static final HashSet<UUID> openYSM_1_21_1Port$hangingPlayersSet = new HashSet<>();


    @Accessor(value = "hangingPlayers",remap = false)
    public static Set<UUID> hangingPlayers() {
        if (hangingPlayers == null) {
            return openYSM_1_21_1Port$hangingPlayersSet;
        }
        return hangingPlayers;
    }
}