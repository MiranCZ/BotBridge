package io.github.mirancz.botbridge.api.lifecycle;

import com.mojang.brigadier.CommandDispatcher;
import io.github.mirancz.botbridge.api.AbstractPlayer;
import io.github.mirancz.botbridge.api.control.ChatCommandListener;
import io.github.mirancz.botbridge.api.control.Task;
import io.github.mirancz.botbridge.api.control.command.BotBridgeCommandSource;
import io.github.mirancz.botbridge.api.control.command.CommandRegister;
import io.github.mirancz.botbridge.api.util.Side;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;


public class BotManager {


    private static final Map<Side, BotManager> instanceMap;

    static {
        instanceMap = Map.of(
                Side.CLIENT, new BotManager(),
                Side.SERVER, new BotManager()
        );
    }

    private static final Map<Side, Map<Character, CommandDispatcher<BotBridgeCommandSource>>> dispatchers = new HashMap<>();
    private static final List<BotFactory> factories = new ArrayList<>();
    private static final Map<String, AbstractPlayer> bots = new HashMap<>();

    public static boolean isBot(ServerPlayerEntity player) {
        AbstractPlayer abstractPlayer = getBot(player);
        if (abstractPlayer == null) return false;

        return abstractPlayer.isOf(player);
    }

    public static BotManager getFor(Side side) {
        return instanceMap.get(side);
    }

    public static AbstractPlayer getBot(ServerPlayerEntity entity) {
        return bots.get(entity.getGameProfile().getName());
    }


    public static CommandDispatcher<BotBridgeCommandSource> getDispatcher(Side side, char prefix) {
        var map = dispatchers.get(side);
        if (map == null) return null;

        return map.get(prefix);
    }

    public static void register(BotFactory factory) {
        ChatCommandListener listener = factory.registerChatCommands();

        if (listener != null) {
            instanceMap.get(Side.CLIENT).handler.register(listener);
            instanceMap.get(Side.SERVER).handler.register(listener);
        }

        CommandRegister register = factory.registerBrigadierCommands();

        if (register != null) {
            char prefix = register.getPrefix();
            if (prefix == '/') {
                throw new IllegalArgumentException("Prefix sadly cannot be '/' in the current implementation...");
            }

            registerDispatcher(Side.CLIENT, prefix, register);
            registerDispatcher(Side.SERVER, prefix, register);
        }

        factories.add(factory);
    }

    private static void registerDispatcher(Side side, char prefix, CommandRegister register) {
        Map<Character, CommandDispatcher<BotBridgeCommandSource>> map = dispatchers.computeIfAbsent(side, k -> new HashMap<>());

        var dispatcher = map.computeIfAbsent(prefix, k -> new CommandDispatcher<>());
        register.register(dispatcher);
    }

    public static boolean onCommand(Side side, String str, AbstractPlayer player) {
        return instanceMap.get(side).handler.onCommand(str, player);
    }


    private final CommandHandler handler;
    private final Map<AbstractPlayer, Task> tasks = new HashMap<>();


    private BotManager() {
        this.handler = new CommandHandler(this);
    }


    public CommandHandler getCommandHandler() {
        return handler;
    }


    public void executeTask(Task task, AbstractPlayer player) {
        if (tasks.containsKey(player)) {
            tasks.get(player).stop();
            player.taskEnd();
        }

        tasks.put(player, task);
        player.taskStart();
    }

    public void onCreated(AbstractPlayer player) {
        for (BotFactory factory : factories) {
            factory.onCreated(player);
        }

        bots.put(player.getProfileName(), player);
    }

    public void onDestroyed(AbstractPlayer player) {
        if (tasks.containsKey(player)) {
            tasks.remove(player).stop();
        }

        bots.remove(player.getProfileName());
    }

    /*public void stopTaskFrom(AbstractPlayer player, CommandListener commandListener) {
        Task task = tasks.get(player);
        if (task != null && task.owner == commandListener) {
            task.stop();

            tasks.remove(player);
        }
    }*/

    public void tick() {
        List<AbstractPlayer> toRemove = new ArrayList<>();

        for (Map.Entry<AbstractPlayer, Task> entry : tasks.entrySet()) {
            Task task = entry.getValue();

            task.tick();

            if (!task.running()) {
                toRemove.add(entry.getKey());
            }
        }

        for (AbstractPlayer player : toRemove) {
            tasks.remove(player).stop();
        }
    }

    public void destroyAll() {
        for (Task task : tasks.values()) {
            task.stop();
        }
        tasks.clear();
    }
}
