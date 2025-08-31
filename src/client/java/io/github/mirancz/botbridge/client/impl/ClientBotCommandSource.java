package io.github.mirancz.botbridge.client.impl;

import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.control.command.brigadier.BotBridgeCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;

public class ClientBotCommandSource extends ClientCommandSource implements BotBridgeCommandSource {

    private final AbstractBot player;

    public ClientBotCommandSource(AbstractBot player) {
        super(MinecraftClient.getInstance().getNetworkHandler(), MinecraftClient.getInstance());
        this.player = player;
    }

    @Override
    public AbstractBot getBotPlayer() {
        return player;
    }
}
