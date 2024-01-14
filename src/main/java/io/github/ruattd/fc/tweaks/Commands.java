package io.github.ruattd.fc.tweaks;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.*;

public final class Commands {
    private static final int MAX_FAKE_PLAYERS = 10;

    private static final HashSet<EntityPlayerMPFake> fakePlayerSet = new HashSet<>();

    static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess ignored1, CommandManager.RegistrationEnvironment ignored2) {
        dispatcher.register(literal("afk")
                .then(literal("spawn")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .then(argument("name", StringArgumentType.word()).executes(context -> afkFakePlayer(context, 1)))
                )
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    fakePlayerSet.forEach(player -> {
                                        var name = player.getEntityName().substring(4);
                                        builder.suggest(name);
                                    });
                                    return builder.buildFuture();
                                })
                                .executes(context -> afkFakePlayer(context, 2))
                        )
                )
                .then(literal("removeAll")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            int count = fakePlayerSet.size();
                            fakePlayerSet.forEach(fakePlayer -> fakePlayer.kill(Text.of("Remove all fake players")));
                            fakePlayerSet.clear();
                            context.getSource().sendMessage(Text.of("Successfully removed " + count + " fake player" + ((count == 1) ? "" : "s")));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .executes(context -> {
                    var source = context.getSource();
                    if (source.getEntity() instanceof ServerPlayerEntity sourcePlayer) {
                        EntityPlayerMPFake.createShadow(source.getServer(), sourcePlayer);
                    } else {
                        source.sendError(Text.of("Only players can shadow themselves"));
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );
    }

    private static int afkFakePlayer(CommandContext<ServerCommandSource> context, int operation) {
        var source = context.getSource();
        if (source.getEntity() instanceof ServerPlayerEntity sourcePlayer) {
            var name = "afk_" + context.getArgument("name", String.class).toLowerCase(Locale.US);
            var server = source.getServer();
            var playerManager = server.getPlayerManager();
            switch (operation) {
                case 1 -> {
                    if (fakePlayerSet.size() <= MAX_FAKE_PLAYERS) {
                        if (playerManager.getPlayer(name) == null) {
                            var fakePlayer = EntityPlayerMPFake.createFake(
                                    name, server, sourcePlayer.getPos(),
                                    sourcePlayer.getYaw(), sourcePlayer.getPitch(),
                                    sourcePlayer.getWorld().getRegistryKey(),
                                    GameMode.CREATIVE, false);
                            fakePlayerSet.add(fakePlayer);
                            source.sendMessage(Text.of("成功生成假玩家"));
                        } else {
                            source.sendError(Text.of("指定的假玩家已存在"));
                        }
                    } else {
                        source.sendError(Text.of("假玩家数量超出上限"));
                    }
                }
                case 2 -> {
                    var target = playerManager.getPlayer(name);
                    if (target == null) {
                        source.sendError(Text.of("玩家不存在"));
                    } else if (target instanceof EntityPlayerMPFake fakePlayer) {
                        fakePlayer.kill(Text.of("Removed by player " + source.getName()));
                        fakePlayerSet.remove(fakePlayer);
                        source.sendMessage(Text.of("成功移除假玩家"));
                    } else {
                        source.sendError(Text.of("你只能移除假玩家"));
                    }
                }
                default -> source.sendError(Text.of("Invalid operation"));
            }
        } else {
            source.sendError(Text.of("You must be a player"));
        }
        return Command.SINGLE_SUCCESS;
    }
}
