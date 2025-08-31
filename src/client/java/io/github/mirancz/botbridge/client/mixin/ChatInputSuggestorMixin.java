package io.github.mirancz.botbridge.client.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import io.github.mirancz.botbridge.api.control.command.brigadier.BotBridgeCommandSource;
import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import io.github.mirancz.botbridge.api.util.Side;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin<S> {

    @Unique
    private CommandDispatcher<BotBridgeCommandSource> dispatcher;

    @Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;peek()C"))
    public char handleCustomCommand(StringReader instance) {

        char peeked = instance.peek();

        dispatcher = BotManager.getDispatcher(Side.CLIENT, peeked);
        if (dispatcher != null) {
            return '/'; // act as a command
        }

        return peeked;
    }

    @Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getCommandDispatcher()Lcom/mojang/brigadier/CommandDispatcher;"))
    @SuppressWarnings("rawtypes") // thank you type erasure
    // the dispatcher is given CommandSource without `getBotPlayer()`
    // that shouldn't be a problem but if it turns out to be one you can instead
    // redirect the individual dispatcher calls and provide the custom context
    public CommandDispatcher replaceDispatcher(ClientPlayNetworkHandler instance) {
        if (dispatcher != null) return dispatcher;

        return instance.getCommandDispatcher();
    }

}
