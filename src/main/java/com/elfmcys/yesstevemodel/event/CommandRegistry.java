package com.elfmcys.yesstevemodel.event;

import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.command.RootCommand;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;
import java.util.stream.Collectors;

@EventBusSubscriber
public final class CommandRegistry {

    public static final SuggestionProvider<CommandSourceStack> MODEL_IDS = SuggestionProviders.register(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "models"), (commandContext, suggestionsBuilder) -> {
        if (commandContext.getSource() instanceof SharedSuggestionProvider) {
            if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
                return SharedSuggestionProvider.suggest(ServerModelManager.getServerModelInfo().keySet().stream().map(CommandRegistry::escapeIfRequired).toList(), suggestionsBuilder);
            }
            return ClientSuggestionHelper.suggestModelIds((CommandSourceStack) commandContext.getSource(), suggestionsBuilder);
        }
        return Suggestions.empty();
    });

    public static final SuggestionProvider<CommandSourceStack> ANIMATION_NAMES = SuggestionProviders.register(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "animations"), (commandContext, suggestionsBuilder) -> {
        if (commandContext.getSource() instanceof SharedSuggestionProvider) {
            if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
                return Suggestions.empty();
            }
            return ClientSuggestionHelper.suggestAnimationNames((CommandSourceStack)commandContext.getSource(), suggestionsBuilder);
        }
        return Suggestions.empty();
    });

    public static final SuggestionProvider<CommandSourceStack> TEXTURE_IDS = SuggestionProviders.register(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "textures"), (commandContext, suggestionsBuilder) -> {
        if (commandContext.getSource() instanceof SharedSuggestionProvider) {
            String str = commandContext.getArgument("model_id", String.class);
            if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
                if (ServerModelManager.getServerModelInfo().containsKey(str)) {
                    List<String> list = ServerModelManager.getServerModelInfo().get(str).getModelInfo().getTextures().stream().map(CommandRegistry::escapeIfRequired).collect(Collectors.toList());
                    list.add(0, "-");
                    return SharedSuggestionProvider.suggest(list, suggestionsBuilder);
                }
            } else {
                return ClientSuggestionHelper.suggestTextureIds((CommandSourceStack)commandContext.getSource(), str, suggestionsBuilder);
            }
        }
        return Suggestions.empty();
    });

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        if (!YesSteveModel.isAvailable()) {
            RootCommand.registerFallbackCommands(event.getDispatcher());
            return;
        }
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSuggestionHelper.registerClientCommands(event.getDispatcher());
        }
        RootCommand.registerCommands(event.getDispatcher());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRegisterCommand(RegisterClientCommandsEvent event) {
        ClientSuggestionHelper.registerClientCommands(event.getDispatcher());
    }

    public static String escapeIfRequired(String str) {
        if (str.chars().allMatch(i -> StringReader.isAllowedInUnquotedString((char) i))) {
            return str;
        }
        return String.format("\"%s\"", str.replace("\"", "\\\"").replace("'", "\\'"));
    }
}
