package io.github.ruattd.fc.tweaks;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.server.HttpServer;

import java.io.File;

public final class ForestCraftTweaks implements ModInitializer {
    @Getter
    private static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(CommandAfk::register);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ForestCraftTweaks.server = server);
//        var cert = new File("/etc/letsencrypt/live/fc.nijika.in/fullchain.pem");
//        var key = new File("/etc/letsencrypt/live/fc.nijika.in/privkey.pem");
        var httpServer = HttpServer.create();
        Runnable r = () -> httpServer.port(80)
                .route(routes -> {
                    routes.get("/style.css", HttpCss::route);
                    routes.get("/ping", HttpPing::route);
                    routes.get("/", Http::route);
                })
//                .secure(spec -> spec.sslContext(Http11SslContextSpec.forServer(cert, key)))
                .bindNow();
        new Thread(r, "Status server").start();
    }
}
