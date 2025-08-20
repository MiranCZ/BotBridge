package io.github.mirancz.botbridge.server.bridge;

import io.netty.channel.Channel;

public interface ClientConnectionBridge {

    void botBridge$setChannel(Channel ch);

}
