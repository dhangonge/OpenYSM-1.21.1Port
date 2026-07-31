package com.elfmcys.yesstevemodel.client.compat.ironsspellbooks;

import com.elfmcys.yesstevemodel.client.animation.molang.CtrlBinding;
import com.elfmcys.yesstevemodel.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.yesstevemodel.geckolib3.core.enums.PlayState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * STUB: libs/ 未提供 irons_spellbooks jar。
 */
public class SpellbookBinding {

    public static void registerBindings(CtrlBinding binding) {
    }

    @Nullable
    public static String extractSpellAnimationName(LivingEntity entity, @Nullable AnimationEvent<?> event) {
        return null;
    }

    public static PlayState determinePlayState(AnimationEvent<?> event, LivingEntity entity) {
        return PlayState.CONTINUE;
    }
}
