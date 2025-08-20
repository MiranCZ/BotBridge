package io.github.mirancz.botbridge.server;

import com.mojang.brigadier.CommandDispatcher;
import io.github.mirancz.botbridge.api.AbstractPlayer;
import io.github.mirancz.botbridge.api.control.ChatCommandListener;
import io.github.mirancz.botbridge.api.control.Task;
import io.github.mirancz.botbridge.api.control.command.BotBridgeCommandSource;
import io.github.mirancz.botbridge.api.control.command.BridgeCommand;
import io.github.mirancz.botbridge.api.control.command.CommandRegister;
import io.github.mirancz.botbridge.api.lifecycle.BotFactory;
import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import io.github.mirancz.botbridge.api.util.Side;
import io.github.mirancz.botbridge.server.commands.RunAsCommand;
import io.github.mirancz.botbridge.server.impl.player.ServerPlayer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;


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
                ServerPlayer player = new ServerPlayer(server, server.getOverworld(), pos, "bot"+i);
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
                    public @Nullable Task onCommand(String message, AbstractPlayer player) {
                        if (message.equals("#test")) {
                            return new Task(player, this) {
                                @Override
                                public void tick() {
                                    player.getInput().jump = true;
                                }
                            };
                        }
                        if (message.equals("#stop")) {
                            return Task.noop(player, this);
                        }

                        return null;
                    }
                };
            }


            @Override
            public CommandRegister registerBrigadierCommands() {
                return new CommandRegister() {
                    @Override
                    public void register(CommandDispatcher<BotBridgeCommandSource> dispatcher) {
                        dispatcher.register(BridgeCommand.literal("hello").executes((s) -> {

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
