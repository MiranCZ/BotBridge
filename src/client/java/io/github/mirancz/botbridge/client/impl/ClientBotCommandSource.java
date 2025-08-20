package io.github.mirancz.botbridge.client.impl;

import io.github.mirancz.botbridge.api.AbstractPlayer;
import io.github.mirancz.botbridge.api.control.command.BotBridgeCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class ClientBotCommandSource extends ClientCommandSource implements BotBridgeCommandSource {

    private final AbstractPlayer player;

    public ClientBotCommandSource(AbstractPlayer player) {
        super(MinecraftClient.getInstance().getNetworkHandler(), MinecraftClient.getInstance());
        this.player = player;
    }

    @Override
    public AbstractPlayer getBotPlayer() {
        return player;
    }
}
