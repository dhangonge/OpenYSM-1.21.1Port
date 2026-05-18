package com.elfmcys.yesstevemodel.event;

import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.command.OpenYSMClientCommand;
import com.elfmcys.yesstevemodel.command.RootClientCommand;
import com.elfmcys.yesstevemodel.geckolib3.core.builder.Animation;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public final class ClientSuggestionHelper {

    public static CompletableFuture<Suggestions> suggestModelIds(CommandSourceStack source, SuggestionsBuilder builder) {
        if (source instanceof SharedSuggestionProvider) {
            return SharedSuggestionProvider.suggest(
                    ClientModelManager.getModelAssemblyMap().keySet().stream()
                            .map(CommandRegistry::escapeIfRequired).toList(),
                    builder);
        }
        return Suggestions.empty();
    }

    public static CompletableFuture<Suggestions> suggestAnimationNames(CommandSourceStack source, SuggestionsBuilder builder) {
        if (source instanceof SharedSuggestionProvider) {
            Object2ReferenceMap<String, Animation> map = ClientModelManager.getLocalModelContext()
                    .getAnimationBundle().getMainAnimations();
            HashSet<String> set = Sets.newHashSet();
            set.addAll(map.keySet().stream().map(CommandRegistry::escapeIfRequired).toList());
            set.add("stop");
            return SharedSuggestionProvider.suggest(set, builder);
        }
        return Suggestions.empty();
    }

    public static CompletableFuture<Suggestions> suggestTextureIds(CommandSourceStack source, String modelId, SuggestionsBuilder builder) {
        if (source instanceof SharedSuggestionProvider) {
            if (modelId != null && ClientModelManager.getModelAssemblyMap().containsKey(modelId)) {
                List<String> list2 = ClientModelManager.getModelContext(modelId)
                        .map(context -> context.getAnimationBundle().getTextures().getKeys().stream()
                                .map(CommandRegistry::escapeIfRequired).collect(Collectors.toList()))
                        .orElseGet(Lists::newArrayList);
                list2.add(0, "-");
                return SharedSuggestionProvider.suggest(list2, builder);
            }
        }
        return Suggestions.empty();
    }

    public static void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        OpenYSMClientCommand.registerClientCommands(dispatcher);
        RootClientCommand.registerClientCommands(dispatcher);
    }
}
