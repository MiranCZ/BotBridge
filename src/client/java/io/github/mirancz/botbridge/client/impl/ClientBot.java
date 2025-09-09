package io.github.mirancz.botbridge.client.impl;

import io.github.mirancz.botbridge.api.Bot;
import io.github.mirancz.botbridge.api.input.BotInput;
import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Functionality for client player is handled through {@link MinecraftClient#getInstance()} calls
 * and its instance should be created only once and reused upon world joining/leaving.
 */
public class ClientBot extends Bot {

    public static ClientBot INSTANCE;
    private static boolean initialized = false;

    public static void init(BotManager manager) {
        if (initialized) {
            throw new IllegalStateException("Already initialized!");
        }

        INSTANCE = new ClientBot(manager);

        initialized = true;
    }



    private final ClientBotInput input;

    private ClientBot(BotManager botManager) {
        super(botManager);
        this.input = new ClientBotInput();

        ClientTickEvents.START_CLIENT_TICK.register(a -> tick());
    }

    @Override
    public BotInput getInput() {
        return input;
    }

    @Override
    public PlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }
}
