package com.elfmcys.yesstevemodel.client.gui;

import com.elfmcys.yesstevemodel.client.gui.button.ConfigCheckBoxForge;
import com.elfmcys.yesstevemodel.client.gui.button.FlatColorButton;
import com.elfmcys.yesstevemodel.client.gui.button.LoadingStateButton;
import com.elfmcys.yesstevemodel.config.GeneralConfig;
import com.elfmcys.yesstevemodel.config.ExtraPlayerRenderConfig;
import com.elfmcys.yesstevemodel.config.LoadingStateConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ExtraPlayerConfigScreen extends Screen {

    @Nullable
    private final PlayerModelScreen parentScreen;

    private int guiLeft;
    private int guiTop;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private static final int CONTENT_HEIGHT = 290;

    private final List<ScrollableEntry> entries = new ArrayList<>();

    public ExtraPlayerConfigScreen(@Nullable PlayerModelScreen modelScreen) {
        super(Component.literal("YSM Config GUI"));
        this.parentScreen = modelScreen;
    }

    private record ScrollableEntry(AbstractWidget widget) {}

    public void init() {
        clearWidgets();
        entries.clear();
        this.guiLeft = (this.width - 420) / 2;
        this.guiTop = (this.height - 235) / 2;
        this.maxScroll = Math.max(0, CONTENT_HEIGHT - 235 + 10);

        addEntry(new FlatColorButton(this.guiLeft + 5, this.guiTop - this.scrollOffset + 2, 80, 18, Component.translatable("gui.yes_steve_model.model.return"), button -> {
            getMinecraft().setScreen(this.parentScreen);
        }));
        addEntry(new AbstractSliderButton(this.guiLeft + 5, this.guiTop - this.scrollOffset + 24, 320, 18, Component.translatable("gui.yes_steve_model.config.sound_volume"), GeneralConfig.SOUND_VOLUME.get().doubleValue() / 100.0d) {
            protected void updateMessage() {
            }
            protected void applyValue() {
                GeneralConfig.SOUND_VOLUME.set((double)(int)(this.value * 100));
            }
        });
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 45, "disable_self_model", GeneralConfig.DISABLE_SELF_MODEL));
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 67, "disable_other_model", GeneralConfig.DISABLE_OTHER_MODEL));
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 89, "print_animation_roulette_msg", GeneralConfig.PRINT_ANIMATION_ROULETTE_MSG));
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 111, "disable_self_hands", GeneralConfig.DISABLE_SELF_HANDS));
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 133, "disable_player_render", ExtraPlayerRenderConfig.DISABLE_PLAYER_RENDER));
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 155, "disable_projectile_model", GeneralConfig.DISABLE_PROJECTILE_MODEL));
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 177, "disable_vehicle_model", GeneralConfig.DISABLE_VEHICLE_MODEL));
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 199, "disable_external_first_person_anim", GeneralConfig.DISABLE_EXTERNAL_FP_ANIM));
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 221, "disable_loading_state_screen", LoadingStateConfig.DISABLE_LOADING_STATE_SCREEN));
        addEntry(ConfigCheckBoxForge.create(this.guiLeft + 5, this.guiTop - this.scrollOffset + 243, "use_compatibility_renderer", GeneralConfig.USE_COMPATIBILITY_RENDERER));
        addEntry(new LoadingStateButton(this.guiLeft + 5, this.guiTop - this.scrollOffset + 264));
    }

    private void addEntry(AbstractWidget widget) {
        entries.add(new ScrollableEntry(widget));
        addRenderableWidget(widget);
    }

    private void updateWidgetPositions() {
        int baseY = this.guiTop - this.scrollOffset;
        int i = 0;
        int[] offsets = {2, 24, 45, 67, 89, 111, 133, 155, 177, 199, 221, 243, 264};
        for (ScrollableEntry entry : entries) {
            if (i < offsets.length) {
                entry.widget().setY(baseY + offsets[i]);
            }
            i++;
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scrollOffset = (int) Math.max(0, Math.min(this.scrollOffset - scrollY * 20, this.maxScroll));
        updateWidgetPositions();
        return true;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}