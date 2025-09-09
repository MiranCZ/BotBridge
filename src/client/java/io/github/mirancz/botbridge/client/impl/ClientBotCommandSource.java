package io.github.mirancz.botbridge.client.impl;

import io.github.mirancz.botbridge.api.Bot;
import io.github.mirancz.botbridge.api.control.command.brigadier.BotBridgeCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;

public class ClientBotCommandSource extends ClientCommandSource implements BotBridgeCommandSource {

    private final Bot player;

    public ClientBotCommandSource(Bot player) {
        super(MinecraftClient.getInstance().getNetworkHandler(), MinecraftClient.getInstance());
        this.player = player;
    }

    @Override
    public Bot getBotPlayer() {
        return player;
    }
}
