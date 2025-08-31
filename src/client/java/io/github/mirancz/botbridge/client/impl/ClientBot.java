package io.github.mirancz.botbridge.client.impl;

import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.AbstractWorld;
import io.github.mirancz.botbridge.api.input.AbstractInput;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Functionality for client player is handled through {@link MinecraftClient#getInstance()} calls
 * and its instance should be created only once and reused upon world joining/leaving.
 */
public class ClientBot extends AbstractBot {

    public static ClientBot INSTANCE = new ClientBot();


    private final ClientWorldImpl clientWorld;
    private final ClientInput input;

    private ClientBot() {
        this.clientWorld = new ClientWorldImpl();
        this.input = new ClientInput();

        ClientTickEvents.START_CLIENT_TICK.register(a -> tick());
    }


    @Override
    public void taskEnd() {
        super.taskEnd();

        input.freeControl();
    }

    @Override
    public AbstractWorld getWorld() {
        return clientWorld;
    }

    @Override
    public AbstractInput getInput() {
        return input;
    }

    @Override
    public PlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }
}
