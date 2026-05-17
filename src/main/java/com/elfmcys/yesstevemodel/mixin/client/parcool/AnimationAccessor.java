package com.elfmcys.yesstevemodel.mixin.client.parcool;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.common.attachment.client.Animation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.lang.reflect.Field;

@Mixin({Animation.class})
public interface AnimationAccessor {
    @Accessor(value = "animator", remap = false)
    static Animator getAnimator(Animation animation) {
        try {
            Field animator = Animation.class.getDeclaredField("animator");
            animator.setAccessible(true);
            @SuppressWarnings("uncheked")
            Animator result = (Animator) animator.get(animation);
            return result;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    };
}