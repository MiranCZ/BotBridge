package io.github.mirancz.botbridge.server.bridge;

import io.github.mirancz.botbridge.api.AbstractPlayer;
import io.github.mirancz.botbridge.server.impl.ServerBotCommandSource;

public interface ServerCommandSourceBridge {

    ServerBotCommandSource botBridge$from(AbstractPlayer bot);

}
