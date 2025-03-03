package io.github.ruattd.fc.tweaks;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;

public final class ForestCraftTweaks implements ModInitializer {
    @Getter
    private static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        log("Initializing...");
        CommandRegistrationCallback.EVENT.register(CommandAfk::register);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ForestCraftTweaks.server = server);
        log("Tweaks applied");
    }

    public static void log(String message) {
        LogManager.getLogger("ForestCraftTweaks").info(message);
    }
}
