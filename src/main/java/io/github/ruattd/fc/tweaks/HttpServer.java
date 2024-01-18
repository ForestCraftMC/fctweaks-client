package io.github.ruattd.fc.tweaks;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class HttpServer {
    static void root(Context context) {
        //TODO website
    }

    static void ping(Context context) {
        var server = ForestCraftTweaks.getServer();
        if (server == null) {
            context.status(HttpStatus.NO_CONTENT);
            context.result("服务器正在启动中");
        } else {
            var info = ServerInfo.of(server);
            var playerCount = "在线玩家: " + info.onlinePlayerCount + '/' + info.maxPlayerCount;
            var bosses = new StringBuilder("进行中的 BOSS 进度: ");
            info.bosses.forEach(boss -> bosses.append("\n  - ").append(boss));
            var result = playerCount + '\n' + bosses;
            context.status(HttpStatus.OK);
            context.result(result);
        }
    }

    record ServerInfo(
            int onlinePlayerCount,
            int maxPlayerCount,
            List<String> bosses
    ) {
        public static ServerInfo of(MinecraftServer server) {
            var online = server.getCurrentPlayerCount();
            var total = 114514;
            var bossBars = server.getBossBarManager().getAll();
            var bossList = new ArrayList<String>(bossBars.size());
            bossBars.forEach(bossBar -> {
                var name = bossBar.getName().getString();
                var value = bossBar.getMaxValue();
                var maxValue = bossBar.getMaxValue();
                var result = name + " (" + value + '/' + maxValue + ')';
                bossList.add(result);
            });
            return new ServerInfo(online, total, bossList);
        }
    }
}
