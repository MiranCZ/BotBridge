package io.github.mirancz.botbridge.api.lifecycle;

import com.mojang.brigadier.CommandDispatcher;
import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.control.command.chat.ChatCommandListener;
import io.github.mirancz.botbridge.api.control.Task;
import io.github.mirancz.botbridge.api.control.command.brigadier.BotBridgeCommandSource;
import io.github.mirancz.botbridge.api.control.command.brigadier.CommandRegister;
import io.github.mirancz.botbridge.api.util.Side;
import net.minecraft.entity.player.PlayerEntity;

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
    private static final Map<String, AbstractBot> bots = new HashMap<>();

    public static boolean isBot(PlayerEntity player) {
        AbstractBot abstractBot = getBot(player);
        if (abstractBot == null) return false;

        return abstractBot.isOf(player);
    }

    public static BotManager getFor(Side side) {
        return instanceMap.get(side);
    }

    public static AbstractBot getBot(PlayerEntity entity) {
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

    public static boolean onCommand(Side side, String str, AbstractBot player) {
        return instanceMap.get(side).handler.onCommand(str, player);
    }


    private final ChatCommandHandler handler;
    private final Map<AbstractBot, Task> tasks = new HashMap<>();


    private BotManager() {
        this.handler = new ChatCommandHandler(this);
    }

    public void executeTask(Task task, AbstractBot player) {
        if (tasks.containsKey(player)) {
            tasks.get(player).stop();
        }

        tasks.put(player, task);
    }

    public boolean isTaskRunningFor(AbstractBot bot) {
        return tasks.containsKey(bot);
    }

    public Task getPlayerTask(AbstractBot player) {
        return tasks.get(player);
    }

    public void onCreated(AbstractBot player) {
        for (BotFactory factory : factories) {
            factory.onCreated(player);
        }

        bots.put(player.getProfileName(), player);
    }

    public void onDestroyed(AbstractBot player) {
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
        List<AbstractBot> toRemove = new ArrayList<>();

        for (Map.Entry<AbstractBot, Task> entry : tasks.entrySet()) {
            Task task = entry.getValue();

            task.tick();

            if (!task.running()) {
                toRemove.add(entry.getKey());
            }
        }

        for (AbstractBot player : toRemove) {
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
