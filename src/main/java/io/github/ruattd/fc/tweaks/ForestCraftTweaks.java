package io.github.ruattd.fc.tweaks;

import io.javalin.Javalin;
import io.javalin.community.ssl.SSLPlugin;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public final class ForestCraftTweaks implements ModInitializer {
    @Getter
    private static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(CommandAfk::register);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ForestCraftTweaks.server = server);
        var webServer = Javalin.create(config -> {
            var ssl = new SSLPlugin(c -> {
                var cert = "/etc/letsencrypt/live/fc.nijika.in/fullchain.pem";
                var key = "/etc/letsencrypt/live/fc.nijika.in/privkey.pem";
                c.pemFromPath(cert, key);
            });
            config.plugins.register(ssl);
        });
        new Thread(() -> webServer
                .get("/", Http::handle)
                .get("/ping", HttpPing::handle)
                .start(800)
        ).start();
    }
}
