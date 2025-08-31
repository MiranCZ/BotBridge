package io.github.mirancz.botbridge.server.bridge;

import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.server.impl.ServerBotCommandSource;

public interface ServerCommandSourceBridge {

    ServerBotCommandSource botBridge$from(AbstractBot bot);

}
