package io.github.mirancz.botbridge.server.mixin;

import io.github.mirancz.botbridge.server.bridge.ClientConnectionBridge;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements ClientConnectionBridge {


    @Shadow private Channel channel;

    @Override
    public void botBridge$setChannel(Channel ch) {
        this.channel = ch;
    }
}
