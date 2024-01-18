package io.github.ruattd.fc.tweaks;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.minecraft.server.MinecraftServer;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpPing {
    static Publisher<Void> route(HttpServerRequest ignored, HttpServerResponse response) {
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        var server = ForestCraftTweaks.getServer();
        String returnContent;
        if (server == null) {
            response.status(HttpResponseStatus.NO_CONTENT);
            returnContent = Http.withH5("服务器正在启动中");
        } else {
            var info = ServerInfo.of(server);
            var playerCount = "在线玩家: " + info.onlinePlayerCount + '/' + info.maxPlayerCount;
            var bosses = new StringBuilder("进行中的 BOSS 进度: ");
            if (info.bosses.isEmpty()) {
                bosses.append("无");
            } else {
                info.bosses.forEach(boss -> bosses.append("<br/>  - ").append(boss));
            }
            var result = "Forest Craft 冬季特别版<br/>" + playerCount + "<br/>" + bosses;
            response.status(HttpResponseStatus.OK);
            returnContent = Http.withH5(result);
        }
        return response.sendString(Mono.just(returnContent));
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
