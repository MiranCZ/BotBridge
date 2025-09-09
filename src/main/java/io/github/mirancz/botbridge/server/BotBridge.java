package io.github.mirancz.botbridge.server;

import com.mojang.brigadier.CommandDispatcher;
import io.github.mirancz.botbridge.api.Bot;
import io.github.mirancz.botbridge.api.control.command.chat.ChatCommandListener;
import io.github.mirancz.botbridge.api.control.Task;
import io.github.mirancz.botbridge.api.control.command.brigadier.BotBridgeCommandSource;
import io.github.mirancz.botbridge.api.control.command.brigadier.BotBridgeCommand;
import io.github.mirancz.botbridge.api.control.command.brigadier.CommandRegister;
import io.github.mirancz.botbridge.api.lifecycle.BotFactory;
import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import io.github.mirancz.botbridge.api.util.Side;
import io.github.mirancz.botbridge.server.commands.RunAsCommand;
import io.github.mirancz.botbridge.server.impl.player.ServerBot;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;


import static net.minecraft.server.command.CommandManager.literal;

public class BotBridge implements ModInitializer {
    static int i = 0;


    private static final BotManager botManager = BotManager.getFor(Side.SERVER);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(new RunAsCommand());


        addTestCommand();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            dispatcher.register(literal("bot").executes(ctx -> {
                ServerPlayerEntity sender = (ServerPlayerEntity) ctx.getSource().getEntity();
                if (sender == null) return 0;

                MinecraftServer server = sender.server;
                Vec3d pos = sender.getPos().add(0,2,0);
                ServerBot player = new ServerBot(botManager, server, server.getOverworld(), pos, "bot"+i);

                player.getSidebar().add(sender);
                // TODO call onDestroyed at some point
                botManager.onCreated(player);
                i++;

                return 1;
            }));
        });


        ServerTickEvents.START_SERVER_TICK.register(a -> botManager.tick());
    }

    private void addTestCommand() {
        BotManager.register(new BotFactory() {
            @Override
            public ChatCommandListener registerChatCommands() {
                return new ChatCommandListener() {
                    @Override
                    public boolean onCommand(String message, Bot bot) {
                        if (message.equals("#test")) {
                            return bot.runTask(new Task(bot) {
                                @Override
                                public void tick() {
                                    player.getInput().jump = true;
                                }
                            });
                        }
                        if (message.equals("#stop")) {
                            return Task.stop(bot);
                        }

                        return false;
                    }
                };
            }


            @Override
            public CommandRegister registerBrigadierCommands() {
                return new CommandRegister() {
                    @Override
                    public void register(CommandDispatcher<BotBridgeCommandSource> dispatcher) {
                        dispatcher.register(BotBridgeCommand.literal("hello").executes((s) -> {

                            System.out.println("HELLO!");
                            return 0;
                        }));
                    }

                    @Override
                    public char getPrefix() {
                        return ';';
                    }
                };
            }
        });
    }


}
