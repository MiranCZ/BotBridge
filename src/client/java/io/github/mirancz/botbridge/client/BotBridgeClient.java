package io.github.mirancz.botbridge.client;

import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import io.github.mirancz.botbridge.api.util.Side;
import io.github.mirancz.botbridge.client.impl.ClientPlayer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class BotBridgeClient implements ClientModInitializer {

    private static final BotManager botManager = BotManager.getFor(Side.CLIENT);

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(a -> botManager.tick());

        ClientPlayConnectionEvents.JOIN.register((a, b, c) -> {
            botManager.onCreated(ClientPlayer.INSTANCE);
        });


        ClientPlayConnectionEvents.DISCONNECT.register((a, b) -> {
            botManager.onDestroyed(ClientPlayer.INSTANCE);
        });
    }

}
